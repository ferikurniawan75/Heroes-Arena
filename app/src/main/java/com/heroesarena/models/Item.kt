package com.heroesarena.models

import java.io.Serializable

/**
 * Item/Equipment that can be purchased during a match
 */
data class Item(
    val id: String,
    val name: String,
    val description: String,
    val category: ItemCategory,
    val subCategory: ItemSubCategory,
    val cost: Int,
    val stats: ItemStats,
    val passive: ItemPassive? = null,
    val buildFrom: List<String> = emptyList(),
    val buildsInto: List<String> = emptyList(),
    val iconRes: String = "",
    val rarity: Rarity = Rarity.COMMON
) : Serializable

data class ItemStats(
    val hp: Float = 0f,
    val mp: Float = 0f,
    val attack: Float = 0f,
    val magic: Float = 0f,
    val defense: Float = 0f,
    val magicDefense: Float = 0f,
    val attackSpeed: Float = 0f,
    val critRate: Float = 0f,
    val critDamage: Float = 0f,
    val moveSpeed: Float = 0f,
    val cooldownReduction: Float = 0f,
    val lifesteal: Float = 0f,
    val spellVamp: Float = 0f,
    val hpRegen: Float = 0f,
    val mpRegen: Float = 0f
) : Serializable {
    operator fun plus(other: ItemStats): ItemStats = ItemStats(
        hp = hp + other.hp, mp = mp + other.mp,
        attack = attack + other.attack, magic = magic + other.magic,
        defense = defense + other.defense, magicDefense = magicDefense + other.magicDefense,
        attackSpeed = attackSpeed + other.attackSpeed, critRate = critRate + other.critRate,
        critDamage = critDamage + other.critDamage, moveSpeed = moveSpeed + other.moveSpeed,
        cooldownReduction = cooldownReduction + other.cooldownReduction,
        lifesteal = lifesteal + other.lifesteal, spellVamp = spellVamp + other.spellVamp,
        hpRegen = hpRegen + other.hpRegen, mpRegen = mpRegen + other.mpRegen
    )
}

data class ItemPassive(
    val name: String,
    val description: String,
    val type: PassiveType,
    val value: Float = 0f
) : Serializable

enum class ItemCategory {
    ATTACK, MAGIC, DEFENSE, MOVEMENT, JUNGLE, ROAM
}

enum class ItemSubCategory {
    WEAPON, ATTACK_SPEED, CRIT, LIFESTEAL, // Attack
    MAGIC_POWER, COOLDOWN, MANA, SPELL_VAMP, // Magic
    HP, ARMOR, MAGIC_RESIST, SHIELD, // Defense
    BOOTS, MOVEMENT, // Movement
    JUNGLE_ITEM, RETRIBUTION, // Jungle
    ROAM_ITEM, SUPPORT // Roam
}

enum class PassiveType {
    ON_HIT, ON_SPELL, ON_KILL, ON_DAMAGED, ON_HEAL,
    AURA, UNIQUE, ACTIVE, STACKING
}

/**
 * Complete item registry
 */
object ItemRegistry {
    private val items = mutableListOf<Item>()

    fun init() {
        items.clear()
        items.addAll(createAllItems())
    }

    fun getAllItems(): List<Item> = items.toList()
    fun getItemById(id: String): Item? = items.find { it.id == id }
    fun getItemsByCategory(cat: ItemCategory): List<Item> = items.filter { it.category == cat }

