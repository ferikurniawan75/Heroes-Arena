package com.heroesarena.engine

/**
 * Game Loop - manages the update/render cycle at target FPS
 */
class GameLoop(
    private val engine: GameEngine,
    private val callback: GameLoopCallback
) {
    private var isRunning = false
    private var targetFPS = 60
    private var lastFrameTime: Long = 0

    interface GameLoopCallback {
        fun onFrame()
    }

    fun start() {
        isRunning = true
        lastFrameTime = System.nanoTime()
        Thread {
            while (isRunning) {
                val currentTime = System.nanoTime()
                val deltaTime = (currentTime - lastFrameTime) / 1_000_000_000f
                lastFrameTime = currentTime

                if (deltaTime <= 0.1f) { // Cap to prevent physics explosion
                    engine.update(deltaTime)
                    callback.onFrame()
                }

                // Frame rate control
                val frameTime = (System.nanoTime() - currentTime) / 1_000_000f
                val sleepTime = (1000f / targetFPS - frameTime).toLong()
                if (sleepTime > 0) {
                    try { Thread.sleep(sleepTime) } catch (e: InterruptedException) { break }
                }
            }
        }.start()
    }

    fun stop() {
        isRunning = false
    }

    fun setTargetFPS(fps: Int) {
        targetFPS = fps.coerceIn(30, 120)
    }

    fun isRunning(): Boolean = isRunning
}
