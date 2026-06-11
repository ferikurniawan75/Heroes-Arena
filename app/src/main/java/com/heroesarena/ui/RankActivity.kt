package com.heroesarena.ui

import android.os.Bundle
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.heroesarena.R
import com.heroesarena.models.RankTier
import com.heroesarena.utils.GameSettings

/**
 * Rank Activity - competitive ranking display
 */
class RankActivity : AppCompatActivity() {

    private lateinit var tvCurrentRank: TextView
    private lateinit var tvRankStars: TextView
    private lateinit var rvLeaderboard: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rank)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        tvCurrentRank = findViewById(R.id.tvCurrentRank)
        tvRankStars = findViewById(R.id.tvRankStars)
        rvLeaderboard = findViewById(R.id.rvLeaderboard)

        findViewById<TextView>(R.id.btnBack)?.setOnClickListener { finish() }

        val profile = GameSettings.loadPlayerProfile()
        tvCurrentRank.text = profile.rank.displayName
        tvRankStars.text = "${profile.rankStars} Stars"

        // Mock leaderboard
        val leaderboard = (0..49).map { i ->
            LeaderboardEntry("Player_${i + 1}", RankTier.entries.reversed().random(), (50 - i))
        }

        rvLeaderboard.layoutManager = LinearLayoutManager(this)
        rvLeaderboard.adapter = LeaderboardAdapter(leaderboard)
    }

    data class LeaderboardEntry(val name: String, val rank: RankTier, val points: Int)

    inner class LeaderboardAdapter(
        private val entries: List<LeaderboardEntry>
    ) : RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>() {

        inner class ViewHolder(view: android.view.View) : RecyclerView.ViewHolder(view) {
            val tvRank: TextView = view.findViewById(R.id.tvLbRank)
            val tvName: TextView = view.findViewById(R.id.tvLbName)
            val tvTier: TextView = view.findViewById(R.id.tvLbTier)
            val tvPoints: TextView = view.findViewById(R.id.tvLbPoints)
        }

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
            val view = android.view.LayoutInflater.from(parent.context)
                .inflate(R.layout.item_leaderboard, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val entry = entries[position]
            holder.tvRank.text = "#${position + 1}"
            holder.tvName.text = entry.name
            holder.tvTier.text = entry.rank.displayName
            holder.tvPoints.text = "${entry.points} pts"
        }

        override fun getItemCount() = entries.size
    }
}
