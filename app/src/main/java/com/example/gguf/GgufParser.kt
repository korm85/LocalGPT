package com.example.gguf

import android.content.Context
import android.net.Uri
import android.util.Log
import java.io.BufferedInputStream
import java.io.EOFException
import java.io.InputStream
import kotlin.math.min

/**
 * Highly technical and precise GGUF (GPT-Generated Unified Format) binary parser
 * written in pure Kotlin to inspect neural network model headers on-device.
 */
object GgufParser {
    private const val TAG = "GgufParser"
    private const val GGUF_MAGIC = "GGUF"
    
    // Limits the parser to a reasonable size to prevent reading the entire multi-GB file
    private const val MAX_METADATA_READ_BYTES = 5 * 1024 * 1024 // 5 MB

    data class GgufMetadata(
        val magic: String,
        val version: Int,
        val tensorCount: Long,
        val kvCount: Long,
        val properties: Map<String, Any>,
        val architecture: String?,
        val modelName: String?,
        val contextLength: Long?,
        val embeddingLength: Long?,
        val blockCount: Long?,
        val headCount: Long?,
        val quantizationType: String?,
        val fileBytesSize: Long
    )

    fun parse(context: Context, uri: Uri): GgufMetadata {
        val contentResolver = context.contentResolver
        var fileLength: Long = -1
        try {
            contentResolver.openAssetFileDescriptor(uri, "r")?.use { afd ->
                fileLength = afd.length
            }
        } catch (e: Exception) {
            Log.w(TAG, "Could not determine file length via AssetFileDescriptor", e)
        }

        val inputStream = contentResolver.openInputStream(uri) ?: throw IllegalArgumentException("Could not open stream for URI: $uri")
        val bufferedStream = BufferedInputStream(inputStream)
        
        // Wrap with a stream that prevents reading the whole file to safeguard memory
        val limitedStream = object : InputStream() {
            private var bytesRead = 0L
            override fun read(): Int {
                if (bytesRead >= MAX_METADATA_READ_BYTES) return -1
                val b = bufferedStream.read()
                if (b != -1) bytesRead++
                return b
            }

            override fun read(b: ByteArray, off: Int, len: Int): Int {
                if (bytesRead >= MAX_METADATA_READ_BYTES) return -1
                val maxToRead = min(len.toLong(), MAX_METADATA_READ_BYTES - bytesRead).toInt()
                val r = bufferedStream.read(b, off, maxToRead)
                if (r != -1) bytesRead += r
                return r
            }

            override fun close() {
                bufferedStream.close()
                inputStream.close()
            }
        }

        limitedStream.use { stream ->
            val reader = LittleEndianReader(stream)
            
            // 1. Parse Magic Bytes
            val magicBytes = ByteArray(4)
            val bytesRead = stream.read(magicBytes)
            if (bytesRead < 4) throw EOFException("Could not read magic bytes")
            val magic = String(magicBytes, Charsets.UTF_8)
            if (magic != GGUF_MAGIC) {
                throw IllegalArgumentException("Invalid GGUF format. Expected 'GGUF' magic, found '$magic'")
            }

            // 2. Version
            val version = reader.readInt32()
            if (version < 1 || version > 3) {
                throw IllegalArgumentException("Unsupported GGUF version: $version")
            }

            // 3. Tensor Count
            val tensorCount = reader.readInt64()

            // 4. Metadata KV Count
            val kvCount = reader.readInt64()

            val properties = mutableMapOf<String, Any>()

            // 5. Parse KV Pairs
            for (i in 0 until minOf(kvCount, 250L)) { // Safely cap key reading count
                try {
                    val key = reader.readString()
                    val typeId = reader.readInt32()
                    val value = readValue(typeId, reader)
                    properties[key] = value
                } catch (e: Exception) {
                    Log.e(TAG, "Error reading metadata key-value pair #$i", e)
                    break
                }
            }

            // Infer specific model details from standard GGUF properties
            val architecture = properties["general.architecture"] as? String ?: "llama"
            val modelName = properties["general.name"] as? String 
                ?: properties["general.basename"] as? String
                ?: uri.lastPathSegment?.substringBeforeLast(".")
                ?: "Unknown Model"
            
            val contextLength = getLongProperty(properties, "$architecture.context_length")
                ?: getLongProperty(properties, "llama.context_length")
                ?: getLongProperty(properties, "general.context_length")
            
            val embeddingLength = getLongProperty(properties, "$architecture.embedding_length")
                ?: getLongProperty(properties, "llama.embedding_length")
                ?: getLongProperty(properties, "general.embedding_length")

            val blockCount = getLongProperty(properties, "$architecture.block_count")
                ?: getLongProperty(properties, "llama.block_count")

            val headCount = getLongProperty(properties, "$architecture.attention.head_count")
                ?: getLongProperty(properties, "llama.attention.head_count")

            // Guess quantization type from filename or properties
            var quantization = properties["general.file_type_string"] as? String
            if (quantization == null) {
                val fileType = getLongProperty(properties, "general.file_type")
                quantization = when (fileType) {
                    0L -> "ALL_F32"
                    1L -> "MOSTLY_F16"
                    2L -> "MOSTLY_Q4_0"
                    3L -> "MOSTLY_Q4_1"
                    8L -> "MOSTLY_Q8_0"
                    9L -> "MOSTLY_Q5_0"
                    10L -> "MOSTLY_Q5_1"
                    12L -> "MOSTLY_Q2_K"
                    14L -> "MOSTLY_Q3_K"
                    15L -> "MOSTLY_Q4_K"
                    16L -> "MOSTLY_Q5_K"
                    17L -> "MOSTLY_Q6_K"
                    else -> null
                }
            }
            if (quantization == null) {
                val nameLower = uri.lastPathSegment?.lowercase() ?: ""
                quantization = when {
                    nameLower.contains("q2_0") -> "Q2_0 (2-bit Ternary Quantized)"
                    nameLower.contains("q2_k") -> "Q2_K (2-bit Quantized)"
                    nameLower.contains("q4_k_m") -> "Q4_K_M (4-bit Medium Quantized)"
                    nameLower.contains("q4_0") -> "Q4_0 (4-bit Fast Quantized)"
                    nameLower.contains("q5_k_m") -> "Q5_K_M (5-bit Medium Quantized)"
                    nameLower.contains("q8_0") -> "Q8_0 (8-bit Quantized)"
                    nameLower.contains("f16") -> "Float16 (Uncompressed)"
                    else -> "Unknown Quantization"
                }
            }

            return GgufMetadata(
                magic = magic,
                version = version,
                tensorCount = tensorCount,
                kvCount = kvCount,
                properties = properties,
                architecture = architecture,
                modelName = modelName,
                contextLength = contextLength,
                embeddingLength = embeddingLength,
                blockCount = blockCount,
                headCount = headCount,
                quantizationType = quantization,
                fileBytesSize = if (fileLength > 0) fileLength else 0L
            )
        }
    }

