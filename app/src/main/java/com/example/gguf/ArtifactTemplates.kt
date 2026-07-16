package com.example.gguf

import com.example.model.Artifact

object ArtifactTemplates {

    fun matchPrompt(prompt: String): Artifact? {
        val query = prompt.lowercase()
        return when {
            query.contains("flappy") || query.contains("bird") -> getFlappyBird()
            query.contains("snake") -> getSnake()
            query.contains("tic") || query.contains("toe") || query.contains("tictac") -> getTicTacToe()
            query.contains("calc") || query.contains("math") -> getCalculator()
            query.contains("todo") || query.contains("task") -> getTodoApp()
            query.contains("weather") || query.contains("forecast") || query.contains("temp") -> getWeatherDashboard()
            query.contains("finance") || query.contains("expense") || query.contains("budget") -> getFinanceTracker()
            query.contains("memory") || query.contains("match") || query.contains("card") -> getMemoryGame()
            query.contains("brick") || query.contains("breaker") -> getBrickBreaker()
            query.contains("reaction") || query.contains("reflex") || query.contains("speed") -> getReactionGame()
            else -> null
        }
    }

    private fun getFlappyBird() = Artifact(
        id = "flappy_bird",
        title = "Retro Flappy Bird Arcade",
        type = "game",
        iconName = "sports_esports",
        description = "A high-performance HTML Canvas game with real-time physics, pipe generators, score counts, and fluid touch jump inputs.",
        code = """
<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
    <style>
        body {
            margin: 0;
            padding: 0;
            background: #121214;
            color: #fff;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            height: 100vh;
            overflow: hidden;
            user-select: none;
        }
        #gameContainer {
            position: relative;
            box-shadow: 0 10px 30px rgba(0,0,0,0.5);
            border-radius: 12px;
            overflow: hidden;
            background: #70c5ce;
            width: 100%;
            max-width: 400px;
            aspect-ratio: 9/16;
        }
        canvas {
            display: block;
            width: 100%;
            height: 100%;
        }
        .overlay {
            position: absolute;
            top: 0; left: 0; right: 0; bottom: 0;
            background: rgba(18, 18, 20, 0.85);
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            text-align: center;
            padding: 20px;
            transition: opacity 0.3s;
        }
        h1 {
            color: #4ef0a0;
            font-size: 2.2rem;
            margin: 0 0 10px 0;
            text-transform: uppercase;
            letter-spacing: 2px;
            text-shadow: 0 4px 10px rgba(78,240,160,0.3);
        }
        p {
            color: #ccc;
            font-size: 1rem;
            margin: 0 0 20px 0;
        }
        button {
            background: linear-gradient(135deg, #10b981, #059669);
            color: white;
            border: none;
            padding: 12px 30px;
            font-size: 1.1rem;
            font-weight: bold;
            border-radius: 30px;
            cursor: pointer;
            box-shadow: 0 5px 15px rgba(16,185,129,0.4);
            transform: scale(1);
            transition: transform 0.1s, box-shadow 0.1s;
        }
        button:active {
            transform: scale(0.95);
            box-shadow: 0 2px 5px rgba(16,185,129,0.4);
        }
        #scoreDisplay {
            position: absolute;
            top: 20px;
            left: 0;
            right: 0;
            text-align: center;
            font-size: 2.5rem;
            font-weight: 900;
            color: #fff;
            text-shadow: 0 4px 6px rgba(0,0,0,0.4);
            pointer-events: none;
            z-index: 10;
        }
    </style>
</head>
<body>
    <div id="gameContainer">
        <div id="scoreDisplay">0</div>
        <canvas id="gameCanvas" width="360" height="640"></canvas>
        
        <div id="menuOverlay" class="overlay">
            <h1>Bonsai Bird</h1>
            <p>Tap anywhere to flap & steer through the gaps!</p>
            <button id="startBtn">PLAY ARCADE</button>
        </div>
        
        <div id="gameOverOverlay" class="overlay" style="display: none;">
            <h1 style="color: #f87171; text-shadow: 0 4px 10px rgba(248,113,113,0.3);">CRASHED!</h1>
            <p id="finalScoreText">You scored: 0</p>
            <p id="highScoreText" style="color: #fbbf24; font-weight: bold;">High Score: 0</p>
            <button id="restartBtn">TRY AGAIN</button>
        </div>
    </div>

    <script>
        const canvas = document.getElementById("gameCanvas");
        const ctx = canvas.getContext("2d");
        const scoreDisplay = document.getElementById("scoreDisplay");
        const menuOverlay = document.getElementById("menuOverlay");
        const gameOverOverlay = document.getElementById("gameOverOverlay");
        const startBtn = document.getElementById("startBtn");
        const restartBtn = document.getElementById("restartBtn");
        const finalScoreText = document.getElementById("finalScoreText");
        const highScoreText = document.getElementById("highScoreText");

        let gameState = "MENU"; // MENU, PLAYING, GAMEOVER
        let score = 0;
        let highScore = parseInt(localStorage.getItem("flappy_high") || "0");

        // Physics parameters
        const gravity = 0.28;
        const lift = -6.5;

        // Bird definition
        const bird = {
            x: 70,
            y: 250,
            radius: 14,
            velocity: 0,
            flap() {
                this.velocity = lift;
                particles.push(new FlapParticle(this.x, this.y));
            },
            draw() {
                // Draw cute bird
                ctx.save();
                ctx.translate(this.x, this.y);
                // Rotate based on velocity
                let angle = Math.min(Math.PI / 4, Math.max(-Math.PI / 7, this.velocity * 0.06));
                ctx.rotate(angle);
                
                // Bird body
                ctx.beginPath();
                ctx.arc(0, 0, this.radius, 0, Math.PI * 2);
                ctx.fillStyle = "#f59e0b"; // Golden yellow
                ctx.fill();
                ctx.strokeStyle = "#b45309";
                ctx.lineWidth = 2;
                ctx.stroke();

                // Eye
                ctx.beginPath();
                ctx.arc(5, -4, 3, 0, Math.PI * 2);
                ctx.fillStyle = "#fff";
                ctx.fill();
                ctx.beginPath();
                ctx.arc(6, -4, 1.2, 0, Math.PI * 2);
                ctx.fillStyle = "#000";
                ctx.fill();

                // Beak
                ctx.beginPath();
                ctx.moveTo(this.radius - 2, -2);
                ctx.lineTo(this.radius + 6, 2);
                ctx.lineTo(this.radius - 2, 6);
                ctx.fillStyle = "#ef4444";
                ctx.fill();

                // Wing
                ctx.beginPath();
                ctx.ellipse(-6, 2, 8, 4, Math.PI/12, 0, Math.PI * 2);
                ctx.fillStyle = "#d97706";
                ctx.fill();

                ctx.restore();
            },
            update() {
                this.velocity += gravity;
                this.y += this.velocity;
                
                // Boundaries
                if (this.y + this.radius > canvas.height - 40) {
                    this.y = canvas.height - 40 - this.radius;
                    endGame();
                }
                if (this.y - this.radius < 0) {
                    this.y = this.radius;
                    this.velocity = 0.5;
                }
            }
        };

        // Obstacles (Pipes)
        let pipes = [];
        const pipeWidth = 52;
        const pipeGap = 135;
        const pipeSpeed = 2.2;
        let pipeTimer = 0;

        class Pipe {
            constructor() {
                this.x = canvas.width;
                this.topHeight = Math.random() * (canvas.height - pipeGap - 140) + 40;
                this.bottomY = this.topHeight + pipeGap;
                this.bottomHeight = canvas.height - this.bottomY - 40;
                this.passed = false;
            }
            draw() {
                ctx.save();
                // Top pipe
                let topGrad = ctx.createLinearGradient(this.x, 0, this.x + pipeWidth, 0);
                topGrad.addColorStop(0, "#10b981");
                topGrad.addColorStop(0.3, "#34d399");
                topGrad.addColorStop(1, "#047857");
                
                ctx.fillStyle = topGrad;
                ctx.fillRect(this.x, 0, pipeWidth, this.topHeight);
                // Lip
                ctx.strokeStyle = "#064e3b";
                ctx.lineWidth = 2;
                ctx.strokeRect(this.x, 0, pipeWidth, this.topHeight);
                ctx.fillRect(this.x - 3, this.topHeight - 16, pipeWidth + 6, 16);
                ctx.strokeRect(this.x - 3, this.topHeight - 16, pipeWidth + 6, 16);

                // Bottom pipe
                let btmGrad = ctx.createLinearGradient(this.x, this.bottomY, this.x + pipeWidth, this.bottomY);
                btmGrad.addColorStop(0, "#10b981");
                btmGrad.addColorStop(0.3, "#34d399");
                btmGrad.addColorStop(1, "#047857");
                ctx.fillStyle = btmGrad;
                ctx.fillRect(this.x, this.bottomY, pipeWidth, this.bottomHeight);
                // Lip
                ctx.fillRect(this.x - 3, this.bottomY, pipeWidth + 6, 16);
                ctx.strokeRect(this.x, this.bottomY, pipeWidth, this.bottomHeight);
                ctx.strokeRect(this.x - 3, this.bottomY, pipeWidth + 6, 16);

                ctx.restore();
            }
            update() {
                this.x -= pipeSpeed;
            }
            collides(b) {
                // Collide with top
                if (b.x + b.radius > this.x && b.x - b.radius < this.x + pipeWidth) {
                    if (b.y - b.radius < this.topHeight || b.y + b.radius > this.bottomY) {
                        return true;
                    }
                }
                return false;
            }
        }

        // Particle System
        let particles = [];
        class FlapParticle {
            constructor(x, y) {
                this.x = x - 10;
                this.y = y;
                this.vx = -Math.random() * 2 - 1;
                this.vy = (Math.random() - 0.5) * 2;
                this.radius = Math.random() * 4 + 2;
                this.alpha = 1;
            }
            draw() {
                ctx.save();
                ctx.globalAlpha = this.alpha;
                ctx.beginPath();
                ctx.arc(this.x, this.y, this.radius, 0, Math.PI * 2);
                ctx.fillStyle = "rgba(255,255,255,0.7)";
                ctx.fill();
                ctx.restore();
            }
            update() {
                this.x += this.vx;
                this.y += this.vy;
                this.alpha -= 0.05;
            }
        }

        // Clouds & Scenery
        let clouds = [];
        class Cloud {
            constructor() {
                this.x = Math.random() * canvas.width + canvas.width;
                this.y = Math.random() * 150 + 20;
                this.speed = Math.random() * 0.4 + 0.1;
                this.size = Math.random() * 20 + 20;
            }
            draw() {
                ctx.save();
                ctx.fillStyle = "rgba(255, 255, 255, 0.45)";
                ctx.beginPath();
                ctx.arc(this.x, this.y, this.size, 0, Math.PI * 2);
                ctx.arc(this.x + this.size * 0.6, this.y - this.size * 0.3, this.size * 0.8, 0, Math.PI * 2);
                ctx.arc(this.x + this.size * 1.2, this.y, this.size * 0.7, 0, Math.PI * 2);
                ctx.fill();
                ctx.restore();
            }
            update() {
                this.x -= this.speed;
                if (this.x + this.size * 2 < 0) {
                    this.x = canvas.width + 50;
                    this.y = Math.random() * 150 + 20;
                }
            }
        }

        for (let i = 0; i < 4; i++) {
            let c = new Cloud();
            c.x = Math.random() * canvas.width;
            clouds.push(c);
        }

        function resetGame() {
            score = 0;
            scoreDisplay.textContent = score;
            bird.y = 250;
            bird.velocity = 0;
            pipes = [];
            particles = [];
            pipeTimer = 0;
        }

        function startGame() {
            resetGame();
            gameState = "PLAYING";
            menuOverlay.style.display = "none";
            gameOverOverlay.style.display = "none";
        }

        function endGame() {
            gameState = "GAMEOVER";
            if (score > highScore) {
                highScore = score;
                localStorage.setItem("flappy_high", highScore.toString());
            }
            finalScoreText.textContent = "You scored: " + score;
            highScoreText.textContent = "High Score: " + highScore;
            gameOverOverlay.style.display = "flex";
        }

        // Tap Handlers
        function handleAction(e) {
            if (e) e.preventDefault();
            if (gameState === "PLAYING") {
                bird.flap();
            }
        }

        window.addEventListener("touchstart", handleAction, {passive: false});
        window.addEventListener("mousedown", (e) => {
            if (e.target.tagName !== "BUTTON") {
                handleAction(e);
            }
        });

        startBtn.addEventListener("click", (e) => { e.stopPropagation(); startGame(); });
        restartBtn.addEventListener("click", (e) => { e.stopPropagation(); startGame(); });

        // Main Loop
        function loop() {
            ctx.clearRect(0,0, canvas.width, canvas.height);

            // Draw Background sky gradient
            let skyGrad = ctx.createLinearGradient(0,0,0,canvas.height);
            skyGrad.addColorStop(0, "#4895ef");
            skyGrad.addColorStop(0.6, "#4cc9f0");
            skyGrad.addColorStop(1, "#3f37c9");
            ctx.fillStyle = skyGrad;
            ctx.fillRect(0,0, canvas.width, canvas.height);

            // Scenery
            clouds.forEach(c => {
                if (gameState === "PLAYING") c.update();
                c.draw();
            });

            // Green hills at the bottom
            ctx.fillStyle = "#8ac926";
            ctx.fillRect(0, canvas.height - 40, canvas.width, 40);
            ctx.fillStyle = "#38b000";
            ctx.fillRect(0, canvas.height - 40, canvas.width, 4);

            if (gameState === "PLAYING") {
                bird.update();
                
                // Add obstacles
                pipeTimer++;
                if (pipeTimer > 100) {
                    pipes.push(new Pipe());
                    pipeTimer = 0;
                }

                // Update pipes
                for (let i = pipes.length - 1; i >= 0; i--) {
                    pipes[i].update();
                    if (pipes[i].collides(bird)) {
                        endGame();
                    }
                    if (!pipes[i].passed && pipes[i].x < bird.x) {
                        pipes[i].passed = true;
                        score++;
                        scoreDisplay.textContent = score;
                    }
                    if (pipes[i].x + pipeWidth < 0) {
                        pipes.splice(i, 1);
                    }
                }

                // Particles
                for (let i = particles.length - 1; i >= 0; i--) {
                    particles[i].update();
                    if (particles[i].alpha <= 0) {
                        particles.splice(i, 1);
                    }
                }
            }

            // Draw Pipes
            pipes.forEach(p => p.draw());

            // Draw particles
            particles.forEach(p => p.draw());

            // Draw Bird
            bird.draw();

            requestAnimationFrame(loop);
        }

        loop();
    </script>
</body>
</html>
        """
    )

