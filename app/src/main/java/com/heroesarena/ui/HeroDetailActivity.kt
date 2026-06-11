package com.heroesarena.ui

import android.os.Bundle
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.heroesarena.R
import com.heroesarena.models.HeroRegistry

/**
 * Hero Detail Activity - detailed view of a single hero
 */
class HeroDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hero_detail)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val heroId = intent.getStringExtra("hero_id") ?: return
        val hero = HeroRegistry.getHeroById(heroId) ?: return

        findViewById<TextView>(R.id.tvDetailHeroName)?.text = hero.name
        findViewById<TextView>(R.id.tvDetailHeroTitle)?.text = hero.title
        findViewById<TextView>(R.id.tvDetailHeroClass)?.text = hero.heroClass.displayName
        findViewById<TextView>(R.id.tvDetailHeroLore)?.text = hero.lore

        findViewById<TextView>(R.id.btnBack)?.setOnClickListener { finish() }
    }
}
