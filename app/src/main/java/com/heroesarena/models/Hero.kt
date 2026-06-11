package com.heroesarena.models

import java.io.Serializable

/**
 * Complete Hero definition with all attributes, skills, and lore
 */
data class Hero(
    val id: String,
    val name: String,
    val title: String,
    val heroClass: HeroClass,
    val rarity: Rarity,
    val stats: HeroStats,
    val skills: List<Skill>,
    val lore: String,
    val price: Int = 0,
    val isFree: Boolean = false,
    val skinIds: List<String> = emptyList(),
    val iconRes: String = "",
    val portraitRes: String = "",
    val modelRes: String = "",
    val passiveDesc: String = ""
) : Serializable {

    fun getSkillByType(type: SkillType): Skill? = skills.find { it.type == type }
    fun getPassive(): Skill? = getSkillByType(SkillType.PASSIVE)
    fun getSkill1(): Skill? = getSkillByType(SkillType.SKILL_1)
    fun getSkill2(): Skill? = getSkillByType(SkillType.SKILL_2)
    fun getSkill3(): Skill? = getSkillByType(SkillType.SKILL_3)
    fun getUltimate(): Skill? = getSkillByType(SkillType.ULTIMATE)
}

/**
 * Hero registry - all heroes in the game
 */
object HeroRegistry {
    private val heroes = mutableListOf<Hero>()

    fun init() {
        heroes.clear()
        heroes.addAll(createAllHeroes())
    }

    fun getAllHeroes(): List<Hero> = heroes.toList()
    fun getHeroById(id: String): Hero? = heroes.find { it.id == id }
    fun getHeroesByClass(heroClass: HeroClass): List<Hero> = heroes.filter { it.heroClass == heroClass }

