package com.heroesarena.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.heroesarena.R
import com.heroesarena.models.GameSettingsData
import com.heroesarena.utils.GameSettings

/**
 * Main Menu - central hub for all game features
 */
class MainActivity : AppCompatActivity() {

    private lateinit var btnPlay: Button
    private lateinit var btnHeroes: Button
    private lateinit var btnShop: Button
    private lateinit var btnProfile: Button
    private lateinit var btnRank: Button
    private lateinit var btnSettings: Button
    private lateinit var tvPlayerName: TextView
    private lateinit var tvPlayerLevel: TextView
    private lateinit var tvGold: TextView
    private lateinit var tvDiamonds: TextView
    private lateinit var tvRank: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        initViews()
        loadPlayerData()
        setupClickListeners()
    }

    private fun initViews() {
        btnPlay = findViewById(R.id.btnPlay)
        btnHeroes = findViewById(R.id.btnHeroes)
        btnShop = findViewById(R.id.btnShop)
        btnProfile = findViewById(R.id.btnProfile)
        btnRank = findViewById(R.id.btnRank)
        btnSettings = findViewById(R.id.btnSettings)
        tvPlayerName = findViewById(R.id.tvPlayerName)
        tvPlayerLevel = findViewById(R.id.tvPlayerLevel)
        tvGold = findViewById(R.id.tvGold)
        tvDiamonds = findViewById(R.id.tvDiamonds)
        tvRank = findViewById(R.id.tvRank)
    }

    private fun loadPlayerData() {
        val profile = GameSettings.loadPlayerProfile()
        tvPlayerName.text = profile.name
        tvPlayerLevel.text = "Lv.${profile.level}"
        tvGold.text = profile.gold.toString()
        tvDiamonds.text = profile.diamonds.toString()
        tvRank.text = profile.rank.displayName
    }

    private fun setupClickListeners() {
        btnPlay.setOnClickListener {
            startActivity(Intent(this, HeroSelectActivity::class.java))
        }

        btnHeroes.setOnClickListener {
            startActivity(Intent(this, HeroSelectActivity::class.java).apply {
                putExtra("mode", "browse")
            })
        }

        btnShop.setOnClickListener {
            startActivity(Intent(this, ShopActivity::class.java))
        }

        btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        btnRank.setOnClickListener {
            startActivity(Intent(this, RankActivity::class.java))
        }

        btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadPlayerData()
    }
}