    private fun getSnake() = Artifact(
        id = "classic_snake",
        title = "Neon Classic Snake",
        type = "game",
        iconName = "grid_3x3",
        description = "Sleek retro console style Snake game with a custom grid canvas, scoring metrics, adjustable speed, and on-screen tactile D-Pad controllers.",
        code = """
<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
    <style>
        body {
            margin: 0;
            padding: 0;
            background: #09090b;
            color: #fff;
            font-family: 'Segoe UI', system-ui, sans-serif;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            height: 100vh;
            overflow: hidden;
            user-select: none;
        }
        #gameArea {
            display: flex;
            flex-direction: column;
            align-items: center;
            width: 100%;
            max-width: 360px;
            padding: 12px;
            box-sizing: border-box;
        }
        .header {
            display: flex;
            justify-content: space-between;
            width: 100%;
            margin-bottom: 10px;
            background: #18181b;
            padding: 10px 16px;
            border-radius: 8px;
            box-sizing: border-box;
            border: 1px solid #27272a;
        }
        .score-box {
            font-size: 0.95rem;
            color: #a1a1aa;
        }
        .score-val {
            font-size: 1.15rem;
            font-weight: bold;
            color: #10b981;
        }
        #canvasContainer {
            position: relative;
            background: #18181b;
            border-radius: 12px;
            overflow: hidden;
            border: 2px solid #10b981;
            box-shadow: 0 0 15px rgba(16,185,129,0.15);
            width: 100%;
            aspect-ratio: 1;
        }
        canvas {
            display: block;
            width: 100%;
            height: 100%;
        }
        .overlay {
            position: absolute;
            top:0; left:0; right:0; bottom:0;
            background: rgba(9, 9, 11, 0.9);
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            text-align: center;
            padding: 20px;
        }
        h1 {
            color: #10b981;
            margin: 0 0 8px 0;
            font-size: 1.8rem;
            letter-spacing: 1px;
            text-shadow: 0 0 10px rgba(16,185,129,0.3);
        }
        p { color: #a1a1aa; font-size: 0.9rem; margin: 0 0 16px 0; }
        button {
            background: #10b981;
            color: #000;
            border: none;
            padding: 10px 24px;
            font-size: 1rem;
            font-weight: bold;
            border-radius: 6px;
            cursor: pointer;
            box-shadow: 0 4px 10px rgba(16,185,129,0.3);
        }
        button:active { transform: scale(0.96); }
        
        /* Dpad styles */
        #dpad {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 8px;
            width: 160px;
            margin-top: 15px;
        }
        .dbtn {
            background: #27272a;
            border: 1px solid #3f3f46;
            color: #e4e4e7;
            height: 48dp;
            border-radius: 8px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 1.25rem;
            font-weight: bold;
            cursor: pointer;
            touch-action: manipulation;
        }
        .dbtn:active {
            background: #10b981;
            color: #000;
        }
    </style>
</head>
<body>
    <div id="gameArea">
        <div class="header">
            <div class="score-box">SCORE: <span id="score" class="score-val">0</span></div>
            <div class="score-box">BEST: <span id="highScore" class="score-val" style="color:#fbbf24;">0</span></div>
        </div>
        
        <div id="canvasContainer">
            <canvas id="gameCanvas" width="300" height="300"></canvas>
            
            <div id="startOverlay" class="overlay">
                <h1>NEON SNAKE</h1>
                <p>Use the D-pad below or swipe to slide!</p>
                <button id="startBtn">START GAME</button>
            </div>
            
            <div id="gameOverOverlay" class="overlay" style="display: none;">
                <h1 style="color: #ef4444; text-shadow:0 0 10px rgba(239,68,68,0.3);">GAME OVER</h1>
                <p id="finalScore">Score reached: 0</p>
                <button id="restartBtn">RESTART</button>
            </div>
        </div>
        
        <div id="dpad">
            <div></div>
            <div class="dbtn" id="btnUp">▲</div>
            <div></div>
            <div class="dbtn" id="btnLeft">◀</div>
            <div style="background:#18181b; border-radius:8px;"></div>
            <div class="dbtn" id="btnRight">▶</div>
            <div></div>
            <div class="dbtn" id="btnDown">▼</div>
            <div></div>
        </div>
    </div>

    <script>
        const canvas = document.getElementById("gameCanvas");
        const ctx = canvas.getContext("2d");
        const scoreText = document.getElementById("score");
        const highScoreText = document.getElementById("highScore");
        const startOverlay = document.getElementById("startOverlay");
        const gameOverOverlay = document.getElementById("gameOverOverlay");
        const finalScore = document.getElementById("finalScore");

        const grid = 15;
        let score = 0;
        let bestScore = parseInt(localStorage.getItem("snake_best") || "0");
        highScoreText.textContent = bestScore;

        let snake = [];
        let dx = grid;
        let dy = 0;
        let apple = { x: 0, y: 0 };
        let gameLoopTimer = null;
        let state = "MENU"; // MENU, PLAYING, DEAD

        function spawnApple() {
            apple.x = Math.floor(Math.random() * (canvas.width / grid)) * grid;
            apple.y = Math.floor(Math.random() * (canvas.height / grid)) * grid;
            
            // Avoid spawning on snake
            for (let cell of snake) {
                if (cell.x === apple.x && cell.y === apple.y) {
                    spawnApple();
                    break;
                }
            }
        }

        function initGame() {
            score = 0;
            scoreText.textContent = score;
            dx = grid;
            dy = 0;
            snake = [
                { x: grid * 5, y: grid * 10 },
                { x: grid * 4, y: grid * 10 },
                { x: grid * 3, y: grid * 10 }
            ];
            spawnApple();
            state = "PLAYING";
            startOverlay.style.display = "none";
            gameOverOverlay.style.display = "none";

            if (gameLoopTimer) clearInterval(gameLoopTimer);
            gameLoopTimer = setInterval(tick, 110);
        }

        function handleDirectionChange(newDx, newDy) {
            if (state !== "PLAYING") return;
            // Prevent 180 degree turns
            if (newDx !== 0 && dx === -newDx) return;
            if (newDy !== 0 && dy === -newDy) return;
            dx = newDx;
            dy = newDy;
        }

        // Click Dpad listeners
        document.getElementById("btnUp").addEventListener("click", () => handleDirectionChange(0, -grid));
        document.getElementById("btnDown").addEventListener("click", () => handleDirectionChange(0, grid));
        document.getElementById("btnLeft").addEventListener("click", () => handleDirectionChange(-grid, 0));
        document.getElementById("btnRight").addEventListener("click", () => handleDirectionChange(grid, 0));

        // Keyboard listeners for desktop support
        document.addEventListener("keydown", (e) => {
            if (e.key === "ArrowUp") handleDirectionChange(0, -grid);
            if (e.key === "ArrowDown") handleDirectionChange(0, grid);
            if (e.key === "ArrowLeft") handleDirectionChange(-grid, 0);
            if (e.key === "ArrowRight") handleDirectionChange(grid, 0);
        });

        // Swipe Gesture support
        let touchStartX = 0;
        let touchStartY = 0;
        canvas.addEventListener("touchstart", (e) => {
            touchStartX = e.touches[0].clientX;
            touchStartY = e.touches[0].clientY;
        }, { passive: true });

        canvas.addEventListener("touchend", (e) => {
            const diffX = e.changedTouches[0].clientX - touchStartX;
            const diffY = e.changedTouches[0].clientY - touchStartY;
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > 25) {
                    if (diffX > 0) handleDirectionChange(grid, 0);
                    else handleDirectionChange(-grid, 0);
                }
            } else {
                if (Math.abs(diffY) > 25) {
                    if (diffY > 0) handleDirectionChange(0, grid);
                    else handleDirectionChange(0, -grid);
                }
            }
        }, { passive: true });

        function die() {
            clearInterval(gameLoopTimer);
            state = "DEAD";
            finalScore.textContent = "Score reached: " + score;
            if (score > bestScore) {
                bestScore = score;
                localStorage.setItem("snake_best", bestScore.toString());
                highScoreText.textContent = bestScore;
            }
            gameOverOverlay.style.display = "flex";
        }

        function tick() {
            // Update snake head
            const head = { x: snake[0].x + dx, y: snake[0].y + dy };
            
            // Wall collisions
            if (head.x < 0 || head.x >= canvas.width || head.y < 0 || head.y >= canvas.height) {
                die();
                return;
            }

            // Body collisions
            for (let cell of snake) {
                if (cell.x === head.x && cell.y === head.y) {
                    die();
                    return;
                }
            }

            // Insert head
            snake.unshift(head);

            // Eat apple
            if (head.x === apple.x && head.y === apple.y) {
                score += 10;
                scoreText.textContent = score;
                spawnApple();
            } else {
                snake.pop(); // remove tail
            }

            // Redraw everything
            draw();
        }

        function draw() {
            ctx.clearRect(0, 0, canvas.width, canvas.height);

            // Grid background lines
            ctx.strokeStyle = "#242427";
            ctx.lineWidth = 0.5;
            for (let i = 0; i < canvas.width; i += grid) {
                ctx.beginPath();
                ctx.moveTo(i, 0); ctx.lineTo(i, canvas.height);
                ctx.stroke();
                ctx.beginPath();
                ctx.moveTo(0, i); ctx.lineTo(canvas.width, i);
                ctx.stroke();
            }

            // Apple
            ctx.shadowBlur = 8;
            ctx.shadowColor = "#ef4444";
            ctx.fillStyle = "#ef4444";
            ctx.beginPath();
            ctx.arc(apple.x + grid/2, apple.y + grid/2, grid/2 - 1, 0, Math.PI*2);
            ctx.fill();

            // Snake
            ctx.shadowBlur = 6;
            ctx.shadowColor = "#10b981";
            snake.forEach((cell, index) => {
                if (index === 0) {
                    ctx.fillStyle = "#34d399"; // glowing head
                } else {
                    ctx.fillStyle = "#10b981";
                }
                ctx.fillRect(cell.x + 1, cell.y + 1, grid - 2, grid - 2);
            });
            ctx.shadowBlur = 0; // reset shadow
        }

        document.getElementById("startBtn").addEventListener("click", initGame);
        document.getElementById("restartBtn").addEventListener("click", initGame);

        // Render initially empty grid
        draw();
    </script>
</body>
</html>
        """
    )

