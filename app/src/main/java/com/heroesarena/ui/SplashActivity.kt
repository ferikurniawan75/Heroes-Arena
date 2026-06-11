package com.heroesarena.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.heroesarena.R
import com.heroesarena.models.HeroRegistry
import com.heroesarena.models.ItemRegistry

/**
 * Splash screen with animations and loading
 */
class SplashActivity : AppCompatActivity() {

    private lateinit var logoText: TextView
    private lateinit var taglineText: TextView
    private lateinit var loadingBar: ProgressBar
    private lateinit var loadingText: TextView
    private lateinit var versionText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        logoText = findViewById(R.id.tvLogo)
        taglineText = findViewById(R.id.tvTagline)
        loadingBar = findViewById(R.id.progressBar)
        loadingText = findViewById(R.id.tvLoading)
        versionText = findViewById(R.id.tvVersion)

        versionText.text = "v1.0.0"

        // Start animations
        startSplashAnimation()

        // Load game data in background
        Handler(Looper.getMainLooper()).postDelayed({
            loadGameData()
        }, 500)
    }

    private fun startSplashAnimation() {
        // Logo animation
        val logoScaleX = ObjectAnimator.ofFloat(logoText, View.SCALE_X, 0f, 1f)
        val logoScaleY = ObjectAnimator.ofFloat(logoText, View.SCALE_Y, 0f, 1f)
        val logoAlpha = ObjectAnimator.ofFloat(logoText, View.ALPHA, 0f, 1f)

        // Tagline animation
        val tagAlpha = ObjectAnimator.ofFloat(taglineText, View.ALPHA, 0f, 1f)
        val tagTranslateY = ObjectAnimator.ofFloat(taglineText, View.TRANSLATION_Y, 30f, 0f)

        AnimatorSet().apply {
            play(logoScaleX).with(logoScaleY).with(logoAlpha)
            play(tagAlpha).with(tagTranslateY).after(logoScaleX)
            duration = 1000
            start()
        }
    }

    private fun loadGameData() {
        Thread {
            // Initialize registries
            HeroRegistry.init()
            ItemRegistry.init()

            // Simulate loading progress
            val steps = listOf(
                "Loading heroes..." to 20,
                "Loading items..." to 40,
                "Loading maps..." to 60,
                "Initializing engine..." to 80,
                "Ready!" to 100
            )

            steps.forEach { (text, progress) ->
                runOnUiThread {
                    loadingText.text = text
                    loadingBar.progress = progress
                }
                Thread.sleep(300)
            }

            // Navigate to main menu
            runOnUiThread {
                startActivity(Intent(this, MainActivity::class.java))
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }
        }.start()
    }
}
