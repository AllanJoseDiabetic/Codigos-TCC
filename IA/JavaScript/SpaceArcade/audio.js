(function () {
    "use strict";

    const SAMPLE_RATE = 22050;
    const TWO_PI = Math.PI * 2;

    const SOUND_CONFIG = {
        shoot: {
            duration: 0.14,
            volume: 0.28
        },
        explosion: {
            duration: 0.48,
            volume: 0.38
        },
        music: {
            duration: 4,
            volume: 0.20
        },
        gameover: {
            duration: 0.90,
            volume: 0.48
        }
    };

    function writeText(view, offset, text) {
        for (let i = 0; i < text.length; i++) {
            view.setUint8(
                offset + i,
                text.charCodeAt(i)
            );
        }
    }

    function createNoiseGenerator() {
        let seed = 123456789;

        return function () {
            seed ^= seed << 13;
            seed ^= seed >>> 17;
            seed ^= seed << 5;

            return (
                (seed >>> 0) / 4294967296
            ) * 2 - 1;
        };
    }

    function sampleSound(name, time, noise) {
        if (name === "shoot") {
            const duration = SOUND_CONFIG.shoot.duration;
            const progress = time / duration;

            const frequency =
                950 - progress * 650;

            const envelope = Math.pow(
                Math.max(0, 1 - progress),
                2
            );

            return Math.sin(
                TWO_PI * frequency * time
            ) * envelope * 0.8;
        }

        if (name === "explosion") {
            const duration =
                SOUND_CONFIG.explosion.duration;

            const progress = time / duration;

            const envelope = Math.pow(
                Math.max(0, 1 - progress),
                2
            );

            const lowTone = Math.sin(
                TWO_PI * (90 - progress * 55) * time
            );

            return (
                noise() * 0.78 +
                lowTone * 0.22
            ) * envelope;
        }

        if (name === "music") {
            const melody = [
                196.00,
                246.94,
                293.66,
                392.00,
                349.23,
                293.66,
                246.94,
                196.00,
                220.00,
                261.63,
                329.63,
                440.00,
                392.00,
                329.63,
                261.63,
                220.00
            ];

            const noteDuration = 0.25;

            const index = Math.floor(
                time / noteDuration
            ) % melody.length;

            const localTime =
                time % noteDuration;

            const frequency = melody[index];

            const envelope = Math.min(
                1,
                localTime * 45
            ) * Math.max(
                0,
                1 - localTime / 0.23
            );

            const lead =
                Math.sin(TWO_PI * frequency * time) *
                0.36 +
                Math.sin(
                    TWO_PI * frequency * 2 * time
                ) * 0.09;

            const bassFrequency =
                index < 8 ? 98 : 110;

            const bass =
                Math.sin(
                    TWO_PI * bassFrequency * time
                ) * 0.13;

            const pulse =
                Math.sin(
                    TWO_PI * 55 * time
                ) * 0.06;

            return (
                lead * envelope +
                bass +
                pulse
            );
        }

        if (name === "gameover") {
            const duration =
                SOUND_CONFIG.gameover.duration;

            const progress = time / duration;

            const frequency =
                350 - 260 * progress;

            const envelope = Math.pow(
                Math.max(0, 1 - progress),
                1.6
            );

            const tone =
                Math.sin(
                    TWO_PI * frequency * time
                ) * 0.7 +
                Math.sin(
                    TWO_PI * frequency * 0.5 * time
                ) * 0.3;

            return tone * envelope;
        }

        return 0;
    }

    function generateWaveFile(name) {
        const settings = SOUND_CONFIG[name];

        const sampleCount = Math.floor(
            settings.duration * SAMPLE_RATE
        );

        const bytesPerSample = 2;

        const buffer = new ArrayBuffer(
            44 + sampleCount * bytesPerSample
        );

        const view = new DataView(buffer);

        writeText(view, 0, "RIFF");

        view.setUint32(
            4,
            36 + sampleCount * bytesPerSample,
            true
        );

        writeText(view, 8, "WAVE");
        writeText(view, 12, "fmt ");

        view.setUint32(16, 16, true);
        view.setUint16(20, 1, true);
        view.setUint16(22, 1, true);

        view.setUint32(
            24,
            SAMPLE_RATE,
            true
        );

        view.setUint32(
            28,
            SAMPLE_RATE * bytesPerSample,
            true
        );

        view.setUint16(32, bytesPerSample, true);
        view.setUint16(34, 16, true);

        writeText(view, 36, "data");

        view.setUint32(
            40,
            sampleCount * bytesPerSample,
            true
        );

        const noise = createNoiseGenerator();

        for (let i = 0; i < sampleCount; i++) {
            const time = i / SAMPLE_RATE;

            let value = sampleSound(
                name,
                time,
                noise
            );

            value = Math.max(
                -1,
                Math.min(1, value)
            );

            const integerSample = value < 0
                ? Math.round(value * 32768)
                : Math.round(value * 32767);

            view.setInt16(
                44 + i * bytesPerSample,
                integerSample,
                true
            );
        }

        const blob = new Blob(
            [buffer],
            {
                type: "audio/wav"
            }
        );

        return URL.createObjectURL(blob);
    }

    function safePlay(audio) {
        try {
            const result = audio.play();

            if (
                result &&
                typeof result.catch === "function"
            ) {
                result.catch(function () {
                    return null;
                });
            }
        } catch (error) {
            return;
        }
    }

    class SoundLibrary {
        constructor() {
            this.tracks = {};
            this.pools = {};
            this.objectUrls = [];
            this.failedSounds = [];
        }

        load(onProgress) {
            const names = [
                "shoot",
                "explosion",
                "music",
                "gameover"
            ];

            let finished = 0;

            const promises = names.map((name) => {
                return new Promise((resolve) => {
                    let url;

                    try {
                        url = generateWaveFile(name);
                    } catch (error) {
                        this.failedSounds.push(name);

                        finished++;

                        onProgress(
                            finished,
                            names.length
                        );

                        resolve(false);
                        return;
                    }

                    this.objectUrls.push(url);

                    const audio = new Audio();

                    audio.src = url;
                    audio.preload = "auto";

                    audio.volume =
                        SOUND_CONFIG[name].volume;

                    this.tracks[name] = audio;

                    if (name !== "music") {
                        this.pools[name] = [];

                        for (let i = 0; i < 5; i++) {
                            const instance = new Audio(url);

                            instance.preload = "auto";
                            instance.volume =
                                SOUND_CONFIG[name].volume;

                            this.pools[name].push(instance);
                        }
                    }

                    let completed = false;
                    let timeoutId = null;

                    const finish = (success) => {
                        if (completed) {
                            return;
                        }

                        completed = true;

                        clearTimeout(timeoutId);

                        if (!success) {
                            this.failedSounds.push(name);
                        }

                        finished++;

                        onProgress(
                            finished,
                            names.length
                        );

                        resolve(success);
                    };

                    audio.addEventListener(
                        "loadeddata",
                        function () {
                            finish(true);
                        },
                        {
                            once: true
                        }
                    );

                    audio.addEventListener(
                        "error",
                        function () {
                            finish(false);
                        },
                        {
                            once: true
                        }
                    );

                    timeoutId = setTimeout(
                        function () {
                            finish(false);
                        },
                        4000
                    );

                    audio.load();
                });
            });

            return Promise.all(promises).then(
                (results) => {
                    return results.every(
                        (success) => success
                    );
                }
            );
        }

        playEffect(name) {
            const pool = this.pools[name];

            if (!pool || pool.length === 0) {
                return;
            }

            let chosen = null;

            for (let i = 0; i < pool.length; i++) {
                if (
                    pool[i].paused ||
                    pool[i].ended
                ) {
                    chosen = pool[i];
                    break;
                }
            }

            if (chosen === null) {
                chosen = pool[0];
                chosen.pause();
            }

            try {
                chosen.currentTime = 0;
            } catch (error) {
                return;
            }

            safePlay(chosen);
        }

        startMusic() {
            const music = this.tracks.music;

            if (!music) {
                return;
            }

            music.loop = true;

            try {
                music.currentTime = 0;
            } catch (error) {
                return;
            }

            safePlay(music);
        }

        stopMusic() {
            const music = this.tracks.music;

            if (!music) {
                return;
            }

            music.pause();

            try {
                music.currentTime = 0;
            } catch (error) {
                return;
            }
        }

        dispose() {
            this.stopMusic();

            Object.keys(this.tracks).forEach(
                (name) => {
                    this.tracks[name].pause();
                    this.tracks[name].removeAttribute(
                        "src"
                    );
                    this.tracks[name].load();
                }
            );

            Object.keys(this.pools).forEach(
                (name) => {
                    this.pools[name].forEach(
                        (audio) => {
                            audio.pause();
                            audio.removeAttribute("src");
                            audio.load();
                        }
                    );
                }
            );

            this.objectUrls.forEach(
                (url) => {
                    URL.revokeObjectURL(url);
                }
            );

            this.objectUrls = [];
        }
    }

    window.SoundLibrary = SoundLibrary;
})();