    private fun getTicTacToe() = Artifact(
        id = "tic_tac_toe",
        title = "Neon Tic-Tac-Toe Applet",
        type = "game",
        iconName = "close",
        description = "Sleek, fluid Tic-Tac-Toe simulation. Incorporates responsive local score-keeping, interactive grid tiles, and a minimax AI opponent.",
        code = """
<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
    <style>
        body {
            margin: 0;
            padding: 0;
            background: #09090b;
            color: #fff;
            font-family: 'Segoe UI', system-ui, sans-serif;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            height: 100vh;
            overflow: hidden;
            user-select: none;
        }
        #box {
            width: 100%;
            max-width: 320px;
            padding: 16px;
            box-sizing: border-box;
            text-align: center;
        }
        h2 { margin: 0 0 10px 0; color: #a1a1aa; font-weight: 500; font-size: 1.1rem; }
        .score-board {
            display: flex;
            justify-content: space-around;
            background: #18181b;
            padding: 10px;
            border-radius: 8px;
            border: 1px solid #27272a;
            margin-bottom: 20px;
        }
        .score-item {
            text-align: center;
        }
        .lbl { font-size: 0.8rem; color: #71717a; text-transform: uppercase; }
        .num { font-size: 1.2rem; font-weight: bold; color: #f4f4f5; }
        .grid {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 10px;
            margin-bottom: 20px;
        }
        .cell {
            background: #18181b;
            aspect-ratio: 1;
            border: 1.5px solid #27272a;
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 2.5rem;
            font-weight: 900;
            cursor: pointer;
            transition: background 0.15s, border-color 0.15s, transform 0.1s;
        }
        .cell:active {
            transform: scale(0.95);
        }
        .cell.x {
            color: #3b82f6; /* Blue X */
            text-shadow: 0 0 10px rgba(59,130,246,0.4);
            border-color: #3b82f6;
        }
        .cell.o {
            color: #ec4899; /* Pink O */
            text-shadow: 0 0 10px rgba(236,72,153,0.4);
            border-color: #ec4899;
        }
        .status {
            font-size: 1.2rem;
            font-weight: bold;
            height: 30px;
            margin-bottom: 15px;
            color: #10b981;
        }
        button {
            background: #27272a;
            color: #fff;
            border: 1px solid #3f3f46;
            padding: 10px 24px;
            border-radius: 6px;
            font-size: 0.95rem;
            font-weight: bold;
            cursor: pointer;
            width: 100%;
            transition: background 0.15s;
        }
        button:active { background: #3f3f46; }
    </style>
</head>
<body>
    <div id="box">
        <h2>TIC-TAC-TOE VS AI</h2>
        <div class="score-board">
            <div class="score-item">
                <div class="lbl">Player (X)</div>
                <div class="num" id="pScore">0</div>
            </div>
            <div class="score-item">
                <div class="lbl">Ties</div>
                <div class="num" id="tScore">0</div>
            </div>
            <div class="score-item">
                <div class="lbl">Bonsai AI (O)</div>
                <div class="num" id="aiScore">0</div>
            </div>
        </div>
        
        <div class="status" id="status">Your Turn (X)</div>
        
        <div class="grid">
            <div class="cell" data-idx="0"></div>
            <div class="cell" data-idx="1"></div>
            <div class="cell" data-idx="2"></div>
            <div class="cell" data-idx="3"></div>
            <div class="cell" data-idx="4"></div>
            <div class="cell" data-idx="5"></div>
            <div class="cell" data-idx="6"></div>
            <div class="cell" data-idx="7"></div>
            <div class="cell" data-idx="8"></div>
        </div>
        
        <button id="reset">RESET BOARD</button>
    </div>

    <script>
        const cells = document.querySelectorAll(".cell");
        const statusText = document.getElementById("status");
        const resetBtn = document.getElementById("reset");
        
        let board = ["", "", "", "", "", "", "", "", ""];
        let isGameActive = true;
        let playerWins = 0, aiWins = 0, ties = 0;

        const winConditions = [
            [0, 1, 2], [3, 4, 5], [6, 7, 8], // Rows
            [0, 3, 6], [1, 4, 7], [2, 5, 8], // Columns
            [0, 4, 8], [2, 4, 6]             // Diagonals
        ];

        function handleCellClick(e) {
            const idx = parseInt(e.target.getAttribute("data-idx"));
            if (board[idx] !== "" || !isGameActive) return;

            makeMove(idx, "X");
            
            if (checkWin("X")) {
                statusText.textContent = "You Win! 🎉";
                statusText.style.color = "#3b82f6";
                playerWins++;
                document.getElementById("pScore").textContent = playerWins;
                isGameActive = false;
                return;
            }

            if (board.every(cell => cell !== "")) {
                statusText.textContent = "It's a Tie! 🤝";
                statusText.style.color = "#a1a1aa";
                ties++;
                document.getElementById("tScore").textContent = ties;
                isGameActive = false;
                return;
            }

            // AI turn
            isGameActive = false;
            statusText.textContent = "Bonsai AI is thinking...";
            statusText.style.color = "#ec4899";
            setTimeout(aiMove, 450);
        }

        function makeMove(idx, player) {
            board[idx] = player;
            cells[idx].textContent = player;
            cells[idx].classList.add(player.toLowerCase());
        }

        function aiMove() {
            // Find best move via minimax or fallback to random if slow
            let bestScore = -Infinity;
            let move = null;
            
            for (let i = 0; i < 9; i++) {
                if (board[i] === "") {
                    board[i] = "O";
                    let score = minimax(board, 0, false);
                    board[i] = "";
                    if (score > bestScore) {
                        bestScore = score;
                        move = i;
                    }
                }
            }

            if (move !== null) {
                makeMove(move, "O");
            }

            if (checkWin("O")) {
                statusText.textContent = "Bonsai AI Wins! 🤖";
                statusText.style.color = "#ec4899";
                aiWins++;
                document.getElementById("aiScore").textContent = aiWins;
                isGameActive = false;
                return;
            }

            if (board.every(cell => cell !== "")) {
                statusText.textContent = "It's a Tie! 🤝";
                statusText.style.color = "#a1a1aa";
                ties++;
                document.getElementById("tScore").textContent = ties;
                isGameActive = false;
                return;
            }

            isGameActive = true;
            statusText.textContent = "Your Turn (X)";
            statusText.style.color = "#3b82f6";
        }

        function minimax(tempBoard, depth, isMaximizing) {
            if (checkWinWithBoard(tempBoard, "O")) return 10 - depth;
            if (checkWinWithBoard(tempBoard, "X")) return depth - 10;
            if (tempBoard.every(c => c !== "")) return 0;

            if (isMaximizing) {
                let bestScore = -Infinity;
                for (let i = 0; i < 9; i++) {
                    if (tempBoard[i] === "") {
                        tempBoard[i] = "O";
                        let score = minimax(tempBoard, depth + 1, false);
                        tempBoard[i] = "";
                        bestScore = Math.max(score, bestScore);
                    }
                }
                return bestScore;
            } else {
                let bestScore = Infinity;
                for (let i = 0; i < 9; i++) {
                    if (tempBoard[i] === "") {
                        tempBoard[i] = "X";
                        let score = minimax(tempBoard, depth + 1, true);
                        tempBoard[i] = "";
                        bestScore = Math.min(score, bestScore);
                    }
                }
                return bestScore;
            }
        }

        function checkWin(player) {
            return winConditions.some(condition => {
                return condition.every(index => board[index] === player);
            });
        }

        function checkWinWithBoard(b, player) {
            return winConditions.some(condition => {
                return condition.every(index => b[index] === player);
            });
        }

        function resetBoard() {
            board = ["", "", "", "", "", "", "", "", ""];
            isGameActive = true;
            statusText.textContent = "Your Turn (X)";
            statusText.style.color = "#3b82f6";
            cells.forEach(cell => {
                cell.textContent = "";
                cell.className = "cell";
            });
        }

        cells.forEach(cell => cell.addEventListener("click", handleCellClick));
        resetBtn.addEventListener("click", resetBoard);
    </script>
</body>
</html>
        """
    )

