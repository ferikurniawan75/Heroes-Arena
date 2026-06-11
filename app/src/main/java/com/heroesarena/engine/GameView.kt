package com.heroesarena.engine

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.heroesarena.models.GameMode
import com.heroesarena.models.Hero
import com.heroesarena.models.SkillType

/**
 * Custom SurfaceView for game rendering
 */
class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {

    private val engine = GameEngine()
    private var gameLoop: GameLoop? = null
    private var isSurfaceReady = false

    // Joystick state
    private var joystickActive = false
    private var joystickCenterX = 0f
    private var joystickCenterY = 0f
    private var joystickTouchId = -1

    // Skill button positions
    private var skillButtonRects = mutableListOf<Pair<android.graphics.RectF, SkillType>>()

    init {
        holder.addCallback(this)
        isFocusable = true
        keepScreenOn = true
    }

    fun startGame(mode: GameMode, hero: Hero) {
        engine.startGame(mode, hero)
        gameLoop = GameLoop(engine, object : GameLoop.GameLoopCallback {
            override fun onFrame() {
                renderFrame()
            }
        })
        gameLoop?.start()
    }

    fun stopGame() {
        gameLoop?.stop()
        gameLoop = null
    }

    fun pauseGame() {
        engine.isPaused = true
    }

    fun resumeGame() {
        engine.isPaused = false
    }

    private fun renderFrame() {
        if (!isSurfaceReady) return
        val canvas = holder.lockCanvas() ?: return
        try {
            canvas.drawColor(Color.BLACK)
            engine.render(canvas, width, height)

            // Draw joystick overlay
            if (joystickActive) {
                drawJoystick(canvas)
            }

            // Draw skill buttons overlay
            drawSkillButtons(canvas)

            // Draw pause indicator
            if (engine.isPaused) {
                val paint = android.graphics.Paint().apply {
                    color = Color.argb(150, 0, 0, 0)
                }
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
                paint.color = Color.WHITE
                paint.textSize = 48f
                paint.textAlign = android.graphics.Paint.Align.CENTER
                canvas.drawText("PAUSED", width / 2f, height / 2f, paint)
            }

            // Game over screen
            if (engine.gameState == com.heroesarena.models.GameState.ENDED) {
                drawGameOver(canvas)
            }
        } finally {
            holder.unlockCanvasAndPost(canvas)
        }
    }

    private fun drawJoystick(canvas: Canvas) {
        val paint = android.graphics.Paint().apply {
            color = Color.argb(60, 255, 255, 255)
            style = android.graphics.Paint.Style.STROKE
            strokeWidth = 3f
            isAntiAlias = true
        }

        // Outer circle
        canvas.drawCircle(joystickCenterX, joystickCenterY, 60f, paint)

        // Inner circle (thumb)
        paint.style = android.graphics.Paint.Style.FILL
        paint.alpha = 100
        canvas.drawCircle(joystickCenterX, joystickCenterY, 20f, paint)
    }

    private fun drawSkillButtons(canvas: Canvas) {
        skillButtonRects.clear()
        val skillY = height - 70f
        val startSkillX = width - 220f
        val skills = listOf(SkillType.SKILL_1, SkillType.SKILL_2, SkillType.SKILL_3, SkillType.ULTIMATE)

        skills.forEachIndexed { i, type ->
            val sx = startSkillX + i * 52f
            val rect = android.graphics.RectF(sx - 24, skillY - 24, sx + 24, skillY + 24)
            skillButtonRects.add(rect to type)

            val paint = android.graphics.Paint().apply {
                color = when (type) {
                    SkillType.ULTIMATE -> Color.argb(100, 156, 39, 176)
                    else -> Color.argb(100, 33, 150, 243)
                }
                isAntiAlias = true
            }

            canvas.drawCircle(sx, skillY, 24f, paint)

            // Border
            paint.style = android.graphics.Paint.Style.STROKE
            paint.color = Color.argb(150, 255, 255, 255)
            paint.strokeWidth = 2f
            canvas.drawCircle(sx, skillY, 24f, paint)
        }
    }

    private fun drawGameOver(canvas: Canvas) {
        val paint = android.graphics.Paint().apply {
            color = Color.argb(180, 0, 0, 0)
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

        paint.textAlign = android.graphics.Paint.Align.CENTER
        paint.textSize = 64f
        paint.isAntiAlias = true

        val winner = engine.winningTeam
        if (winner == com.heroesarena.models.Team.BLUE) {
            paint.color = Color.rgb(66, 165, 245)
            canvas.drawText("VICTORY", width / 2f, height / 2f - 40, paint)
        } else {
            paint.color = Color.rgb(239, 83, 80)
            canvas.drawText("DEFEAT", width / 2f, height / 2f - 40, paint)
        }

        paint.textSize = 24f
        paint.color = Color.WHITE
        canvas.drawText("Tap to return to menu", width / 2f, height / 2f + 40, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                val x = event.x
                val y = event.y

                // Check skill buttons
                val clickedSkill = skillButtonRects.find { it.first.contains(x, y) }?.second
                if (clickedSkill != null) {
                    engine.playerUseSkill(clickedSkill)
                    return true
                }

                // Start joystick (left side of screen)
                if (x < width * 0.4f) {
                    joystickActive = true
                    joystickCenterX = x
                    joystickCenterY = y
                    joystickTouchId = event.getPointerId(0)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (joystickActive) {
                    val pointerIndex = event.findPointerIndex(joystickTouchId)
                    if (pointerIndex >= 0) {
                        val dx = event.getX(pointerIndex) - joystickCenterX
                        val dy = event.getY(pointerIndex) - joystickCenterY
                        engine.movePlayer(dx, dy, 1f / 60f)
                    }
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                joystickActive = false
                joystickTouchId = -1
            }
        }
        return true
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        isSurfaceReady = true
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        isSurfaceReady = true
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        isSurfaceReady = false
        stopGame()
    }
}