    private fun getLongProperty(properties: Map<String, Any>, key: String): Long? {
        val value = properties[key] ?: return null
        if (value is Number) return value.toLong()
        return null
    }

    private fun readValue(typeId: Int, reader: LittleEndianReader): Any {
        return when (typeId) {
            0 -> reader.readByte() // uint8
            1 -> reader.readByte() // int8
            2 -> reader.readInt32() and 0xFFFF // uint16
            3 -> reader.readInt32().toShort() // int16
            4 -> reader.readInt32().toLong() and 0xFFFFFFFFL // uint32
            5 -> reader.readInt32() // int32
            6 -> reader.readFloat32() // float32
            7 -> reader.readByte().toInt() != 0 // bool
            8 -> reader.readString() // string
            9 -> { // array
                val itemTypeId = reader.readInt32()
                val arrayLen = reader.readInt64().toInt()
                val list = mutableListOf<Any>()
                val safeLen = minOf(arrayLen, 25) // read maximum 25 elements for display safety
                for (i in 0 until safeLen) {
                    list.add(readValue(itemTypeId, reader))
                }
                if (arrayLen > safeLen) {
                    // Skip remaining array elements
                    for (i in safeLen until arrayLen) {
                        skipValue(itemTypeId, reader)
                    }
                }
                list
            }
            10 -> reader.readInt64() // uint64
            11 -> reader.readInt64() // int64
            12 -> reader.readFloat64() // float64
            else -> "UnknownType($typeId)"
        }
    }

    private fun skipValue(typeId: Int, reader: LittleEndianReader) {
        when (typeId) {
            0, 1, 7 -> reader.readByte()
            2, 3 -> { reader.readByte(); reader.readByte() }
            4, 5, 6 -> reader.readInt32()
            8 -> {
                val len = reader.readInt64().toInt()
                for (i in 0 until len) reader.readByte()
            }
            9 -> {
                val itemTypeId = reader.readInt32()
                val arrayLen = reader.readInt64().toInt()
                for (i in 0 until arrayLen) {
                    skipValue(itemTypeId, reader)
                }
            }
            10, 11, 12 -> reader.readInt64()
        }
    }

    class LittleEndianReader(private val stream: InputStream) {
        fun readByte(): Byte {
            val b = stream.read()
            if (b == -1) throw EOFException("Unexpected EOF")
            return b.toByte()
        }

        fun readInt32(): Int {
            val b0 = stream.read()
            val b1 = stream.read()
            val b2 = stream.read()
            val b3 = stream.read()
            if (b0 == -1 || b1 == -1 || b2 == -1 || b3 == -1) throw EOFException("Unexpected EOF")
            return (b0 and 0xFF) or ((b1 and 0xFF) shl 8) or ((b2 and 0xFF) shl 16) or ((b3 and 0xFF) shl 24)
        }

        fun readInt64(): Long {
            val b = ByteArray(8)
            var total = 0
            while (total < 8) {
                val r = stream.read(b, total, 8 - total)
                if (r == -1) throw EOFException("Unexpected EOF")
                total += r
            }
            var value: Long = 0
            for (i in 0..7) {
                value = value or ((b[i].toLong() and 0xFF) shl (i * 8))
            }
            return value
        }

        fun readFloat32(): Float {
            return Float.fromBits(readInt32())
        }

        fun readFloat64(): Double {
            return Double.fromBits(readInt64())
        }

        fun readString(): String {
            val len = readInt64().toInt()
            if (len < 0 || len > 2 * 1024 * 1024) throw IllegalArgumentException("Extremely large string size in GGUF metadata: $len bytes. Likely corrupted header.")
            val b = ByteArray(len)
            var totalRead = 0
            while (totalRead < len) {
                val r = stream.read(b, totalRead, len - totalRead)
                if (r == -1) throw EOFException("Unexpected EOF while reading string of length $len")
                totalRead += r
            }
            return String(b, Charsets.UTF_8)
        }
    }
}