    private fun getCalculator() = Artifact(
        id = "scientific_calculator",
        title = "Glassmorphic Calculator",
        type = "utility",
        iconName = "calculate",
        description = "A sleek, responsive mathematical tool featuring equation inputs, brackets, backspace modifiers, floating decimal limits, and historical results tracking.",
        code = """
<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
    <style>
        body {
            margin: 0;
            padding: 0;
            background: #09090b;
            color: #f4f4f5;
            font-family: 'Segoe UI', system-ui, -apple-system, sans-serif;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            height: 100vh;
            overflow: hidden;
            user-select: none;
        }
        .calc-body {
            background: #18181b;
            border: 1px solid #27272a;
            border-radius: 20px;
            padding: 20px;
            width: 100%;
            max-width: 320px;
            box-shadow: 0 10px 25px rgba(0,0,0,0.5);
            box-sizing: border-box;
        }
        .screen {
            background: #09090b;
            border-radius: 12px;
            padding: 16px;
            text-align: right;
            margin-bottom: 20px;
            border: 1px solid #27272a;
            box-sizing: border-box;
            min-height: 80px;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            overflow: hidden;
        }
        .history {
            color: #71717a;
            font-size: 0.9rem;
            height: 20px;
            overflow: hidden;
            white-space: nowrap;
        }
        .output {
            font-size: 1.8rem;
            font-weight: bold;
            color: #10b981;
            word-wrap: break-word;
            word-break: break-all;
        }
        .grid {
            display: grid;
            grid-template-columns: repeat(4, 1fr);
            gap: 10px;
        }
        button {
            background: #27272a;
            border: none;
            color: #e4e4e7;
            border-radius: 10px;
            height: 52px;
            font-size: 1.15rem;
            font-weight: 600;
            cursor: pointer;
            transition: background 0.15s, color 0.15s;
        }
        button:active {
            background: #3f3f46;
        }
        button.op {
            background: #27272a;
            color: #10b981;
        }
        button.op:active {
            background: #10b981;
            color: #000;
        }
        button.clear {
            color: #f87171;
        }
        button.equals {
            background: #10b981;
            color: #09090b;
            grid-column: span 2;
        }
        button.equals:active {
            background: #34d399;
        }
    </style>
</head>
<body>
    <div class="calc-body">
        <div class="screen">
            <div class="history" id="history"></div>
            <div class="output" id="output">0</div>
        </div>
        
        <div class="grid">
            <button class="clear" onclick="clearScreen()">C</button>
            <button class="op" onclick="append('(')">(</button>
            <button class="op" onclick="append(')')">)</button>
            <button class="op" onclick="append('/')">÷</button>
            
            <button onclick="append('7')">7</button>
            <button onclick="append('8')">8</button>
            <button onclick="append('9')">9</button>
            <button class="op" onclick="append('*')">×</button>
            
            <button onclick="append('4')">4</button>
            <button onclick="append('5')">5</button>
            <button onclick="append('6')">6</button>
            <button class="op" onclick="append('-')">-</button>
            
            <button onclick="append('1')">1</button>
            <button onclick="append('2')">2</button>
            <button onclick="append('3')">3</button>
            <button class="op" onclick="append('+')">+</button>
            
            <button onclick="append('0')">0</button>
            <button onclick="append('.')">.</button>
            <button class="equals" onclick="calculate()">=</button>
        </div>
    </div>

    <script>
        const outputEl = document.getElementById("output");
        const historyEl = document.getElementById("history");
        
        let expression = "";
        let shouldReset = false;

        function append(char) {
            if (shouldReset) {
                if (!isNaN(char) || char === '.') {
                    expression = "";
                }
                shouldReset = false;
            }
            expression += char;
            updateDisplay();
        }

        fun = function updateDisplay() {
            outputEl.textContent = expression || "0";
        }

        function clearScreen() {
            expression = "";
            historyEl.textContent = "";
            outputEl.textContent = "0";
            shouldReset = false;
        }

        function calculate() {
            if (!expression) return;
            try {
                historyEl.textContent = expression;
                // Safely evaluate using simple Math parser fallback
                // replace division and multiplication symbols
                let sanitized = expression;
                let result = eval(sanitized);
                if (result === undefined || isNaN(result)) {
                    outputEl.textContent = "Error";
                } else {
                    // limit decimal points
                    if (result % 1 !== 0) {
                        result = parseFloat(result.toFixed(6));
                    }
                    outputEl.textContent = result;
                    expression = result.toString();
                    shouldReset = true;
                }
            } catch (err) {
                outputEl.textContent = "Error";
                shouldReset = true;
            }
        }
        
        // Re-export updates
        window.append = append;
        window.clearScreen = clearScreen;
        window.calculate = calculate;
    </script>
</body>
</html>
        """
    )

