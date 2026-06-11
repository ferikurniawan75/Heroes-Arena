package com.heroesarena.models

import java.io.Serializable

/**
 * Represents a skill/ability for a hero
 */
data class Skill(
    val id: String,
    val name: String,
    val description: String,
    val type: SkillType,
    val damageType: DamageType,
    val baseDamage: FloatArray,
    val manaCost: FloatArray,
    val cooldown: FloatArray,
    val range: Float = 300f,
    val aoeRadius: Float = 0f,
    val ccType: CCType? = null,
    val ccDuration: Float = 0f,
    val scalingAD: Float = 0f,
    val scalingAP: Float = 0f,
    val iconRes: String = "",
    val effectRes: String = "",
    val maxLevel: Int = 5,
    val unlockLevel: Int = 1
) : Serializable {

    fun getDamageAtLevel(level: Int): Float = baseDamage.getOrElse(level - 1) { baseDamage.last() }
    fun getManaCostAtLevel(level: Int): Float = manaCost.getOrElse(level - 1) { manaCost.last() }
    fun getCooldownAtLevel(level: Int): Float = cooldown.getOrElse(level - 1) { cooldown.last() }

    fun calculateTotalDamage(skillLevel: Int, bonusAD: Float, bonusAP: Float): Float {
        val base = getDamageAtLevel(skillLevel)
        val scaling = scalingAD * bonusAD + scalingAP * bonusAP
        return base + scaling
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Skill
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}

enum class SkillType {
    PASSIVE, SKILL_1, SKILL_2, SKILL_3, ULTIMATE
}

/**
 * Predefined skill templates for hero creation
 */
object SkillTemplates {

    fun dashSkill(id: String, name: String) = Skill(
        id = id, name = name, description = "Dash forward dealing damage",
        type = SkillType.SKILL_1, damageType = DamageType.PHYSICAL,
        baseDamage = floatArrayOf(80f, 120f, 160f, 200f, 240f),
        manaCost = floatArrayOf(40f, 45f, 50f, 55f, 60f),
        cooldown = floatArrayOf(8f, 7.5f, 7f, 6.5f, 6f),
        range = 350f, scalingAD = 0.8f
    )

    fun aoeSkill(id: String, name: String) = Skill(
        id = id, name = name, description = "Deal area damage around the hero",
        type = SkillType.SKILL_2, damageType = DamageType.MAGIC,
        baseDamage = floatArrayOf(100f, 150f, 200f, 250f, 300f),
        manaCost = floatArrayOf(60f, 70f, 80f, 90f, 100f),
        cooldown = floatArrayOf(10f, 9f, 8f, 7f, 6f),
        range = 250f, aoeRadius = 200f, scalingAP = 1.0f
    )

    fun stunSkill(id: String, name: String) = Skill(
        id = id, name = name, description = "Stun enemies in area",
        type = SkillType.SKILL_3, damageType = DamageType.MAGIC,
        baseDamage = floatArrayOf(60f, 100f, 140f, 180f, 220f),
        manaCost = floatArrayOf(50f, 55f, 60f, 65f, 70f),
        cooldown = floatArrayOf(12f, 11f, 10f, 9f, 8f),
        range = 300f, aoeRadius = 150f,
        ccType = CCType.STUN, ccDuration = 1.5f,
        scalingAP = 0.6f
    )

    fun ultimateSkill(id: String, name: String) = Skill(
        id = id, name = name, description = "Unleash ultimate power",
        type = SkillType.ULTIMATE, damageType = DamageType.MAGIC,
        baseDamage = floatArrayOf(300f, 450f, 600f),
        manaCost = floatArrayOf(100f, 120f, 140f),
        cooldown = floatArrayOf(45f, 40f, 35f),
        range = 500f, aoeRadius = 300f,
        scalingAP = 1.5f, maxLevel = 3, unlockLevel = 4
    )

    fun passiveSkill(id: String, name: String, desc: String) = Skill(
        id = id, name = name, description = desc,
        type = SkillType.PASSIVE, damageType = DamageType.PHYSICAL,
        baseDamage = floatArrayOf(0f),
        manaCost = floatArrayOf(0f),
        cooldown = floatArrayOf(0f),
        maxLevel = 1, unlockLevel = 1
    )
}
