package com.heroesarena.ui

import android.os.Bundle
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.heroesarena.R
import com.heroesarena.models.PlayerProfile
import com.heroesarena.utils.GameSettings

/**
 * Player Profile Activity - stats, match history, achievements
 */
class ProfileActivity : AppCompatActivity() {

    private lateinit var tvName: TextView
    private lateinit var tvLevel: TextView
    private lateinit var tvRank: TextView
    private lateinit var tvTotalGames: TextView
    private lateinit var tvWinRate: TextView
    private lateinit var tvMvpCount: TextView
    private lateinit var tvKda: TextView
    private lateinit var rvMatchHistory: RecyclerView
    private lateinit var rvAchievements: RecyclerView

    private lateinit var profile: PlayerProfile

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        profile = GameSettings.loadPlayerProfile()
        initViews()
        loadProfileData()
    }

    private fun initViews() {
        tvName = findViewById(R.id.tvProfileName)
        tvLevel = findViewById(R.id.tvProfileLevel)
        tvRank = findViewById(R.id.tvProfileRank)
        tvTotalGames = findViewById(R.id.tvTotalGames)
        tvWinRate = findViewById(R.id.tvWinRate)
        tvMvpCount = findViewById(R.id.tvMvpCount)
        tvKda = findViewById(R.id.tvKda)
        rvMatchHistory = findViewById(R.id.rvMatchHistory)
        rvAchievements = findViewById(R.id.rvAchievements)

        findViewById<TextView>(R.id.btnBack)?.setOnClickListener { finish() }
    }

    private fun loadProfileData() {
        tvName.text = profile.name
        tvLevel.text = "Level ${profile.level}"
        tvRank.text = profile.rank.displayName
        tvTotalGames.text = "Games: ${profile.totalGames}"
        tvWinRate.text = "Win Rate: ${profile.winRatePercent()}%"
        tvMvpCount.text = "MVP: ${profile.mvpCount}"
        tvKda.text = "Savage: ${profile.savageCount} | Legendary: ${profile.legendaryCount}"

        rvMatchHistory.layoutManager = LinearLayoutManager(this)
        rvMatchHistory.adapter = MatchHistoryAdapter(profile.matchHistory)

        rvAchievements.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    inner class MatchHistoryAdapter(
        private val matches: List<com.heroesarena.models.MatchResult>
    ) : RecyclerView.Adapter<MatchHistoryAdapter.ViewHolder>() {

        inner class ViewHolder(view: android.view.View) : RecyclerView.ViewHolder(view) {
            val tvResult: TextView = view.findViewById(R.id.tvMatchResult)
            val tvHero: TextView = view.findViewById(R.id.tvMatchHero)
            val tvKDA: TextView = view.findViewById(R.id.tvMatchKDA)
            val tvMode: TextView = view.findViewById(R.id.tvMatchMode)
        }

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
            val view = android.view.LayoutInflater.from(parent.context)
                .inflate(R.layout.item_match_history, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val match = matches[position]
            holder.tvResult.text = if (match.result) "WIN" else "LOSS"
            holder.tvResult.setTextColor(
                if (match.result) getColor(com.heroesarena.R.color.hp_green)
                else getColor(com.heroesarena.R.color.hp_red)
            )
            holder.tvHero.text = match.heroId
            holder.tvKDA.text = match.kdaString()
            holder.tvMode.text = match.mode.displayName
        }

        override fun getItemCount() = matches.size
    }
}