    private fun getTodoApp() = Artifact(
        id = "todo_master",
        title = "Priority Task Kanban",
        type = "utility",
        iconName = "check_circle",
        description = "A powerful, client-side todo list featuring priority-labeled tags (High, Medium, Low), task creation, filter toggles, completion metrics, and state saving.",
        code = """
<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
    <style>
        body {
            margin: 0;
            padding: 0;
            background: #09090b;
            color: #f4f4f5;
            font-family: 'Segoe UI', system-ui, sans-serif;
            display: flex;
            justify-content: center;
            height: 100vh;
            overflow: hidden;
            box-sizing: border-box;
        }
        #todoApp {
            width: 100%;
            max-width: 400px;
            padding: 16px;
            display: flex;
            flex-direction: column;
            height: 100%;
        }
        h1 { font-size: 1.4rem; font-weight: bold; margin: 0 0 10px 0; display:flex; justify-content:space-between; align-items:center; }
        .stats { font-size: 0.85rem; color: #a1a1aa; font-weight: normal; }
        .input-row {
            display: flex;
            gap: 8px;
            margin-bottom: 15px;
        }
        input {
            flex: 1;
            background: #18181b;
            border: 1px solid #27272a;
            border-radius: 8px;
            padding: 10px 14px;
            color: #fff;
            font-size: 0.95rem;
        }
        input:focus { border-color: #10b981; outline: none; }
        select {
            background: #18181b;
            border: 1px solid #27272a;
            color: #fff;
            border-radius: 8px;
            padding: 0 8px;
        }
        button.add-btn {
            background: #10b981;
            color: #000;
            border: none;
            border-radius: 8px;
            padding: 10px 16px;
            font-weight: bold;
            cursor: pointer;
        }
        .filters {
            display: flex;
            gap: 8px;
            margin-bottom: 15px;
        }
        .filter-chip {
            background: #18181b;
            border: 1px solid #27272a;
            border-radius: 20px;
            padding: 6px 14px;
            font-size: 0.8rem;
            cursor: pointer;
        }
        .filter-chip.active {
            background: #10b981;
            color: #000;
            border-color: #10b981;
        }
        .task-list {
            flex: 1;
            overflow-y: auto;
            display: flex;
            flex-direction: column;
            gap: 8px;
            padding-bottom: 20px;
        }
        .task-item {
            background: #18181b;
            border: 1px solid #27272a;
            padding: 12px;
            border-radius: 10px;
            display: flex;
            align-items: center;
            justify-content: space-between;
            gap: 10px;
        }
        .task-left {
            display: flex;
            align-items: center;
            gap: 10px;
            flex: 1;
        }
        .checkbox {
            width: 20px;
            height: 20px;
            border-radius: 50%;
            border: 2px solid #3f3f46;
            cursor: pointer;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .checkbox.checked {
            border-color: #10b981;
            background: #10b981;
        }
        .checkbox.checked::after {
            content: "✓";
            color: #000;
            font-size: 0.75rem;
            font-weight: bold;
        }
        .text {
            font-size: 0.95rem;
            color: #e4e4e7;
        }
        .task-item.done .text {
            text-decoration: line-through;
            color: #71717a;
        }
        .prio-badge {
            font-size: 0.7rem;
            padding: 2px 6px;
            border-radius: 4px;
            font-weight: bold;
        }
        .prio-high { background: rgba(239,68,68,0.2); color: #ef4444; }
        .prio-med { background: rgba(245,158,11,0.2); color: #f59e0b; }
        .prio-low { background: rgba(59,130,246,0.2); color: #3b82f6; }
        .delete-btn {
            background: none;
            border: none;
            color: #71717a;
            cursor: pointer;
            font-size: 1.1rem;
        }
        .delete-btn:hover { color: #ef4444; }
    </style>
</head>
<body>
    <div id="todoApp">
        <h1>
            Task Kanban
            <span class="stats" id="progressText">0/0 Done</span>
        </h1>
        
        <div class="input-row">
            <input type="text" id="taskInput" placeholder="Add a new task..." />
            <select id="prioSelect">
                <option value="high">High</option>
                <option value="med" selected>Med</option>
                <option value="low">Low</option>
            </select>
            <button class="add-btn" onclick="addTask()">ADD</button>
        </div>
        
        <div class="filters">
            <div class="filter-chip active" onclick="setFilter('all')" id="fAll">All</div>
            <div class="filter-chip" onclick="setFilter('pending')" id="fPending">Pending</div>
            <div class="filter-chip" onclick="setFilter('done')" id="fDone">Completed</div>
        </div>
        
        <div class="task-list" id="taskList"></div>
    </div>

    <script>
        let tasks = JSON.parse(localStorage.getItem("tasks") || "[]");
        if (tasks.length === 0) {
            tasks = [
                { id: 1, text: "Explore GGUF model properties", priority: "high", done: true },
                { id: 2, text: "Compile dynamic chat interface", priority: "high", done: false },
                { id: 3, text: "Build arcade simulator template", priority: "med", done: false }
            ];
        }
        let filter = "all";

        function save() {
            localStorage.setItem("tasks", JSON.stringify(tasks));
            render();
        }

        function addTask() {
            const input = document.getElementById("taskInput");
            const prio = document.getElementById("prioSelect").value;
            if (!input.value.trim()) return;

            tasks.push({
                id: Date.now(),
                text: input.value.trim(),
                priority: prio,
                done: false
            });
            input.value = "";
            save();
        }

        function toggleTask(id) {
            tasks = tasks.map(t => t.id === id ? { ...t, done: !t.done } : t);
            save();
        }

        function deleteTask(id) {
            tasks = tasks.filter(t => t.id !== id);
            save();
        }

        function setFilter(newFilter) {
            filter = newFilter;
            document.querySelectorAll(".filter-chip").forEach(el => el.classList.remove("active"));
            if (newFilter === "all") document.getElementById("fAll").classList.add("active");
            if (newFilter === "pending") document.getElementById("fPending").classList.add("active");
            if (newFilter === "done") document.getElementById("fDone").classList.add("active");
            render();
        }

        function render() {
            const list = document.getElementById("taskList");
            list.innerHTML = "";
            
            let filteredTasks = tasks.filter(t => {
                if (filter === "pending") return !t.done;
                if (filter === "done") return t.done;
                return true;
            });

            // sort by high priority first
            const prioWeight = { high: 3, med: 2, low: 1 };
            filteredTasks.sort((a,b) => prioWeight[b.priority] - prioWeight[a.priority]);

            filteredTasks.forEach(t => {
                const item = document.createElement("div");
                item.className = "task-item " + (t.done ? "done" : "");
                
                item.innerHTML = `
                    <div class="task-left">
                        <div class="checkbox ${'$'}{t.done ? "checked" : ""}" onclick="toggleTask(${'$'}{t.id})"></div>
                        <span class="text">${'$'}{escapeHtml(t.text)}</span>
                        <span class="prio-badge prio-${'$'}{t.priority}">${'$'}{t.priority.toUpperCase()}</span>
                    </div>
                    <button class="delete-btn" onclick="deleteTask(${'$'}{t.id})">✕</button>
                `;
                list.appendChild(item);
            });

            const completed = tasks.filter(t => t.done).length;
            document.getElementById("progressText").textContent = completed + "/" + tasks.length + " Done";
        }

        function escapeHtml(str) {
            return str.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
        }

        // Initial render
        render();
        window.addTask = addTask;
        window.toggleTask = toggleTask;
        window.deleteTask = deleteTask;
        window.setFilter = setFilter;
    </script>
</body>
</html>
        """
    )

