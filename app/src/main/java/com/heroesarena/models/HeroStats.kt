package com.heroesarena.models

import java.io.Serializable

/**
 * Represents a hero's stat profile with base stats and per-level growth
 */
data class HeroStats(
    val hpBase: Float = 2500f,
    val hpPerLevel: Float = 200f,
    val mpBase: Float = 1000f,
    val mpPerLevel: Float = 50f,
    val hpRegenBase: Float = 8f,
    val hpRegenPerLevel: Float = 0.5f,
    val mpRegenBase: Float = 5f,
    val mpRegenPerLevel: Float = 0.3f,
    val attackBase: Float = 120f,
    val attackPerLevel: Float = 8f,
    val magicBase: Float = 0f,
    val magicPerLevel: Float = 5f,
    val defenseBase: Float = 50f,
    val defensePerLevel: Float = 4f,
    val magicDefenseBase: Float = 30f,
    val magicDefensePerLevel: Float = 2f,
    val attackSpeedBase: Float = 1.0f,
    val attackSpeedPerLevel: Float = 0.02f,
    val moveSpeed: Float = 250f,
    val attackRange: Float = 150f,
    val critRate: Float = 0f,
    val critDamage: Float = 2.0f,
    val cooldownReduction: Float = 0f,
    val lifesteal: Float = 0f,
    val spellVamp: Float = 0f
) : Serializable {

    fun getHpAtLevel(level: Int): Float = hpBase + hpPerLevel * (level - 1)
    fun getMpAtLevel(level: Int): Float = mpBase + mpPerLevel * (level - 1)
    fun getAttackAtLevel(level: Int): Float = attackBase + attackPerLevel * (level - 1)
    fun getMagicAtLevel(level: Int): Float = magicBase + magicPerLevel * (level - 1)
    fun getDefenseAtLevel(level: Int): Float = defenseBase + defensePerLevel * (level - 1)
    fun getMagicDefenseAtLevel(level: Int): Float = magicDefenseBase + magicDefensePerLevel * (level - 1)
    fun getAttackSpeedAtLevel(level: Int): Float = attackSpeedBase + attackSpeedPerLevel * (level - 1)

    /**
     * Calculate effective damage after defense reduction
     */
    fun calculateDamageReduction(defense: Float): Float {
        return 100f / (100f + defense)
    }

    companion object {
        fun tankDefaults() = HeroStats(
            hpBase = 3500f, hpPerLevel = 280f,
            defenseBase = 80f, defensePerLevel = 6f,
            attackBase = 100f, attackPerLevel = 6f,
            moveSpeed = 240f, attackRange = 130f
        )

        fun fighterDefaults() = HeroStats(
            hpBase = 2800f, hpPerLevel = 220f,
            defenseBase = 60f, defensePerLevel = 5f,
            attackBase = 130f, attackPerLevel = 9f,
            moveSpeed = 260f, attackRange = 140f
        )

        fun assassinDefaults() = HeroStats(
            hpBase = 2200f, hpPerLevel = 180f,
            defenseBase = 40f, defensePerLevel = 3f,
            attackBase = 150f, attackPerLevel = 12f,
            moveSpeed = 270f, attackRange = 130f,
            critRate = 0.1f, critDamage = 2.5f
        )

        fun mageDefaults() = HeroStats(
            hpBase = 2000f, hpPerLevel = 160f,
            mpBase = 1500f, mpPerLevel = 80f,
            defenseBase = 30f, defensePerLevel = 2f,
            attackBase = 90f, attackPerLevel = 5f,
            magicBase = 120f, magicPerLevel = 10f,
            moveSpeed = 250f, attackRange = 400f
        )

        fun marksmanDefaults() = HeroStats(
            hpBase = 2000f, hpPerLevel = 150f,
            defenseBase = 25f, defensePerLevel = 2f,
            attackBase = 160f, attackPerLevel = 14f,
            moveSpeed = 250f, attackRange = 500f,
            critRate = 0.15f, critDamage = 2.0f
        )

        fun supportDefaults() = HeroStats(
            hpBase = 2500f, hpPerLevel = 200f,
            mpBase = 1200f, mpPerLevel = 60f,
            defenseBase = 50f, defensePerLevel = 4f,
            attackBase = 80f, attackPerLevel = 4f,
            magicBase = 80f, magicPerLevel = 8f,
            moveSpeed = 250f, attackRange = 350f,
            cooldownReduction = 0.1f
        )
    }
}