    private fun createAllItems(): List<Item> = listOf(
        // === ATTACK ITEMS ===
        Item("atk_sword", "Warblade", "A finely crafted sword.", ItemCategory.ATTACK,
            ItemSubCategory.WEAPON, 500, ItemStats(attack = 20f), rarity = Rarity.COMMON),
        Item("atk_giant_sword", "Giant Slayer", "Sword designed to slay titans.", ItemCategory.ATTACK,
            ItemSubCategory.WEAPON, 1200, ItemStats(attack = 60f), buildFrom = listOf("atk_sword"), rarity = Rarity.UNCOMMON),
        Item("atk_berserker", "Berserker's Fury", "Unleash your rage.", ItemCategory.ATTACK,
            ItemSubCategory.WEAPON, 2400, ItemStats(attack = 80f, critRate = 0.15f, critDamage = 0.4f),
            buildFrom = listOf("atk_giant_sword"), rarity = Rarity.EPIC),
        Item("atk_hunter", "Hunter Strike", "Strike with precision.", ItemCategory.ATTACK,
            ItemSubCategory.WEAPON, 2100, ItemStats(attack = 70f, cooldownReduction = 0.1f),
            passive = ItemPassive("Pursuit", "After skill hit, next basic attack deals 100 bonus damage", PassiveType.ON_SPELL, 100f),
            rarity = Rarity.EPIC),
        Item("atk_speed_blade", "Swift Blade", "Attack with incredible speed.", ItemCategory.ATTACK,
            ItemSubCategory.ATTACK_SPEED, 800, ItemStats(attackSpeed = 0.3f), rarity = Rarity.COMMON),
        Item("atk_demon_hunter", "Demon Hunter Sword", "Drain life with each strike.", ItemCategory.ATTACK,
            ItemSubCategory.LIFESTEAL, 2200, ItemStats(attack = 60f, attackSpeed = 0.3f, lifesteal = 0.15f),
            passive = ItemPassive("Soul Drain", "Basic attacks steal 15% of damage as HP", PassiveType.ON_HIT),
            rarity = Rarity.EPIC),
        Item("atk_golden", "Golden Staff", "Consistent critical strikes.", ItemCategory.ATTACK,
            ItemSubCategory.CRIT, 2000, ItemStats(attack = 50f, attackSpeed = 0.3f),
            passive = ItemPassive("Golden Touch", "Crit damage reduced but every attack crits", PassiveType.UNIQUE),
            rarity = Rarity.RARE),

        // === MAGIC ITEMS ===
        Item("mag_staff", "Arcane Staff", "Channel magical energy.", ItemCategory.MAGIC,
            ItemSubCategory.MAGIC_POWER, 500, ItemStats(magic = 30f), rarity = Rarity.COMMON),
        Item("mag_wisdom", "Wisdom Staff", "Amplify magical power.", ItemCategory.MAGIC,
            ItemSubCategory.MAGIC_POWER, 1500, ItemStats(magic = 80f, mp = 300f), buildFrom = listOf("mag_staff"), rarity = Rarity.UNCOMMON),
        Item("mag_blood", "Bloodwing", "Drain life through magic.", ItemCategory.MAGIC,
            ItemSubCategory.SPELL_VAMP, 2200, ItemStats(magic = 90f, spellVamp = 0.2f),
            passive = ItemPassive("Blood Magic", "Spell damage heals for 20% of damage dealt", PassiveType.ON_SPELL),
            rarity = Rarity.EPIC),
        Item("mag_clock", "Clock of Destiny", "Master time itself.", ItemCategory.MAGIC,
            ItemSubCategory.COOLDOWN, 2000, ItemStats(magic = 70f, cooldownReduction = 0.2f, mp = 500f),
            passive = ItemPassive("Destiny", "Gain stacking magic power over time", PassiveType.STACKING),
            rarity = Rarity.EPIC),
        Item("mag_divine", "Divine Glaive", "Pierce magical defenses.", ItemCategory.MAGIC,
            ItemSubCategory.MAGIC_POWER, 2400, ItemStats(magic = 100f),
            passive = ItemPassive("Arcane Pierce", "Ignore 40% of target magic defense", PassiveType.UNIQUE, 0.4f),
            rarity = Rarity.LEGENDARY),

        // === DEFENSE ITEMS ===
        Item("def_armor", "Iron Armor", "Basic protection.", ItemCategory.DEFENSE,
            ItemSubCategory.ARMOR, 500, ItemStats(defense = 30f), rarity = Rarity.COMMON),
        Item("def_dominance", "Dominance Ice", "Chilling presence.", ItemCategory.DEFENSE,
            ItemSubCategory.ARMOR, 2200, ItemStats(defense = 70f, mp = 400f, attackSpeed = 0.1f),
            passive = ItemPassive("Frost", "Nearby enemies have reduced attack speed", PassiveType.AURA, -0.3f),
            rarity = Rarity.EPIC),
        Item("def_antique", "Antique Cuirass", "Reduce enemy physical attack.", ItemCategory.DEFENSE,
            ItemSubCategory.ARMOR, 2000, ItemStats(defense = 60f, hp = 500f),
            passive = ItemPassive("Deter", "Nearby enemies have reduced physical attack by 20%", PassiveType.AURA, -0.2f),
            rarity = Rarity.EPIC),
        Item("def_immortality", "Immortality", "Rise from death once.", ItemCategory.DEFENSE,
            ItemSubCategory.SHIELD, 2800, ItemStats(defense = 40f, magicDefense = 40f, hp = 800f),
            passive = ItemPassive("Resurrection", "Revive with 30% HP after dying once (120s CD)", PassiveType.ON_DAMAGED),
            rarity = Rarity.LEGENDARY),
        Item("def_athena", "Athena's Shield", "Magical protection.", ItemCategory.DEFENSE,
            ItemSubCategory.MAGIC_RESIST, 2000, ItemStats(magicDefense = 60f, hp = 500f),
            passive = ItemPassive("Shield", "Gain a magic shield every 30s", PassiveType.ON_DAMAGED),
            rarity = Rarity.EPIC),

        // === MOVEMENT (BOOTS) ===
        Item("mov_boots", "Basic Boots", "Move faster.", ItemCategory.MOVEMENT,
            ItemSubCategory.BOOTS, 300, ItemStats(moveSpeed = 40f), rarity = Rarity.COMMON),
        Item("mov_swift", "Swift Boots", "Attack speed boots.", ItemCategory.MOVEMENT,
            ItemSubCategory.BOOTS, 700, ItemStats(moveSpeed = 50f, attackSpeed = 0.15f),
            buildFrom = listOf("mov_boots"), rarity = Rarity.COMMON),
        Item("mov_wizard", "Wizard Boots", "Magic penetration boots.", ItemCategory.MOVEMENT,
            ItemSubCategory.BOOTS, 700, ItemStats(moveSpeed = 50f, magic = 30f),
            buildFrom = listOf("mov_boots"), rarity = Rarity.COMMON),
        Item("mov_tough", "Tough Boots", "Defense boots with CC reduction.", ItemCategory.MOVEMENT,
            ItemSubCategory.BOOTS, 700, ItemStats(moveSpeed = 50f, defense = 20f, magicDefense = 20f),
            buildFrom = listOf("mov_boots"), rarity = Rarity.COMMON),

        // === JUNGLE ITEMS ===
        Item("jng_hunter", "Hunter's Knife", "Jungle starter item.", ItemCategory.JUNGLE,
            ItemSubCategory.JUNGLE_ITEM, 500, ItemStats(attack = 20f),
            passive = ItemPassive("Hunter", "Deal 50% bonus damage to jungle monsters", PassiveType.UNIQUE),
            rarity = Rarity.COMMON),

        // === ROAM ITEMS ===
        Item("roam_mask", "Roaming Mask", "Support starter.", ItemCategory.ROAM,
            ItemSubCategory.ROAM_ITEM, 400, ItemStats(hp = 300f, mpRegen = 5f),
            passive = ItemPassive("Roam", "Gain gold/exp when near ally kills", PassiveType.UNIQUE),
            rarity = Rarity.COMMON)
    )
}