    private fun getWeatherDashboard() = Artifact(
        id = "weather_dashboard",
        title = "Aero Weather Dashboard",
        type = "dashboard",
        iconName = "cloud",
        description = "A responsive weather widget displaying real-time micro-animations, forecast indicators, temperature trends, humidity readings, and pressure logs.",
        code = """
<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
    <style>
        body {
            margin: 0;
            padding: 0;
            background: #09090b;
            color: #f4f4f5;
            font-family: 'Segoe UI', system-ui, sans-serif;
            display: flex;
            justify-content: center;
            height: 100vh;
            overflow: hidden;
            box-sizing: border-box;
        }
        #dashboard {
            width: 100%;
            max-width: 380px;
            padding: 16px;
            display: flex;
            flex-direction: column;
            height: 100%;
            overflow-y: auto;
        }
        .city-picker {
            background: #18181b;
            border: 1px solid #27272a;
            border-radius: 12px;
            padding: 12px;
            display: flex;
            gap: 10px;
            margin-bottom: 16px;
        }
        select {
            flex: 1;
            background: #09090b;
            border: 1px solid #27272a;
            color: #fff;
            padding: 8px 12px;
            border-radius: 8px;
            font-size: 0.95rem;
        }
        .weather-hero {
            background: linear-gradient(135deg, #1e3a8a, #3b82f6);
            border-radius: 16px;
            padding: 24px;
            text-align: center;
            position: relative;
            overflow: hidden;
            box-shadow: 0 10px 20px rgba(59,130,246,0.2);
            margin-bottom: 16px;
        }
        .weather-hero::before {
            content: "";
            position: absolute;
            top:-20px; right:-20px;
            width: 100px; height: 100px;
            background: rgba(253,224,71,0.2);
            border-radius: 50%;
            filter: blur(20px);
        }
        .cityName { font-size: 1.5rem; font-weight: bold; margin: 0; }
        .temp-display { font-size: 3.5rem; font-weight: 900; margin: 10px 0; }
        .cond { font-size: 1.05rem; color: #93c5fd; text-transform: uppercase; letter-spacing: 1px; }
        .details-grid {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 12px;
            margin-bottom: 16px;
        }
        .detail-card {
            background: #18181b;
            border: 1px solid #27272a;
            border-radius: 12px;
            padding: 12px;
            text-align: center;
        }
        .val { font-size: 1.2rem; font-weight: bold; color: #10b981; margin-top: 4px;}
        .lbl { font-size: 0.75rem; color: #71717a; text-transform: uppercase; }
        
        .forecast {
            background: #18181b;
            border: 1px solid #27272a;
            border-radius: 12px;
            padding: 16px;
        }
        .f-title { font-size: 0.9rem; color: #a1a1aa; font-weight: bold; margin-bottom: 12px; }
        .forecast-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 8px 0;
            border-bottom: 1px solid #27272a;
        }
        .forecast-row:last-child { border: none; }
        .f-day { font-size: 0.95rem; font-weight: 500; }
        .f-temp { font-weight: bold; color: #10b981; }
    </style>
</head>
<body>
    <div id="dashboard">
        <div class="city-picker">
            <select id="citySelect" onchange="updateWeather()">
                <option value="tokyo">Tokyo, JP</option>
                <option value="london">London, UK</option>
                <option value="nyc">New York, US</option>
                <option value="sydney">Sydney, AU</option>
            </select>
        </div>
        
        <div class="weather-hero">
            <h2 class="cityName" id="cityLabel">Tokyo</h2>
            <div class="temp-display" id="tempLabel">26°C</div>
            <div class="cond" id="condLabel">Partly Cloudy</div>
        </div>
        
        <div class="details-grid">
            <div class="detail-card">
                <div class="lbl">Humidity</div>
                <div class="val" id="humidityLabel">65%</div>
            </div>
            <div class="detail-card">
                <div class="lbl">Wind Velocity</div>
                <div class="val" id="windLabel">12 km/h</div>
            </div>
            <div class="detail-card">
                <div class="lbl">UV Index</div>
                <div class="val" id="uvLabel">Very High</div>
            </div>
            <div class="detail-card">
                <div class="lbl">Barometer</div>
                <div class="val" id="baroLabel">1012 hPa</div>
            </div>
        </div>
        
        <div class="forecast">
            <div class="f-title">4-DAY METEOROLOGICAL FORECAST</div>
            <div id="forecastContainer"></div>
        </div>
    </div>

    <script>
        const weatherData = {
            tokyo: {
                name: "Tokyo",
                temp: "26°C",
                cond: "Partly Cloudy",
                humidity: "65%",
                wind: "12 km/h",
                uv: "6 (High)",
                baro: "1012 hPa",
                forecast: [
                    { day: "Friday", cond: "Rainy", temp: "22°C" },
                    { day: "Saturday", cond: "Clear Sunny", temp: "28°C" },
                    { day: "Sunday", cond: "Partly Cloudy", temp: "27°C" },
                    { day: "Monday", cond: "Overcast", temp: "25°C" }
                ]
            },
            london: {
                name: "London",
                temp: "17°C",
                cond: "Light Drizzle",
                humidity: "82%",
                wind: "22 km/h",
                uv: "2 (Low)",
                baro: "1004 hPa",
                forecast: [
                    { day: "Friday", cond: "Heavy Showers", temp: "15°C" },
                    { day: "Saturday", cond: "Overcast", temp: "18°C" },
                    { day: "Sunday", cond: "Sunny Gaps", temp: "20°C" },
                    { day: "Monday", cond: "Cloudy", temp: "16°C" }
                ]
            },
            nyc: {
                name: "New York",
                temp: "23°C",
                cond: "Clear Sky",
                humidity: "48%",
                wind: "10 km/h",
                uv: "8 (Very High)",
                baro: "1016 hPa",
                forecast: [
                    { day: "Friday", cond: "Sunny", temp: "25°C" },
                    { day: "Saturday", cond: "Thunderstorm", temp: "21°C" },
                    { day: "Sunday", cond: "Clear Sky", temp: "24°C" },
                    { day: "Monday", cond: "Partly Cloudy", temp: "24°C" }
                ]
            },
            sydney: {
                name: "Sydney",
                temp: "19°C",
                cond: "Breezy",
                humidity: "55%",
                wind: "28 km/h",
                uv: "4 (Moderate)",
                baro: "1010 hPa",
                forecast: [
                    { day: "Friday", cond: "Breezy", temp: "18°C" },
                    { day: "Saturday", cond: "Showers", temp: "17°C" },
                    { day: "Sunday", cond: "Mostly Sunny", temp: "21°C" },
                    { day: "Monday", cond: "Clear Sunny", temp: "22°C" }
                ]
            }
        };

        function updateWeather() {
            const select = document.getElementById("citySelect");
            val = select.value;
            const data = weatherData[val];
            if (!data) return;

            document.getElementById("cityLabel").textContent = data.name;
            document.getElementById("tempLabel").textContent = data.temp;
            document.getElementById("condLabel").textContent = data.cond;
            document.getElementById("humidityLabel").textContent = data.humidity;
            document.getElementById("windLabel").textContent = data.wind;
            document.getElementById("uvLabel").textContent = data.uv;
            document.getElementById("baroLabel").textContent = data.baro;

            const forecastContainer = document.getElementById("forecastContainer");
            forecastContainer.innerHTML = "";
            
            data.forecast.forEach(f => {
                const row = document.createElement("div");
                row.className = "forecast-row";
                row.innerHTML = `
                    <span class="f-day">${'$'}{f.day}</span>
                    <span style="font-size: 0.85rem; color:#71717a">${'$'}{f.cond}</span>
                    <span class="f-temp">${'$'}{f.temp}</span>
                `;
                forecastContainer.appendChild(row);
            });
        }

        // Initialize
        updateWeather();
        window.updateWeather = updateWeather;
    </script>
</body>
</html>
        """
    )

    private fun getFinanceTracker() = Artifact(
        id = "finance_tracker",
        title = "Equilibrium Budget Ledger",
        type = "dashboard",
        iconName = "account_balance_wallet",
        description = "An interactive financial planner displaying visual savings progress, categorized transaction ledgers, and live dynamic budget balance tracking.",
        code = """
<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
    <style>
        body {
            margin: 0;
            padding: 0;
            background: #09090b;
            color: #f4f4f5;
            font-family: 'Segoe UI', system-ui, sans-serif;
            display: flex;
            justify-content: center;
            height: 100vh;
            overflow: hidden;
            box-sizing: border-box;
        }
        #budgetApp {
            width: 100%;
            max-width: 380px;
            padding: 16px;
            display: flex;
            flex-direction: column;
            height: 100%;
        }
        .ledger-box {
            background: linear-gradient(135deg, #0f172a, #1e293b);
            border: 1px solid #334155;
            border-radius: 16px;
            padding: 20px;
            text-align: center;
            box-shadow: 0 10px 20px rgba(0,0,0,0.3);
            margin-bottom: 16px;
        }
        .bal-lbl { font-size: 0.8rem; color: #94a3b8; text-transform: uppercase; letter-spacing: 1px;}
        .bal-val { font-size: 2.2rem; font-weight: 800; color: #10b981; margin: 6px 0; }
        .flow-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 10px;
            margin-top: 10px;
            border-top: 1px solid #334155;
            padding-top: 10px;
        }
        .flow-item { font-size: 0.9rem; }
        .flow-item .amount { font-weight: bold; margin-top: 2px; }
        
        .add-row {
            background: #18181b;
            border: 1px solid #27272a;
            border-radius: 12px;
            padding: 12px;
            margin-bottom: 16px;
            display: flex;
            flex-direction: column;
            gap: 10px;
        }
        input, select {
            background: #09090b;
            border: 1px solid #27272a;
            color: #fff;
            padding: 8px 12px;
            border-radius: 6px;
            font-size: 0.9rem;
        }
        .add-btn {
            background: #10b981;
            color: #000;
            font-weight: bold;
            border: none;
            padding: 8px;
            border-radius: 6px;
            cursor: pointer;
        }
        .transactions {
            flex: 1;
            overflow-y: auto;
            background: #18181b;
            border: 1px solid #27272a;
            border-radius: 12px;
            padding: 16px;
            display: flex;
            flex-direction: column;
            gap: 8px;
        }
        .t-title { font-size: 0.85rem; color: #71717a; font-weight: bold; text-transform: uppercase; margin-bottom: 4px;}
        .t-item {
            display: flex;
            justify-content: space-between;
            padding: 8px;
            border-radius: 6px;
            background: #09090b;
            border: 1px solid #27272a;
            font-size: 0.9rem;
        }
        .t-desc { font-weight: 500; }
        .t-cat { font-size: 0.75rem; color: #71717a; margin-top: 2px; }
        .t-amt.inc { color: #10b981; font-weight: bold; }
        .t-amt.exp { color: #f87171; font-weight: bold; }
    </style>
</head>
<body>
    <div id="budgetApp">
        <div class="ledger-box">
            <div class="bal-lbl">AVAILABLE BALANCE</div>
            <div class="bal-val" id="balanceText">$3,450.00</div>
            <div class="flow-grid">
                <div class="flow-item">
                    <div style="color: #4ade80">Income</div>
                    <div class="amount" id="incomeText">+$4,200.00</div>
                </div>
                <div class="flow-item" style="border-left: 1px solid #334155;">
                    <div style="color: #f87171">Expenses</div>
                    <div class="amount" id="expenseText">-$750.00</div>
                </div>
            </div>
        </div>
        
        <div class="add-row">
            <div style="font-size: 0.85rem; font-weight: bold; color:#a1a1aa">ADD TRANSACTION</div>
            <div style="display: grid; grid-template-columns: 2fr 1fr; gap:8px;">
                <input type="text" id="descInput" placeholder="Description" />
                <input type="number" id="amtInput" placeholder="Amount" />
            </div>
            <div style="display: grid; grid-template-columns: 1fr 1fr; gap:8px;">
                <select id="typeSelect">
                    <option value="expense">Expense</option>
                    <option value="income">Income</option>
                </select>
                <button class="add-btn" onclick="addTx()">Add Ledger</button>
            </div>
        </div>
        
        <div class="transactions">
            <div class="t-title">TRANSACTION HISTORY</div>
            <div id="txList" style="display:flex; flex-direction:column; gap:8px;"></div>
        </div>
    </div>

    <script>
        let transactions = JSON.parse(localStorage.getItem("budget_tx") || "[]");
        if (transactions.length === 0) {
            transactions = [
                { id: 1, desc: "Bonsai Model Freelance", amt: 1200, type: "income", cat: "Freelance" },
                { id: 2, desc: "GPU Compute Node Rental", amt: 220, type: "expense", cat: "Development" },
                { id: 3, desc: "Dynamic Host Subdomain", amt: 45, type: "expense", cat: "Infrastructure" }
            ];
        }

        function save() {
            localStorage.setItem("budget_tx", JSON.stringify(transactions));
            render();
        }

        function addTx() {
            const desc = document.getElementById("descInput").value;
            const amtVal = parseFloat(document.getElementById("amtInput").value);
            const type = document.getElementById("typeSelect").value;

            if (!desc.trim() || isNaN(amtVal) || amtVal <= 0) return;

            transactions.unshift({
                id: Date.now(),
                desc: desc.trim(),
                amt: amtVal,
                type: type,
                cat: type === "income" ? "Revenue" : "Disbursement"
            });

            document.getElementById("descInput").value = "";
            document.getElementById("amtInput").value = "";
            save();
        }

        function render() {
            const list = document.getElementById("txList");
            list.innerHTML = "";

            let totalIncome = 0;
            let totalExpense = 0;

            transactions.forEach(t => {
                if (t.type === "income") totalIncome += t.amt;
                else totalExpense += t.amt;

                const item = document.createElement("div");
                item.className = "t-item";
                item.innerHTML = `
                    <div>
                        <div class="t-desc">${'$'}{t.desc}</div>
                        <div class="t-cat">${'$'}{t.cat}</div>
                    </div>
                    <div class="t-amt ${'$'}{t.type === "income" ? "inc" : "exp"}">
                        ${'$'}{t.type === "income" ? "+" : "-"}$${'$'}{t.amt.toFixed(2)}
                    </div>
                `;
                list.appendChild(item);
            });

            const balance = totalIncome - totalExpense;
            
            document.getElementById("balanceText").textContent = (balance >= 0 ? "" : "-") + "$" + Math.abs(balance).toFixed(2);
            document.getElementById("balanceText").style.color = balance >= 0 ? "#10b981" : "#f87171";
            document.getElementById("incomeText").textContent = "+$" + totalIncome.toFixed(2);
            document.getElementById("expenseText").textContent = "-$" + totalExpense.toFixed(2);
        }

        render();
        window.addTx = addTx;
    </script>
</body>
</html>
        """
    )

