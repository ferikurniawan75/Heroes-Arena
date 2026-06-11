package com.heroesarena.ui

import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.heroesarena.R
import com.heroesarena.models.ItemCategory
import com.heroesarena.models.ItemRegistry

/**
 * Shop Activity - buy heroes, skins, and items
 */
class ShopActivity : AppCompatActivity() {

    private lateinit var rvItems: RecyclerView
    private lateinit var tvGold: TextView
    private lateinit var tvDiamonds: TextView
    private var currentCategory: ItemCategory = ItemCategory.ATTACK

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        rvItems = findViewById(R.id.rvShopItems)
        tvGold = findViewById(R.id.tvShopGold)
        tvDiamonds = findViewById(R.id.tvShopDiamonds)

        findViewById<Button>(R.id.btnBack)?.setOnClickListener { finish() }

        // Category buttons
        findViewById<Button>(R.id.btnCatAttack)?.setOnClickListener { setCategory(ItemCategory.ATTACK) }
        findViewById<Button>(R.id.btnCatMagic)?.setOnClickListener { setCategory(ItemCategory.MAGIC) }
        findViewById<Button>(R.id.btnCatDefense)?.setOnClickListener { setCategory(ItemCategory.DEFENSE) }
        findViewById<Button>(R.id.btnCatMovement)?.setOnClickListener { setCategory(ItemCategory.MOVEMENT) }
        findViewById<Button>(R.id.btnCatJungle)?.setOnClickListener { setCategory(ItemCategory.JUNGLE) }
        findViewById<Button>(R.id.btnCatRoam)?.setOnClickListener { setCategory(ItemCategory.ROAM) }

        loadShop()
    }

    private fun setCategory(category: ItemCategory) {
        currentCategory = category
        loadShop()
    }

    private fun loadShop() {
        val items = ItemRegistry.getItemsByCategory(currentCategory)
        rvItems.layoutManager = GridLayoutManager(this, 3)
        rvItems.adapter = ShopItemAdapter(items)
    }

    inner class ShopItemAdapter(
        private val items: List<com.heroesarena.models.Item>
    ) : RecyclerView.Adapter<ShopItemAdapter.ViewHolder>() {

        inner class ViewHolder(view: android.view.View) : RecyclerView.ViewHolder(view) {
            val tvName: TextView = view.findViewById(R.id.tvItemName)
            val tvCost: TextView = view.findViewById(R.id.tvItemCost)
            val tvStat: TextView = view.findViewById(R.id.tvItemStat)
        }

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
            val view = android.view.LayoutInflater.from(parent.context)
                .inflate(R.layout.item_shop_card, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.tvName.text = item.name
            holder.tvCost.text = "${item.cost}g"

            val mainStat = when {
                item.stats.attack > 0 -> "ATK +${item.stats.attack.toInt()}"
                item.stats.magic > 0 -> "MAG +${item.stats.magic.toInt()}"
                item.stats.defense > 0 -> "DEF +${item.stats.defense.toInt()}"
                item.stats.hp > 0 -> "HP +${item.stats.hp.toInt()}"
                item.stats.moveSpeed > 0 -> "SPD +${item.stats.moveSpeed.toInt()}"
                else -> ""
            }
            holder.tvStat.text = mainStat
        }

        override fun getItemCount() = items.size
    }
}
