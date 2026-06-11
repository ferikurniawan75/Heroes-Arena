package com.heroesarena.ui

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.heroesarena.R
import com.heroesarena.models.*
import com.heroesarena.utils.GameSettings

/**
 * Hero Selection Screen - browse and select heroes before match
 */
class HeroSelectActivity : AppCompatActivity() {

    private lateinit var rvHeroes: RecyclerView
    private lateinit var tvSelectedHeroName: TextView
    private lateinit var tvSelectedHeroTitle: TextView
    private lateinit var tvSelectedHeroClass: TextView
    private lateinit var tvSelectedHeroLore: TextView
    private lateinit var tvHpStat: TextView
    private lateinit var tvAtkStat: TextView
    private lateinit var tvDefStat: TextView
    private lateinit var tvSpdStat: TextView
    private lateinit var pbHp: ProgressBar
    private lateinit var pbAtk: ProgressBar
    private lateinit var pbDef: ProgressBar
    private lateinit var pbSpd: ProgressBar
    private lateinit var btnConfirm: Button
    private lateinit var rgClassFilter: RadioGroup
    private lateinit var tvGameMode: TextView

    private var heroes = HeroRegistry.getAllHeroes()
    private var selectedHero: Hero? = null
    private var gameMode: GameMode = GameMode.CLASSIC
    private var isBrowseMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hero_select)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        isBrowseMode = intent.getStringExtra("mode") == "browse"

        initViews()
        setupClassFilter()
        updateHeroList()
    }

    private fun initViews() {
        rvHeroes = findViewById(R.id.rvHeroes)
        tvSelectedHeroName = findViewById(R.id.tvSelectedHeroName)
        tvSelectedHeroTitle = findViewById(R.id.tvSelectedHeroTitle)
        tvSelectedHeroClass = findViewById(R.id.tvSelectedHeroClass)
        tvSelectedHeroLore = findViewById(R.id.tvSelectedHeroLore)
        tvHpStat = findViewById(R.id.tvHpStat)
        tvAtkStat = findViewById(R.id.tvAtkStat)
        tvDefStat = findViewById(R.id.tvDefStat)
        tvSpdStat = findViewById(R.id.tvSpdStat)
        pbHp = findViewById(R.id.pbHp)
        pbAtk = findViewById(R.id.pbAtk)
        pbDef = findViewById(R.id.pbDef)
        pbSpd = findViewById(R.id.pbSpd)
        btnConfirm = findViewById(R.id.btnConfirm)
        rgClassFilter = findViewById(R.id.rgClassFilter)
        tvGameMode = findViewById(R.id.tvGameMode)

        tvGameMode.text = gameMode.displayName

        btnConfirm.setOnClickListener {
            if (selectedHero != null) {
                startActivity(Intent(this, GameActivity::class.java).apply {
                    putExtra("hero_id", selectedHero!!.id)
                    putExtra("game_mode", gameMode.name)
                })
            } else {
                Toast.makeText(this, "Select a hero first!", Toast.LENGTH_SHORT).show()
            }
        }

        rvHeroes.layoutManager = GridLayoutManager(this, 4)
    }

    private fun setupClassFilter() {
        rgClassFilter.setOnCheckedChangeListener { _, checkedId ->
            heroes = when (checkedId) {
                R.id.rbAll -> HeroRegistry.getAllHeroes()
                R.id.rbTank -> HeroRegistry.getHeroesByClass(HeroClass.TANK)
                R.id.rbFighter -> HeroRegistry.getHeroesByClass(HeroClass.FIGHTER)
                R.id.rbAssassin -> HeroRegistry.getHeroesByClass(HeroClass.ASSASSIN)
                R.id.rbMage -> HeroRegistry.getHeroesByClass(HeroClass.MAGE)
                R.id.rbMarksman -> HeroRegistry.getHeroesByClass(HeroClass.MARKSMAN)
                R.id.rbSupport -> HeroRegistry.getHeroesByClass(HeroClass.SUPPORT)
                else -> HeroRegistry.getAllHeroes()
            }
            updateHeroList()
        }
    }

    private fun updateHeroList() {
        val adapter = HeroAdapter(heroes) { hero ->
            selectedHero = hero
            updateHeroDetail(hero)
        }
        rvHeroes.adapter = adapter
    }

    private fun updateHeroDetail(hero: Hero) {
        tvSelectedHeroName.text = hero.name
        tvSelectedHeroTitle.text = hero.title
        tvSelectedHeroClass.text = hero.heroClass.displayName
        tvSelectedHeroLore.text = hero.lore

        val stats = hero.stats
        pbHp.progress = ((stats.hpBase / 5000f) * 100).toInt()
        pbAtk.progress = ((stats.attackBase / 200f) * 100).toInt()
        pbDef.progress = ((stats.defenseBase / 100f) * 100).toInt()
        pbSpd.progress = ((stats.moveSpeed / 300f) * 100).toInt()

        tvHpStat.text = "HP: ${stats.hpBase.toInt()}"
        tvAtkStat.text = "ATK: ${stats.attackBase.toInt()}"
        tvDefStat.text = "DEF: ${stats.defenseBase.toInt()}"
        tvSpdStat.text = "SPD: ${stats.moveSpeed.toInt()}"
    }

    /**
     * RecyclerView adapter for hero grid
     */
    inner class HeroAdapter(
        private val heroes: List<Hero>,
        private val onClick: (Hero) -> Unit
    ) : RecyclerView.Adapter<HeroAdapter.HeroViewHolder>() {

        inner class HeroViewHolder(view: android.view.View) : RecyclerView.ViewHolder(view) {
            val tvName: TextView = view.findViewById(R.id.tvHeroName)
            val tvClass: TextView = view.findViewById(R.id.tvHeroClass)
            val viewBg: View = view.findViewById(R.id.heroCardBg)
        }

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): HeroViewHolder {
            val view = android.view.LayoutInflater.from(parent.context)
                .inflate(R.layout.item_hero_card, parent, false)
            return HeroViewHolder(view)
        }

        override fun onBindViewHolder(holder: HeroViewHolder, position: Int) {
            val hero = heroes[position]
            holder.tvName.text = hero.name
            holder.tvClass.text = hero.heroClass.displayName

            val color = hero.heroClass.color.toInt()
            holder.viewBg.setBackgroundColor(color)

            holder.itemView.setOnClickListener { onClick(hero) }

            if (hero == selectedHero) {
                holder.itemView.setBackgroundResource(R.drawable.hero_selected_border)
            }
        }

        override fun getItemCount() = heroes.size
    }
}