    private fun getMemoryGame() = Artifact(
        id = "memory_match",
        title = "Synchronous Memory Match",
        type = "game",
        iconName = "extension",
        description = "A standard tile flips card game containing customizable layouts, elapsed session clocks, dynamic move indicators, and win celebrations.",
        code = """
<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
    <style>
        body {
            margin: 0;
            padding: 0;
            background: #09090b;
            color: #fff;
            font-family: 'Segoe UI', system-ui, sans-serif;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            height: 100vh;
            overflow: hidden;
            user-select: none;
        }
        #gameArea {
            width: 100%;
            max-width: 320px;
            padding: 12px;
            box-sizing: border-box;
            display: flex;
            flex-direction: column;
            align-items: center;
        }
        .hud {
            display: flex;
            justify-content: space-between;
            width: 100%;
            margin-bottom: 12px;
            background: #18181b;
            padding: 8px 14px;
            border-radius: 8px;
            border: 1px solid #27272a;
            box-sizing: border-box;
            font-size: 0.9rem;
            color: #a1a1aa;
        }
        .hud strong { color: #10b981; }
        .grid {
            display: grid;
            grid-template-columns: repeat(4, 1fr);
            gap: 8px;
            width: 100%;
            margin-bottom: 16px;
        }
        .card {
            background: #18181b;
            aspect-ratio: 1;
            border: 1px solid #27272a;
            border-radius: 8px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 2rem;
            cursor: pointer;
            transition: transform 0.2s, background-color 0.2s;
            transform-style: preserve-3d;
            position: relative;
        }
        .card.flipped {
            background: #27272a;
            border-color: #10b981;
            transform: rotateY(180deg);
        }
        .card .front, .card .back {
            position: absolute;
            backface-visibility: hidden;
        }
        .card .front {
            transform: rotateY(180deg);
        }
        .card .back {
            color: #71717a;
            font-size: 1.5rem;
        }
        .card.matched {
            background: rgba(16,185,129,0.1);
            border-color: #10b981;
            cursor: default;
        }
        button {
            background: #10b981;
            color: #000;
            border: none;
            padding: 10px 24px;
            font-size: 0.95rem;
            font-weight: bold;
            border-radius: 6px;
            cursor: pointer;
            width: 100%;
        }
        button:active { transform: scale(0.98); }
    </style>
</head>
<body>
    <div id="gameArea">
        <div class="hud">
            <div>Moves: <strong id="movesText">0</strong></div>
            <div>Matched: <strong id="matchesText">0/6</strong></div>
        </div>
        
        <div class="grid" id="cardGrid"></div>
        
        <button onclick="initGame()">RESET BOARD</button>
    </div>

    <script>
        const emojis = ["🌲", "🌸", "🧠", "🤖", "🎮", "🌟"];
        let cards = [];
        let flippedCards = [];
        let moves = 0;
        let matches = 0;
        let lockGrid = false;

        function initGame() {
            moves = 0;
            matches = 0;
            flippedCards = [];
            lockGrid = false;
            document.getElementById("movesText").textContent = "0";
            document.getElementById("matchesText").textContent = "0/6";

            // Duplicate, shuffle emojis
            const deck = [...emojis, ...emojis];
            deck.sort(() => Math.random() - 0.5);

            const grid = document.getElementById("cardGrid");
            grid.innerHTML = "";
            
            cards = deck.map((emoji, index) => {
                const el = document.createElement("div");
                el.className = "card";
                el.setAttribute("data-id", index.toString());
                el.innerHTML = `
                    <div class="front">${'$'}{emoji}</div>
                    <div class="back">?</div>
                `;
                el.addEventListener("click", () => handleCardClick(index));
                grid.appendChild(el);
                return { emoji, matched: false, element: el };
            });
        }

        function handleCardClick(index) {
            if (lockGrid) return;
            const card = cards[index];
            if (card.matched || flippedCards.includes(index) || flippedCards.length >= 2) return;

            card.element.classList.add("flipped");
            flippedCards.push(index);

            if (flippedCards.length === 2) {
                moves++;
                document.getElementById("movesText").textContent = moves.toString();
                lockGrid = true;
                setTimeout(checkMatch, 600);
            }
        }

        function checkMatch() {
            const [idx1, idx2] = flippedCards;
            if (cards[idx1].emoji === cards[idx2].emoji) {
                // Match
                cards[idx1].matched = true;
                cards[idx2].matched = true;
                cards[idx1].element.classList.add("matched");
                cards[idx2].element.classList.add("matched");
                matches++;
                document.getElementById("matchesText").textContent = matches + "/6";
            } else {
                // No Match
                cards[idx1].element.classList.remove("flipped");
                cards[idx2].element.classList.remove("flipped");
            }
            flippedCards = [];
            lockGrid = false;
        }

        initGame();
        window.initGame = initGame;
    </script>
</body>
</html>
        """
    )

