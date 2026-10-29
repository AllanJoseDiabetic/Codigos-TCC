(function () {
    "use strict";

    const WIDTH = 600;
    const HEIGHT = 360;

    const PLAYER_MIN_Y = 273;
    const PLAYER_MAX_Y = 338;

    function clamp(value, minimum, maximum) {
        return Math.max(
            minimum,
            Math.min(maximum, value)
        );
    }

    function intersects(a, b) {
        return (
            Math.abs(a.x - b.x) <
                (a.w + b.w) / 2 &&
            Math.abs(a.y - b.y) <
                (a.h + b.h) / 2
        );
    }

    class SpaceGame {
        constructor() {
            this.canvas = document.getElementById(
                "gameCanvas"
            );

            this.ctx = this.canvas.getContext("2d");

            this.scoreElement = document.getElementById(
                "score"
            );

            this.waveElement = document.getElementById(
                "wave"
            );

            this.overlay = document.getElementById(
                "overlay"
            );

            this.overlayTitle = document.getElementById(
                "overlayTitle"
            );

            this.overlayMessage = document.getElementById(
                "overlayMessage"
            );

            this.actionButton = document.getElementById(
                "actionButton"
            );

            this.loadingTrack = document.getElementById(
                "loadingTrack"
            );

            this.loadingProgress = document.getElementById(
                "loadingProgress"
            );

            this.audioStatus = document.getElementById(
                "audioStatus"
            );

            this.sounds = new SoundLibrary();

            this.state = "loading";

            this.keys = new Set();

            this.score = 0;
            this.wave = 0;

            this.stars = [];
            this.enemies = [];
            this.playerBullets = [];
            this.enemyBullets = [];

            this.player = {
                x: WIDTH / 2,
                y: 326,
                w: 26,
                h: 24
            };

            this.enemyDirection = 1;
            this.enemyShotTimer = 1;
            this.playerShotTimer = 0;

            this.seed = 123456789;
            this.elapsedTime = 0;

            this.previousFrame = 0;

            this.installEvents();

            this.createStars();

            this.updateHud();

            this.beginLoading();

            requestAnimationFrame(
                (timestamp) => this.frame(timestamp)
            );
        }

        random() {
            this.seed = (
                Math.imul(
                    this.seed,
                    1664525
                ) + 1013904223
            ) >>> 0;

            return this.seed / 4294967296;
        }

        createStars() {
            this.stars = [];

            for (let i = 0; i < 90; i++) {
                this.stars.push({
                    x: this.random() * WIDTH,
                    y: this.random() * HEIGHT,
                    speed: 12 + this.random() * 48,
                    size: 0.7 + this.random() * 1.5,
                    alpha: 0.3 + this.random() * 0.7
                });
            }
        }

        installEvents() {
            const controlledKeys = [
                "ArrowUp",
                "ArrowDown",
                "ArrowLeft",
                "ArrowRight",
                "Space"
            ];

            window.addEventListener(
                "keydown",
                (event) => {
                    if (
                        controlledKeys.indexOf(event.code) === -1
                    ) {
                        return;
                    }

                    if (this.state === "playing") {
                        event.preventDefault();

                        this.keys.add(event.code);
                    }
                }
            );

            window.addEventListener(
                "keyup",
                (event) => {
                    this.keys.delete(event.code);
                }
            );

            window.addEventListener(
                "blur",
                () => {
                    this.keys.clear();
                }
            );

            this.actionButton.addEventListener(
                "click",
                () => {
                    if (
                        this.state === "ready" ||
                        this.state === "over"
                    ) {
                        this.startGame();
                    }
                }
            );

            window.addEventListener(
                "beforeunload",
                () => {
                    this.sounds.dispose();
                }
            );
        }

        beginLoading() {
            this.showOverlay(
                "CARREGANDO",
                "Preparando os recursos sonoros.",
                "",
                true
            );

            this.audioStatus.textContent =
                "Carregando os quatro sons originais.";

            this.sounds.load(
                (completed, total) => {
                    const percentage =
                        completed / total * 100;

                    this.loadingProgress.style.width =
                        percentage + "%";

                    this.overlayMessage.textContent =
                        "Recursos preparados: " +
                        completed + " de " + total + ".";
                }
            ).then(
                (allReady) => {
                    this.finishLoading(allReady);
                }
            ).catch(
                () => {
                    this.finishLoading(false);
                }
            );
        }

        finishLoading(allReady) {
            if (this.state !== "loading") {
                return;
            }

            this.state = "ready";

            if (allReady) {
                this.audioStatus.textContent =
                    "Áudio preparado. A reprodução " +
                    "será solicitada após iniciar a partida.";
            } else {
                this.audioStatus.textContent =
                    "Um ou mais sons não puderam ser " +
                    "preparados. O jogo pode funcionar " +
                    "com áudio parcial ou sem áudio.";
            }

            this.showOverlay(
                "PRONTO PARA JOGAR",
                "Use as setas para mover a nave. " +
                "Segure a barra de espaço para disparar. " +
                "Destrua os inimigos e evite colisões.",
                "INICIAR PARTIDA",
                false
            );
        }

        showOverlay(
            title,
            message,
            buttonText,
            showLoading
        ) {
            this.overlay.classList.remove("hidden");

            this.overlayTitle.textContent = title;
            this.overlayMessage.textContent = message;

            this.loadingTrack.hidden = !showLoading;

            if (buttonText) {
                this.actionButton.hidden = false;
                this.actionButton.textContent = buttonText;
            } else {
                this.actionButton.hidden = true;
            }
        }

        hideOverlay() {
            this.overlay.classList.add("hidden");
        }

        resetWorld() {
            this.seed = 123456789;

            this.score = 0;
            this.wave = 0;
            this.elapsedTime = 0;

            this.player = {
                x: WIDTH / 2,
                y: 326,
                w: 26,
                h: 24
            };

            this.enemies = [];
            this.playerBullets = [];
            this.enemyBullets = [];

            this.enemyDirection = 1;
            this.playerShotTimer = 0;
            this.enemyShotTimer = 1;

            this.keys.clear();

            this.createStars();
            this.createWave();

            this.updateHud();
        }

        startGame() {
            this.sounds.stopMusic();

            this.resetWorld();

            this.state = "playing";

            this.hideOverlay();

            this.previousFrame = performance.now();

            this.sounds.startMusic();
        }

        createWave() {
            this.wave++;

            const columns = Math.min(
                5 + Math.floor((this.wave - 1) / 2),
                8
            );

            const rows = Math.min(
                2 + Math.floor((this.wave - 1) / 4),
                3
            );

            const spacingX = 58;
            const spacingY = 36;

            const firstX = (
                WIDTH - (columns - 1) * spacingX
            ) / 2;

            this.enemies = [];

            for (let row = 0; row < rows; row++) {
                for (
                    let column = 0;
                    column < columns;
                    column++
                ) {
                    this.enemies.push({
                        x: firstX + column * spacingX,
                        y: 61 + row * spacingY,
                        w: 28,
                        h: 22,
                        type: row % 3
                    });
                }
            }

            this.enemyDirection =
                this.wave % 2 === 0 ? -1 : 1;

            this.enemyShotTimer = 1.1;

            this.updateHud();
        }

        updateHud() {
            let scoreText = String(this.score);

            while (scoreText.length < 6) {
                scoreText = "0" + scoreText;
            }

            this.scoreElement.textContent = scoreText;

            this.waveElement.textContent =
                this.wave < 10
                    ? "0" + this.wave
                    : String(this.wave);
        }

        frame(timestamp) {
            if (this.previousFrame === 0) {
                this.previousFrame = timestamp;
            }

            const elapsed = (
                timestamp - this.previousFrame
            ) / 1000;

            this.previousFrame = timestamp;

            const deltaTime = Math.min(
                Math.max(elapsed, 0),
                0.04
            );

            if (this.state === "playing") {
                this.update(deltaTime);
            }

            this.draw();

            requestAnimationFrame(
                (nextTimestamp) => this.frame(nextTimestamp)
            );
        }

        update(deltaTime) {
            this.elapsedTime += deltaTime;

            this.updateStars(deltaTime);
            this.updatePlayer(deltaTime);

            this.updateEnemies(deltaTime);

            this.updatePlayerBullets(deltaTime);

            if (this.state !== "playing") {
                return;
            }

            this.updateEnemyBullets(deltaTime);

            if (this.state !== "playing") {
                return;
            }

            this.checkEnemyCollisions();

            if (this.state !== "playing") {
                return;
            }

            if (this.enemies.length === 0) {
                this.createWave();
            }
        }

        updateStars(deltaTime) {
            for (const star of this.stars) {
                star.y += star.speed * deltaTime;

                if (star.y > HEIGHT) {
                    star.y = 0;
                    star.x = this.random() * WIDTH;
                }
            }
        }

        updatePlayer(deltaTime) {
            let movementX = 0;
            let movementY = 0;

            if (this.keys.has("ArrowLeft")) {
                movementX--;
            }

            if (this.keys.has("ArrowRight")) {
                movementX++;
            }

            if (this.keys.has("ArrowUp")) {
                movementY--;
            }

            if (this.keys.has("ArrowDown")) {
                movementY++;
            }

            if (
                movementX !== 0 ||
                movementY !== 0
            ) {
                const magnitude = Math.sqrt(
                    movementX * movementX +
                    movementY * movementY
                );

                movementX /= magnitude;
                movementY /= magnitude;

                const playerSpeed = 210;

                this.player.x +=
                    movementX * playerSpeed * deltaTime;

                this.player.y +=
                    movementY * playerSpeed * deltaTime;
            }

            this.player.x = clamp(
                this.player.x,
                this.player.w / 2 + 3,
                WIDTH - this.player.w / 2 - 3
            );

            this.player.y = clamp(
                this.player.y,
                PLAYER_MIN_Y,
                PLAYER_MAX_Y
            );

            this.playerShotTimer -= deltaTime;

            if (
                this.keys.has("Space") &&
                this.playerShotTimer <= 0
            ) {
                this.firePlayerBullet();

                this.playerShotTimer = 0.20;
            }
        }

        firePlayerBullet() {
            this.playerBullets.push({
                x: this.player.x,
                y: this.player.y - 17,
                w: 5,
                h: 13,
                speed: 370
            });

            this.sounds.playEffect("shoot");
        }

        updateEnemies(deltaTime) {
            if (this.enemies.length === 0) {
                return;
            }

            const movementSpeed = Math.min(
                25 + this.wave * 7,
                100
            );

            for (const enemy of this.enemies) {
                enemy.x +=
                    this.enemyDirection *
                    movementSpeed *
                    deltaTime;
            }

            let touchedBoundary = false;

            for (const enemy of this.enemies) {
                if (
                    enemy.x - enemy.w / 2 < 12 ||
                    enemy.x + enemy.w / 2 > WIDTH - 12
                ) {
                    touchedBoundary = true;
                    break;
                }
            }

            if (touchedBoundary) {
                this.enemyDirection *= -1;

                for (const enemy of this.enemies) {
                    enemy.x = clamp(
                        enemy.x,
                        enemy.w / 2 + 12,
                        WIDTH - enemy.w / 2 - 12
                    );

                    enemy.y = Math.min(
                        enemy.y + 11,
                        260
                    );
                }
            }

            this.enemyShotTimer -= deltaTime;

            if (this.enemyShotTimer <= 0) {
                this.fireEnemyBullet();

                const normalInterval = Math.max(
                    0.34,
                    1.35 - this.wave * 0.075
                );

                this.enemyShotTimer =
                    normalInterval *
                    (0.75 + this.random() * 0.5);
            }
        }

        fireEnemyBullet() {
            if (this.enemies.length === 0) {
                return;
            }

            const index = Math.floor(
                this.random() * this.enemies.length
            );

            const enemy = this.enemies[index];

            const differenceX =
                this.player.x - enemy.x;

            const differenceY =
                this.player.y - enemy.y;

            const distance = Math.max(
                1,
                Math.sqrt(
                    differenceX * differenceX +
                    differenceY * differenceY
                )
            );

            const speed = Math.min(
                125 + this.wave * 8,
                220
            );

            this.enemyBullets.push({
                x: enemy.x,
                y: enemy.y + 14,
                w: 6,
                h: 11,
                vx: differenceX / distance * speed,
                vy: differenceY / distance * speed
            });
        }

        updatePlayerBullets(deltaTime) {
            for (
                let i = this.playerBullets.length - 1;
                i >= 0;
                i--
            ) {
                const bullet = this.playerBullets[i];

                bullet.y -= bullet.speed * deltaTime;

                if (bullet.y + bullet.h / 2 < 0) {
                    this.playerBullets.splice(i, 1);
                    continue;
                }

                let hitEnemy = false;

                for (
                    let j = this.enemies.length - 1;
                    j >= 0;
                    j--
                ) {
                    if (
                        intersects(
                            bullet,
                            this.enemies[j]
                        )
                    ) {
                        this.playerBullets.splice(i, 1);
                        this.enemies.splice(j, 1);

                        this.score += 10;

                        this.updateHud();

                        this.sounds.playEffect(
                            "explosion"
                        );

                        hitEnemy = true;

                        break;
                    }
                }

                if (hitEnemy) {
                    continue;
                }
            }
        }

        updateEnemyBullets(deltaTime) {
            for (
                let i = this.enemyBullets.length - 1;
                i >= 0;
                i--
            ) {
                const bullet = this.enemyBullets[i];

                bullet.x += bullet.vx * deltaTime;
                bullet.y += bullet.vy * deltaTime;

                if (
                    bullet.y > HEIGHT + 20 ||
                    bullet.x < -20 ||
                    bullet.x > WIDTH + 20
                ) {
                    this.enemyBullets.splice(i, 1);
                    continue;
                }

                if (
                    intersects(
                        bullet,
                        this.player
                    )
                ) {
                    this.finishGame();
                    return;
                }
            }
        }

        checkEnemyCollisions() {
            for (const enemy of this.enemies) {
                if (
                    intersects(
                        enemy,
                        this.player
                    )
                ) {
                    this.finishGame();
                    return;
                }
            }
        }

        finishGame() {
            if (this.state !== "playing") {
                return;
            }

            this.state = "over";

            this.keys.clear();

            this.sounds.stopMusic();

            this.sounds.playEffect("gameover");

            this.showOverlay(
                "FIM DE JOGO",
                "Sua nave foi destruída. " +
                "Pontuação final: " +
                this.score +
                " pontos. " +
                "Onda alcançada: " +
                this.wave + ".",
                "JOGAR NOVAMENTE",
                false
            );
        }

        draw() {
            const ctx = this.ctx;

            ctx.clearRect(
                0,
                0,
                WIDTH,
                HEIGHT
            );

            this.drawBackground(ctx);

            if (
                this.state === "playing" ||
                this.state === "over"
            ) {
                this.drawEnemies(ctx);
                this.drawPlayerBullets(ctx);
                this.drawEnemyBullets(ctx);
                this.drawPlayer(ctx);
            }
        }

        drawBackground(ctx) {
            const background = ctx.createLinearGradient(
                0,
                0,
                0,
                HEIGHT
            );

            background.addColorStop(
                0,
                "#040918"
            );

            background.addColorStop(
                0.55,
                "#111c3b"
            );

            background.addColorStop(
                1,
                "#040917"
            );

            ctx.fillStyle = background;

            ctx.fillRect(
                0,
                0,
                WIDTH,
                HEIGHT
            );

            const nebula = ctx.createRadialGradient(
                300,
                165,
                10,
                300,
                165,
                270
            );

            nebula.addColorStop(
                0,
                "rgba(56, 75, 144, 0.14)"
            );

            nebula.addColorStop(
                1,
                "rgba(56, 75, 144, 0)"
            );

            ctx.fillStyle = nebula;

            ctx.fillRect(
                0,
                0,
                WIDTH,
                HEIGHT
            );

            for (const star of this.stars) {
                ctx.globalAlpha = star.alpha;

                ctx.fillStyle = "#d5ebff";

                ctx.fillRect(
                    star.x,
                    star.y,
                    star.size,
                    star.size + star.speed / 55
                );
            }

            ctx.globalAlpha = 1;

            ctx.strokeStyle =
                "rgba(85, 189, 231, 0.13)";

            ctx.lineWidth = 1;

            ctx.beginPath();

            ctx.moveTo(
                0,
                PLAYER_MIN_Y - 18
            );

            ctx.lineTo(
                WIDTH,
                PLAYER_MIN_Y - 18
            );

            ctx.stroke();

            ctx.fillStyle =
                "rgba(134, 202, 242, 0.35)";

            ctx.font = "10px monospace";

            ctx.fillText(
                "ZONA DA NAVE",
                12,
                HEIGHT - 10
            );
        }

        drawPlayer(ctx) {
            const player = this.player;

            ctx.save();

            ctx.translate(
                player.x,
                player.y
            );

            ctx.shadowColor = "#45dfff";
            ctx.shadowBlur = 14;

            ctx.fillStyle = "#67e8f9";

            ctx.beginPath();

            ctx.moveTo(0, -16);
            ctx.lineTo(13, 12);
            ctx.lineTo(6, 7);
            ctx.lineTo(-6, 7);
            ctx.lineTo(-13, 12);

            ctx.closePath();
            ctx.fill();

            ctx.shadowBlur = 0;

            ctx.fillStyle = "#155e91";

            ctx.beginPath();

            ctx.moveTo(0, -8);
            ctx.lineTo(5, 6);
            ctx.lineTo(-5, 6);

            ctx.closePath();
            ctx.fill();

            ctx.fillStyle = "#b5faff";

            ctx.fillRect(
                -2,
                -12,
                4,
                9
            );

            if (this.state === "playing") {
                const flameLength =
                    8 + Math.sin(
                        this.elapsedTime * 35
                    ) * 3;

                ctx.fillStyle = "#ffbf46";

                ctx.beginPath();

                ctx.moveTo(-5, 9);
                ctx.lineTo(0, 9 + flameLength);
                ctx.lineTo(5, 9);

                ctx.closePath();
                ctx.fill();
            }

            ctx.restore();
        }

        drawEnemies(ctx) {
            for (const enemy of this.enemies) {
                ctx.save();

                ctx.translate(
                    enemy.x,
                    enemy.y
                );

                const colors = [
                    "#fe6387",
                    "#ffb454",
                    "#b596ff"
                ];

                ctx.fillStyle = colors[enemy.type];

                ctx.shadowColor = colors[enemy.type];
                ctx.shadowBlur = 8;

                ctx.beginPath();

                ctx.moveTo(-13, -7);
                ctx.lineTo(-7, -12);
                ctx.lineTo(7, -12);
                ctx.lineTo(13, -7);
                ctx.lineTo(12, 6);
                ctx.lineTo(7, 6);
                ctx.lineTo(7, 11);
                ctx.lineTo(2, 7);
                ctx.lineTo(-2, 7);
                ctx.lineTo(-7, 11);
                ctx.lineTo(-7, 6);
                ctx.lineTo(-12, 6);

                ctx.closePath();
                ctx.fill();

                ctx.shadowBlur = 0;

                ctx.fillStyle = "#15162e";

                ctx.fillRect(
                    -8,
                    -4,
                    5,
                    5
                );

                ctx.fillRect(
                    3,
                    -4,
                    5,
                    5
                );

                ctx.restore();
            }
        }

        drawPlayerBullets(ctx) {
            ctx.save();

            ctx.fillStyle = "#a5fff1";

            ctx.shadowColor = "#55ffcf";
            ctx.shadowBlur = 9;

            for (const bullet of this.playerBullets) {
                ctx.fillRect(
                    bullet.x - bullet.w / 2,
                    bullet.y - bullet.h / 2,
                    bullet.w,
                    bullet.h
                );
            }

            ctx.restore();
        }

        drawEnemyBullets(ctx) {
            ctx.save();

            ctx.fillStyle = "#ff6b79";

            ctx.shadowColor = "#ff375e";
            ctx.shadowBlur = 8;

            for (const bullet of this.enemyBullets) {
                ctx.fillRect(
                    bullet.x - bullet.w / 2,
                    bullet.y - bullet.h / 2,
                    bullet.w,
                    bullet.h
                );
            }

            ctx.restore();
        }
    }

    const canvas = document.getElementById(
        "gameCanvas"
    );

    const context = canvas.getContext("2d");

    if (context) {
        new SpaceGame();
    } else {
        document.getElementById(
            "overlayTitle"
        ).textContent = "CANVAS INDISPONÍVEL";

        document.getElementById(
            "overlayMessage"
        ).textContent =
            "Este navegador não oferece o " +
            "contexto de renderização Canvas 2D.";

        document.getElementById(
            "loadingTrack"
        ).hidden = true;
    }
})();