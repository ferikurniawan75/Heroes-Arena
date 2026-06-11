package com.heroesarena.ui

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.heroesarena.R
import com.heroesarena.engine.GameView
import com.heroesarena.models.GameMode
import com.heroesarena.models.HeroRegistry

/**
 * Main game activity - hosts the GameView
 */
class GameActivity : AppCompatActivity() {

    private lateinit var gameView: GameView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val heroId = intent.getStringExtra("hero_id") ?: "blade_dancer"
        val modeName = intent.getStringExtra("game_mode") ?: "CLASSIC"
        val hero = HeroRegistry.getHeroById(heroId) ?: HeroRegistry.getAllHeroes().first()
        val mode = try { GameMode.valueOf(modeName) } catch (e: Exception) { GameMode.CLASSIC }

        gameView = GameView(this)
        setContentView(gameView)

        gameView.startGame(mode, hero)
    }

    override fun onPause() {
        super.onPause()
        gameView.pauseGame()
    }

    override fun onResume() {
        super.onResume()
        gameView.resumeGame()
    }

    override fun onDestroy() {
        super.onDestroy()
        gameView.stopGame()
    }

    override fun onBackPressed() {
        // Show pause instead of exiting
        gameView.pauseGame()
        super.onBackPressed()
    }
}