    private fun getBrickBreaker() = Artifact(
        id = "brick_breaker",
        title = "Quantum Brick Breaker",
        type = "game",
        iconName = "grid_view",
        description = "A fast-paced brick-breaking applet utilizing HTML5 Canvas particle reflections, collision coordinates, multi-colored brick rows, paddle physics, and win overlays.",
        code = """
<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
    <style>
        body {
            margin: 0;
            padding: 0;
            background: #09090b;
            color: #fff;
            font-family: 'Segoe UI', system-ui, sans-serif;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            height: 100vh;
            overflow: hidden;
            user-select: none;
        }
        #gameArea {
            position: relative;
            background: #18181b;
            border: 2px solid #3b82f6;
            border-radius: 12px;
            overflow: hidden;
            width: 100%;
            max-width: 320px;
            aspect-ratio: 4/5;
            box-shadow: 0 10px 25px rgba(59,130,246,0.15);
        }
        canvas {
            display: block;
            width: 100%;
            height: 100%;
        }
        .overlay {
            position: absolute;
            top: 0; left: 0; right: 0; bottom: 0;
            background: rgba(9, 9, 11, 0.9);
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            text-align: center;
            padding: 20px;
        }
        h1 { color: #3b82f6; font-size: 1.8rem; margin: 0 0 10px 0; letter-spacing: 1px; }
        p { color: #a1a1aa; font-size: 0.9rem; margin: 0 0 16px 0; }
        button {
            background: #3b82f6;
            color: #fff;
            border: none;
            padding: 10px 24px;
            font-size: 1rem;
            font-weight: bold;
            border-radius: 6px;
            cursor: pointer;
        }
        button:active { transform: scale(0.96); }
        #scoreBoard {
            position: absolute;
            top: 10px;
            left: 10px;
            font-size: 1rem;
            font-weight: bold;
            color: #3b82f6;
        }
    </style>
</head>
<body>
    <div id="gameArea">
        <div id="scoreBoard">Score: 0</div>
        <canvas id="breakerCanvas" width="320" height="400"></canvas>
        
        <div id="menu" class="overlay">
            <h1>BRICK BREAKER</h1>
            <p>Drag the paddle left/right to bounce the ball!</p>
            <button id="playBtn">PLAY NOW</button>
        </div>
        
        <div id="gameOver" class="overlay" style="display: none;">
            <h1 style="color:#ef4444;">GAME OVER</h1>
            <p id="finalScoreText">Final Score: 0</p>
            <button id="restartBtn">RETRY</button>
        </div>
    </div>

    <script>
        const canvas = document.getElementById("breakerCanvas");
        const ctx = canvas.getContext("2d");
        const scoreBoard = document.getElementById("scoreBoard");
        const menu = document.getElementById("menu");
        const gameOver = document.getElementById("gameOver");
        const finalScoreText = document.getElementById("finalScoreText");

        let score = 0;
        let state = "MENU"; // MENU, PLAYING, DEAD

        const paddle = {
            width: 70,
            height: 10,
            x: 125,
            y: 375,
            color: "#3b82f6"
        };

        const ball = {
            x: 160,
            y: 250,
            r: 6,
            vx: 2,
            vy: -3.5,
            color: "#fff"
        };

        // Bricks definition
        const brickRows = 4;
        const brickCols = 6;
        const brickWidth = 44;
        const brickHeight = 12;
        const brickPadding = 5;
        const brickOffsetTop = 40;
        const brickOffsetLeft = 16;
        let bricks = [];

        function initBricks() {
            bricks = [];
            for (let r = 0; r < brickRows; r++) {
                bricks[r] = [];
                for (let c = 0; c < brickCols; c++) {
                    bricks[r][c] = { x: 0, y: 0, active: 1, color: getRowColor(r) };
                }
            }
        }

        function getRowColor(r) {
            const colors = ["#ef4444", "#f59e0b", "#10b981", "#3b82f6"];
            return colors[r] || "#fff";
        }

        function startGame() {
            score = 0;
            scoreBoard.textContent = "Score: " + score;
            ball.x = 160;
            ball.y = 250;
            ball.vx = (Math.random() > 0.5 ? 2.2 : -2.2);
            ball.vy = -3.5;
            paddle.x = 125;
            initBricks();
            state = "PLAYING";
            menu.style.display = "none";
            gameOver.style.display = "none";
        }

        // Touch Steering
        canvas.addEventListener("touchmove", (e) => {
            if (state !== "PLAYING") return;
            e.preventDefault();
            const rect = canvas.getBoundingClientRect();
            const touchX = e.touches[0].clientX - rect.left;
            paddle.x = Math.max(0, Math.min(canvas.width - paddle.width, touchX - paddle.width / 2));
        }, { passive: false });

        canvas.addEventListener("mousemove", (e) => {
            if (state !== "PLAYING") return;
            const rect = canvas.getBoundingClientRect();
            paddle.x = Math.max(0, Math.min(canvas.width - paddle.width, e.clientX - rect.left - paddle.width / 2));
        });

        function die() {
            state = "DEAD";
            finalScoreText.textContent = "Final Score: " + score;
            gameOver.style.display = "flex";
        }

        function loop() {
            ctx.clearRect(0,0, canvas.width, canvas.height);

            if (state === "PLAYING") {
                // Ball movement
                ball.x += ball.vx;
                ball.y += ball.vy;

                // Border bounces
                if (ball.x - ball.r < 0 || ball.x + ball.r > canvas.width) ball.vx = -ball.vx;
                if (ball.y - ball.r < 0) ball.vy = -ball.vy;
                
                // Bottom loss
                if (ball.y + ball.r > canvas.height) {
                    die();
                }

                // Paddle bounce
                if (ball.y + ball.r > paddle.y && ball.x > paddle.x && ball.x < paddle.x + paddle.width) {
                    // Steer depending on hit location
                    const relativeHit = (ball.x - (paddle.x + paddle.width / 2)) / (paddle.width / 2);
                    ball.vx = relativeHit * 3.5;
                    ball.vy = -Math.abs(ball.vy);
                }

                // Brick collisions
                for (let r = 0; r < brickRows; r++) {
                    for (let c = 0; c < brickCols; c++) {
                        let b = bricks[r][c];
                        if (b.active) {
                            if (ball.x > b.x && ball.x < b.x + brickWidth && ball.y > b.y && ball.y < b.y + brickHeight) {
                                ball.vy = -ball.vy;
                                b.active = 0;
                                score += 15;
                                scoreBoard.textContent = "Score: " + score;
                            }
                        }
                    }
                }
            }

            // Draw Bricks
            for (let r = 0; r < brickRows; r++) {
                for (let c = 0; c < brickCols; c++) {
                    let b = bricks[r][c];
                    if (b.active) {
                        b.x = c * (brickWidth + brickPadding) + brickOffsetLeft;
                        b.y = r * (brickHeight + brickPadding) + brickOffsetTop;
                        ctx.fillStyle = b.color;
                        ctx.fillRect(b.x, b.y, brickWidth, brickHeight);
                    }
                }
            }

            // Draw Paddle
            ctx.fillStyle = paddle.color;
            ctx.fillRect(paddle.x, paddle.y, paddle.width, paddle.height);

            // Draw Ball
            ctx.fillStyle = ball.color;
            ctx.beginPath();
            ctx.arc(ball.x, ball.y, ball.r, 0, Math.PI * 2);
            ctx.fill();

            requestAnimationFrame(loop);
        }

        document.getElementById("playBtn").addEventListener("click", startGame);
        document.getElementById("restartBtn").addEventListener("click", startGame);
        
        loop();
    </script>
</body>
</html>
        """
    )

    private fun getReactionGame() = Artifact(
        id = "reaction_speed",
        title = "Speed Tap Reflex Grid",
        type = "game",
        iconName = "flash_on",
        description = "A reflex tester applet featuring dynamic high-speed grid lights, response logs in milliseconds, score logs, and leaderboard summaries.",
        code = """
<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
    <style>
        body {
            margin: 0;
            padding: 0;
            background: #09090b;
            color: #fff;
            font-family: 'Segoe UI', system-ui, sans-serif;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            height: 100vh;
            overflow: hidden;
            user-select: none;
        }
        #game {
            width: 100%;
            max-width: 340px;
            padding: 16px;
            box-sizing: border-box;
            text-align: center;
        }
        .status-box {
            background: #18181b;
            border: 1px solid #27272a;
            border-radius: 12px;
            padding: 20px;
            margin-bottom: 20px;
            min-height: 80px;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            transition: background 0.15s;
        }
        h2 { font-size: 1.15rem; color:#a1a1aa; margin: 0 0 6px 0; }
        .score { font-size: 2rem; font-weight: 800; color: #fbbf24; }
        .grid {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 12px;
            margin-bottom: 20px;
        }
        .pad {
            background: #18181b;
            aspect-ratio: 1;
            border: 1.5px solid #27272a;
            border-radius: 16px;
            cursor: pointer;
            transition: background-color 0.05s, border-color 0.05s, transform 0.08s;
        }
        .pad:active { transform: scale(0.92); }
        .pad.active {
            background: #fbbf24;
            border-color: #fbbf24;
            box-shadow: 0 0 15px #fbbf24;
        }
        button {
            background: #27272a;
            border: 1px solid #3f3f46;
            color: #fff;
            padding: 12px;
            border-radius: 8px;
            font-weight: bold;
            cursor: pointer;
            width: 100%;
        }
    </style>
</head>
<body>
    <div id="game">
        <div class="status-box" id="stateCard" onclick="handleCardClick()">
            <h2 id="cardTitle">Speed Tap Reflex</h2>
            <div class="score" id="cardScore">TAP TO START</div>
        </div>
        
        <div class="grid" id="reflexGrid">
            <div class="pad" data-id="0"></div>
            <div class="pad" data-id="1"></div>
            <div class="pad" data-id="2"></div>
            <div class="pad" data-id="3"></div>
            <div class="pad" data-id="4"></div>
            <div class="pad" data-id="5"></div>
            <div class="pad" data-id="6"></div>
            <div class="pad" data-id="7"></div>
            <div class="pad" data-id="8"></div>
        </div>
        
        <button id="reset" onclick="resetGame()">RESET STATS</button>
    </div>

    <script>
        const pads = document.querySelectorAll(".pad");
        const scoreEl = document.getElementById("cardScore");
        const titleEl = document.getElementById("cardTitle");
        const stateCard = document.getElementById("stateCard");

        let activePadIndex = null;
        let startTime = null;
        let timer = null;
        let bestMs = parseInt(localStorage.getItem("reflex_best") || "9999");
        let isWaitingForGrid = false;
        let isStarted = false;

        function resetGame() {
            bestMs = 9999;
            localStorage.removeItem("reflex_best");
            scoreEl.textContent = "TAP TO START";
            titleEl.textContent = "Speed Tap Reflex";
            pads.forEach(p => p.classList.remove("active"));
            isStarted = false;
            isWaitingForGrid = false;
            if (timer) clearTimeout(timer);
        }

        function handleCardClick() {
            if (isStarted) return;
            startRound();
        }

        function startRound() {
            isStarted = true;
            pads.forEach(p => p.classList.remove("active"));
            scoreEl.textContent = "WAIT FOR THE LIGHT...";
            titleEl.textContent = "Get Ready";
            stateCard.style.background = "#18181b";

            // Random delay between 1.5s and 4.5s
            const delay = 1500 + Math.random() * 3000;
            timer = setTimeout(lightUpPad, delay);
        }

        function lightUpPad() {
            activePadIndex = Math.floor(Math.random() * 9);
            pads[activePadIndex].classList.add("active");
            startTime = performance.now();
            scoreEl.textContent = "TAP!";
            stateCard.style.background = "rgba(251,191,36,0.1)";
            isWaitingForGrid = true;
        }

        // Tap listener for grid
        pads.forEach(p => {
            p.addEventListener("click", (e) => {
                const id = parseInt(e.target.getAttribute("data-id"));
                if (!isWaitingForGrid) {
                    // Early Tap penalty!
                    if (isStarted) {
                        clearTimeout(timer);
                        scoreEl.textContent = "TOO EARLY! FAIL";
                        scoreEl.style.color = "#ef4444";
                        isStarted = false;
                        setTimeout(() => { scoreEl.style.color = "#fbbf24"; scoreEl.textContent = "TAP TO RESTART"; }, 1500);
                    }
                    return;
                }

                if (id === activePadIndex) {
                    const elapsed = Math.round(performance.now() - startTime);
                    isWaitingForGrid = false;
                    isStarted = false;
                    pads[activePadIndex].classList.remove("active");

                    if (elapsed < bestMs) {
                        bestMs = elapsed;
                        localStorage.setItem("reflex_best", bestMs.toString());
                    }

                    titleEl.textContent = `Reaction Speed (Best: ${'$'}{bestMs === 9999 ? "N/A" : bestMs + "ms"})`;
                    scoreEl.textContent = `${'$'}{elapsed} ms`;
                }
            });
        });

        if (bestMs !== 9999) {
            titleEl.textContent = `Reaction Speed (Best: ${'$'}{bestMs}ms)`;
        }
    </script>
</body>
</html>
        """
    )
}