    private fun createAllHeroes(): List<Hero> = listOf(
        // === TANKS ===
        Hero(
            id = "titan_guard", name = "Titan Guard", title = "The Unbreakable Wall",
            heroClass = HeroClass.TANK, rarity = Rarity.EPIC,
            stats = HeroStats.tankDefaults().copy(
                hpBase = 4000f, hpPerLevel = 320f,
                defenseBase = 90f, defensePerLevel = 7f,
                moveSpeed = 235f
            ),
            skills = listOf(
                SkillTemplates.passiveSkill("titan_p", "Iron Will", "Gain shield equal to 10% max HP when below 30% HP"),
                Skill(
                    id = "titan_s1", name = "Shield Bash", description = "Slam shield dealing damage and stunning enemies",
                    type = SkillType.SKILL_1, damageType = DamageType.PHYSICAL,
                    baseDamage = floatArrayOf(100f, 140f, 180f, 220f, 260f),
                    manaCost = floatArrayOf(50f, 55f, 60f, 65f, 70f),
                    cooldown = floatArrayOf(10f, 9f, 8f, 7f, 6f),
                    range = 200f, ccType = CCType.STUN, ccDuration = 1.0f,
                    scalingAD = 0.7f
                ),
                Skill(
                    id = "titan_s2", name = "Fortress", description = "Create protective zone reducing ally damage taken",
                    type = SkillType.SKILL_2, damageType = DamageType.MAGIC,
                    baseDamage = floatArrayOf(50f, 80f, 110f, 140f, 170f),
                    manaCost = floatArrayOf(70f, 80f, 90f, 100f, 110f),
                    cooldown = floatArrayOf(14f, 13f, 12f, 11f, 10f),
                    range = 300f, aoeRadius = 250f, scalingAP = 0.4f
                ),
                Skill(
                    id = "titan_s3", name = "War Cry", description = "Taunt nearby enemies and gain damage reduction",
                    type = SkillType.SKILL_3, damageType = DamageType.PHYSICAL,
                    baseDamage = floatArrayOf(80f, 120f, 160f, 200f, 240f),
                    manaCost = floatArrayOf(60f, 65f, 70f, 75f, 80f),
                    cooldown = floatArrayOf(12f, 11f, 10f, 9f, 8f),
                    range = 350f, ccType = CCType.TAUNT, ccDuration = 1.5f, scalingAD = 0.5f
                ),
                Skill(
                    id = "titan_ult", name = "Unbreakable", description = "Become invincible for 3 seconds, then explode dealing massive damage",
                    type = SkillType.ULTIMATE, damageType = DamageType.PHYSICAL,
                    baseDamage = floatArrayOf(400f, 600f, 800f),
                    manaCost = floatArrayOf(120f, 140f, 160f),
                    cooldown = floatArrayOf(50f, 45f, 40f),
                    range = 400f, aoeRadius = 350f, scalingAD = 1.2f,
                    maxLevel = 3, unlockLevel = 4
                )
            ),
            lore = "Once a humble blacksmith, Titan Guard was forged in the fires of the Great War. His unyielding will and impenetrable shield have protected countless allies on the battlefield.",
            price = 32000
        ),

        Hero(
            id = "ocean_sovereign", name = "Ocean Sovereign", title = "Tide Caller",
            heroClass = HeroClass.TANK, rarity = Rarity.LEGENDARY,
            stats = HeroStats.tankDefaults().copy(
                hpBase = 3800f, mpBase = 1200f,
                magicBase = 60f, magicPerLevel = 8f,
                moveSpeed = 245f
            ),
            skills = listOf(
                SkillTemplates.passiveSkill("ocean_p", "Tidal Protection", "Nearby allies gain 15% damage reduction when below 50% HP"),
                Skill(
                    id = "ocean_s1", name = "Tidal Wave", description = "Send a wave knocking up enemies",
                    type = SkillType.SKILL_1, damageType = DamageType.MAGIC,
                    baseDamage = floatArrayOf(120f, 160f, 200f, 240f, 280f),
                    manaCost = floatArrayOf(55f, 60f, 65f, 70f, 75f),
                    cooldown = floatArrayOf(9f, 8.5f, 8f, 7.5f, 7f),
                    range = 400f, ccType = CCType.KNOCKUP, ccDuration = 1.0f, scalingAP = 0.8f
                ),
                Skill(
                    id = "ocean_s2", name = "Whirlpool", description = "Create a whirlpool pulling enemies to center",
                    type = SkillType.SKILL_2, damageType = DamageType.MAGIC,
                    baseDamage = floatArrayOf(80f, 120f, 160f, 200f, 240f),
                    manaCost = floatArrayOf(70f, 80f, 90f, 100f, 110f),
                    cooldown = floatArrayOf(12f, 11f, 10f, 9f, 8f),
                    range = 350f, aoeRadius = 200f, ccType = CCType.ROOT, ccDuration = 1.0f, scalingAP = 0.7f
                ),
                Skill(
                    id = "ocean_s3", name = "Aqua Shield", description = "Grant shield to self and nearby ally",
                    type = SkillType.SKILL_3, damageType = DamageType.MAGIC,
                    baseDamage = floatArrayOf(0f, 0f, 0f, 0f, 0f),
                    manaCost = floatArrayOf(80f, 85f, 90f, 95f, 100f),
                    cooldown = floatArrayOf(14f, 13f, 12f, 11f, 10f),
                    range = 300f, scalingAP = 0.5f
                ),
                Skill(
                    id = "ocean_ult", name = "Tsunami", description = "Summon a massive tsunami across the lane",
                    type = SkillType.ULTIMATE, damageType = DamageType.MAGIC,
                    baseDamage = floatArrayOf(500f, 700f, 900f),
                    manaCost = floatArrayOf(150f, 170f, 190f),
                    cooldown = floatArrayOf(55f, 50f, 45f),
                    range = 800f, aoeRadius = 400f, ccType = CCType.KNOCKUP, ccDuration = 1.5f,
                    scalingAP = 1.8f, maxLevel = 3, unlockLevel = 4
                )
            ),
            lore = "Born from the depths of the Abyssal Sea, Ocean Sovereign commands the tides themselves. His wrath brings tsunamis, his mercy brings life-giving rain.",
            price = 48000
        ),

        // === FIGHTERS ===
        Hero(
            id = "blade_dancer", name = "Blade Dancer", title = "Storm of Steel",
            heroClass = HeroClass.FIGHTER, rarity = Rarity.EPIC,
            stats = HeroStats.fighterDefaults().copy(
                hpBase = 3000f, attackBase = 140f,
                attackPerLevel = 10f, moveSpeed = 265f
            ),
            skills = listOf(
                SkillTemplates.passiveSkill("blade_p", "Dance of Blades", "Every 3rd attack deals bonus physical damage equal to 8% target max HP"),
                Skill(
                    id = "blade_s1", name = "Crescent Slash", description = "Slash in an arc dealing damage to all enemies hit",
                    type = SkillType.SKILL_1, damageType = DamageType.PHYSICAL,
                    baseDamage = floatArrayOf(90f, 130f, 170f, 210f, 250f),
                    manaCost = floatArrayOf(40f, 45f, 50f, 55f, 60f),
                    cooldown = floatArrayOf(6f, 5.5f, 5f, 4.5f, 4f),
                    range = 250f, aoeRadius = 180f, scalingAD = 1.0f
                ),
                Skill(
                    id = "blade_s2", name = "Phantom Step", description = "Dash through enemies marking them for bonus damage",
                    type = SkillType.SKILL_2, damageType = DamageType.PHYSICAL,
                    baseDamage = floatArrayOf(60f, 90f, 120f, 150f, 180f),
                    manaCost = floatArrayOf(45f, 50f, 55f, 60f, 65f),
                    cooldown = floatArrayOf(8f, 7.5f, 7f, 6.5f, 6f),
                    range = 400f, scalingAD = 0.6f
                ),
                Skill(
                    id = "blade_s3", name = "Steel Storm", description = "Spin dealing damage to nearby enemies repeatedly",
                    type = SkillType.SKILL_3, damageType = DamageType.PHYSICAL,
                    baseDamage = floatArrayOf(70f, 100f, 130f, 160f, 190f),
                    manaCost = floatArrayOf(60f, 65f, 70f, 75f, 80f),
                    cooldown = floatArrayOf(10f, 9f, 8f, 7f, 6f),
                    range = 200f, aoeRadius = 200f, scalingAD = 0.8f
                ),
                Skill(
                    id = "blade_ult", name = "Thousand Cuts", description = "Unleash a flurry of slashes dealing massive damage",
                    type = SkillType.ULTIMATE, damageType = DamageType.PHYSICAL,
                    baseDamage = floatArrayOf(350f, 500f, 650f),
                    manaCost = floatArrayOf(100f, 120f, 140f),
                    cooldown = floatArrayOf(40f, 35f, 30f),
                    range = 300f, scalingAD = 2.0f,
                    maxLevel = 3, unlockLevel = 4
                )
            ),
            lore = "A wandering swordsman seeking the perfect blade technique. Each swing tells a story, each step is a dance of death.",
            price = 32000
        ),

        // === ASSASSINS ===
        Hero(
            id = "shadow_reaper", name = "Shadow Reaper", title = "Death Incarnate",
            heroClass = HeroClass.ASSASSIN, rarity = Rarity.LEGENDARY,
            stats = HeroStats.assassinDefaults().copy(
                hpBase = 2300f, attackBase = 160f,
                attackPerLevel = 14f, critRate = 0.15f,
                moveSpeed = 275f
            ),
            skills = listOf(
                SkillTemplates.passiveSkill("shadow_p", "Death Mark", "Attacks from behind deal 20% bonus damage"),
                Skill(
                    id = "shadow_s1", name = "Shadow Step", description = "Teleport behind target dealing damage",
                    type = SkillType.SKILL_1, damageType = DamageType.PHYSICAL,
                    baseDamage = floatArrayOf(100f, 140f, 180f, 220f, 260f),
                    manaCost = floatArrayOf(50f, 55f, 60f, 65f, 70f),
                    cooldown = floatArrayOf(7f, 6.5f, 6f, 5.5f, 5f),
                    range = 500f, scalingAD = 1.2f
                ),
                Skill(
                    id = "shadow_s2", name = "Phantom Slash", description = "Slash through enemies in a line",
                    type = SkillType.SKILL_2, damageType = DamageType.PHYSICAL,
                    baseDamage = floatArrayOf(80f, 120f, 160f, 200f, 240f),
                    manaCost = floatArrayOf(45f, 50f, 55f, 60f, 65f),
                    cooldown = floatArrayOf(5f, 4.5f, 4f, 3.5f, 3f),
                    range = 350f, scalingAD = 0.9f
                ),
                Skill(
                    id = "shadow_s3", name = "Vanish", description = "Become invisible and gain move speed",
                    type = SkillType.SKILL_3, damageType = DamageType.PHYSICAL,
                    baseDamage = floatArrayOf(0f, 0f, 0f, 0f, 0f),
                    manaCost = floatArrayOf(60f, 65f, 70f, 75f, 80f),
                    cooldown = floatArrayOf(15f, 14f, 13f, 12f, 11f)
                ),
                Skill(
                    id = "shadow_ult", name = "Reaper's Embrace", description = "Execute target below 30% HP, dealing massive true damage",
                    type = SkillType.ULTIMATE, damageType = DamageType.TRUE,
                    baseDamage = floatArrayOf(400f, 600f, 800f),
                    manaCost = floatArrayOf(80f, 100f, 120f),
                    cooldown = floatArrayOf(35f, 30f, 25f),
                    range = 400f, scalingAD = 1.5f,
                    maxLevel = 3, unlockLevel = 4
                )
            ),
            lore = "From the realm between life and death, Shadow Reaper emerges. Those who hear the whisper of his scythe are already marked for oblivion.",
            price = 48000
        ),

        // === MAGES ===
        Hero(
            id = "arcane_witch", name = "Arcane Witch", title = "Mistress of the Arcane",
            heroClass = HeroClass.MAGE, rarity = Rarity.EPIC,
            stats = HeroStats.mageDefaults().copy(
                mpBase = 1800f, magicBase = 140f,
                magicPerLevel = 12f, attackRange = 450f
            ),
            skills = listOf(
                SkillTemplates.passiveSkill("arcane_p", "Arcane Mastery", "Each spell cast increases next spell damage by 8%, stacks 3 times"),
                Skill(
                    id = "arcane_s1", name = "Arcane Bolt", description = "Fire a bolt of pure arcane energy",
                    type = SkillType.SKILL_1, damageType = DamageType.MAGIC,
                    baseDamage = floatArrayOf(120f, 170f, 220f, 270f, 320f),
                    manaCost = floatArrayOf(50f, 55f, 60f, 65f, 70f),
                    cooldown = floatArrayOf(5f, 4.5f, 4f, 3.5f, 3f),
                    range = 500f, scalingAP = 0.9f
                ),
                Skill(
                    id = "arcane_s2", name = "Gravity Well", description = "Create a gravity well slowing and damaging enemies",
                    type = SkillType.SKILL_2, damageType = DamageType.MAGIC,
                    baseDamage = floatArrayOf(100f, 140f, 180f, 220f, 260f),
                    manaCost = floatArrayOf(70f, 80f, 90f, 100f, 110f),
                    cooldown = floatArrayOf(10f, 9f, 8f, 7f, 6f),
                    range = 400f, aoeRadius = 250f, ccType = CCType.SLOW, ccDuration = 2.0f, scalingAP = 0.8f
                ),
                Skill(
                    id = "arcane_s3", name = "Arcane Barrier", description = "Create magical shield and reflect damage",
                    type = SkillType.SKILL_3, damageType = DamageType.MAGIC,
                    baseDamage = floatArrayOf(60f, 90f, 120f, 150f, 180f),
                    manaCost = floatArrayOf(60f, 65f, 70f, 75f, 80f),
                    cooldown = floatArrayOf(12f, 11f, 10f, 9f, 8f),
                    scalingAP = 0.4f
                ),
                Skill(
                    id = "arcane_ult", name = "Arcane Annihilation", description = "Channel devastating arcane energy in a direction",
                    type = SkillType.ULTIMATE, damageType = DamageType.MAGIC,
                    baseDamage = floatArrayOf(600f, 850f, 1100f),
                    manaCost = floatArrayOf(150f, 180f, 210f),
                    cooldown = floatArrayOf(45f, 40f, 35f),
                    range = 700f, aoeRadius = 200f, scalingAP = 2.0f,
                    maxLevel = 3, unlockLevel = 4
                )
            ),
            lore = "The last keeper of the Ancient Library, Arcane Witch wields knowledge so powerful it bends reality itself. Her spells are not magic—they are rewritten laws of nature.",
            price = 32000
        ),

        // === MARKSMEN ===
        Hero(
            id = "storm_archer", name = "Storm Archer", title = "Eye of the Storm",
            heroClass = HeroClass.MARKSMAN, rarity = Rarity.RARE,
            stats = HeroStats.marksmanDefaults().copy(
                attackBase = 170f, attackPerLevel = 15f,
                attackRange = 550f, critRate = 0.2f
            ),
            skills = listOf(
                SkillTemplates.passiveSkill("storm_p", "Precise Aim", "Every 4th attack is a guaranteed crit with bonus range"),
                Skill(
                    id = "storm_s1", name = "Piercing Arrow", description = "Fire a piercing arrow hitting all enemies in line",
                    type = SkillType.SKILL_1, damageType = DamageType.PHYSICAL,
                    baseDamage = floatArrayOf(100f, 150f, 200f, 250f, 300f),
                    manaCost = floatArrayOf(45f, 50f, 55f, 60f, 65f),
                    cooldown = floatArrayOf(7f, 6.5f, 6f, 5.5f, 5f),
                    range = 600f, scalingAD = 1.1f
                ),
                Skill(
                    id = "storm_s2", name = "Rain of Arrows", description = "Fire arrows into the sky raining down on area",
                    type = SkillType.SKILL_2, damageType = DamageType.PHYSICAL,
                    baseDamage = floatArrayOf(80f, 120f, 160f, 200f, 240f),
                    manaCost = floatArrayOf(65f, 70f, 75f, 80f, 85f),
                    cooldown = floatArrayOf(10f, 9f, 8f, 7f, 6f),
                    range = 500f, aoeRadius = 300f, scalingAD = 0.8f
                ),
                Skill(
                    id = "storm_s3", name = "Wind Walk", description = "Dash and gain attack speed buff",
                    type = SkillType.SKILL_3, damageType = DamageType.PHYSICAL,
                    baseDamage = floatArrayOf(0f, 0f, 0f, 0f, 0f),
                    manaCost = floatArrayOf(50f, 55f, 60f, 65f, 70f),
                    cooldown = floatArrayOf(12f, 11f, 10f, 9f, 8f),
                    range = 300f
                ),
                Skill(
                    id = "storm_ult", name = "Tempest Shot", description = "Charge and fire devastating arrow across map",
                    type = SkillType.ULTIMATE, damageType = DamageType.PHYSICAL,
                    baseDamage = floatArrayOf(500f, 700f, 900f),
                    manaCost = floatArrayOf(100f, 120f, 140f),
                    cooldown = floatArrayOf(40f, 35f, 30f),
                    range = 1500f, scalingAD = 2.5f,
                    maxLevel = 3, unlockLevel = 4
                )
            ),
            lore = "Raised by the Sky Nomads, Storm Archer's arrows ride the wind itself. Legends say she can shoot down a star from the heavens.",
            price = 24000
        ),

        // === SUPPORT ===
        Hero(
            id = "divine_oracle", name = "Divine Oracle", title = "Voice of the Heavens",
            heroClass = HeroClass.SUPPORT, rarity = Rarity.EPIC,
            stats = HeroStats.supportDefaults().copy(
                mpBase = 1500f, mpPerLevel = 70f,
                magicBase = 90f, magicPerLevel = 10f,
                cooldownReduction = 0.15f
            ),
            skills = listOf(
                SkillTemplates.passiveSkill("divine_p", "Blessing", "Nearby allies gain 5% HP regen boost"),
                Skill(
                    id = "divine_s1", name = "Holy Light", description = "Heal an ally for a large amount",
                    type = SkillType.SKILL_1, damageType = DamageType.MAGIC,
                    baseDamage = floatArrayOf(-200f, -280f, -360f, -440f, -520f), // Negative = heal
                    manaCost = floatArrayOf(60f, 70f, 80f, 90f, 100f),
                    cooldown = floatArrayOf(8f, 7f, 6f, 5f, 4f),
                    range = 600f, scalingAP = 0.8f
                ),
                Skill(
                    id = "divine_s2", name = "Purify", description = "Remove all CC from an ally and grant CC immunity",
                    type = SkillType.SKILL_2, damageType = DamageType.MAGIC,
                    baseDamage = floatArrayOf(0f, 0f, 0f, 0f, 0f),
                    manaCost = floatArrayOf(70f, 75f, 80f, 85f, 90f),
                    cooldown = floatArrayOf(14f, 13f, 12f, 11f, 10f),
                    range = 500f
                ),
                Skill(
                    id = "divine_s3", name = "Smite", description = "Deal magic damage and slow enemy",
                    type = SkillType.SKILL_3, damageType = DamageType.MAGIC,
                    baseDamage = floatArrayOf(90f, 130f, 170f, 210f, 250f),
                    manaCost = floatArrayOf(55f, 60f, 65f, 70f, 75f),
                    cooldown = floatArrayOf(8f, 7.5f, 7f, 6.5f, 6f),
                    range = 400f, ccType = CCType.SLOW, ccDuration = 1.5f, scalingAP = 0.7f
                ),
                Skill(
                    id = "divine_ult", name = "Divine Resurrection", description = "Revive a fallen ally with 50% HP",
                    type = SkillType.ULTIMATE, damageType = DamageType.MAGIC,
                    baseDamage = floatArrayOf(0f, 0f, 0f),
                    manaCost = floatArrayOf(200f, 220f, 240f),
                    cooldown = floatArrayOf(90f, 80f, 70f),
                    range = 800f, maxLevel = 3, unlockLevel = 4
                )
            ),
            lore = "The chosen vessel of the Celestial Choir, Divine Oracle bridges the mortal and divine realms. Her voice mends wounds, purges darkness, and even defies death itself.",
            price = 32000
        )
    )
}
