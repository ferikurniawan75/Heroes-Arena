package com.heroesarena.models

import java.io.Serializable

/**
 * Player profile data - persists across sessions
 */
data class PlayerProfile(
    val id: String,
    val name: String,
    val level: Int = 1,
    val exp: Long = 0,
    val gold: Long = 5000,
    val diamonds: Int = 100,
    val rank: RankTier = RankTier.WARRIOR_I,
    val rankStars: Int = 0,
    val totalGames: Int = 0,
    val totalWins: Int = 0,
    val totalLosses: Int = 0,
    val mvpCount: Int = 0,
    val savageCount: Int = 0,
    val legendaryCount: Int = 0,
    val ownedHeroes: MutableList<String> = mutableListOf(),
    val ownedSkins: MutableList<String> = mutableListOf(),
    val matchHistory: MutableList<MatchResult> = mutableListOf(),
    val achievements: MutableList<String> = mutableListOf(),
    val settings: GameSettingsData = GameSettingsData(),
    val favoriteHeroes: MutableList<String> = mutableListOf(),
    val selectedSkin: MutableMap<String, String> = mutableMapOf(),
    val createdAt: Long = System.currentTimeMillis(),
    val lastLogin: Long = System.currentTimeMillis()
) : Serializable {

    fun winRate(): Float = if (totalGames > 0) totalWins.toFloat() / totalGames else 0f
    fun winRatePercent(): Int = (winRate() * 100).toInt()
    fun expToNextLevel(): Long = (level * 1000).toLong()
    fun expProgress(): Float = if (expToNextLevel() > 0) exp.toFloat() / expToNextLevel() else 0f
}

enum class RankTier(val displayName: String, val icon: String, val minStars: Int) {
    WARRIOR_III("Warrior III", "warrior", 0),
    WARRIOR_II("Warrior II", "warrior", 1),
    WARRIOR_I("Warrior I", "warrior", 2),
    ELITE_III("Elite III", "elite", 3),
    ELITE_II("Elite II", "elite", 4),
    ELITE_I("Elite I", "elite", 5),
    MASTER_III("Master III", "master", 6),
    MASTER_II("Master II", "master", 7),
    MASTER_I("Master I", "master", 8),
    GRANDMASTER_III("Grandmaster III", "grandmaster", 9),
    GRANDMASTER_II("Grandmaster II", "grandmaster", 10),
    GRANDMASTER_I("Grandmaster I", "grandmaster", 11),
    EPIC_III("Epic III", "epic", 12),
    EPIC_II("Epic II", "epic", 13),
    EPIC_I("Epic I", "epic", 14),
    LEGEND_III("Legend III", "legend", 15),
    LEGEND_II("Legend II", "legend", 16),
    LEGEND_I("Legend I", "legend", 17),
    MYTHIC("Mythic", "mythic", 18),
    MYTHIC_GLORY("Mythic Glory", "mythic_glory", 19);

    fun next(): RankTier? = entries.let { it.getOrNull(it.indexOf(this) + 1) }
    fun prev(): RankTier? = entries.let { it.getOrNull(it.indexOf(this) - 1) }
}

data class MatchResult(
    val matchId: String,
    val heroId: String,
    val mode: GameMode,
    val result: Boolean, // true = win
    val kills: Int,
    val deaths: Int,
    val assists: Int,
    val goldEarned: Int,
    val damageDealt: Long,
    val damageTaken: Long,
    val duration: Int, // seconds
    val isMvp: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
) : Serializable {

    fun kda(): Float = if (deaths > 0) (kills + assists).toFloat() / deaths else (kills + assists).toFloat()
    fun kdaString(): String = "$kills/$deaths/$assists"
}

data class GameSettingsData(
    var graphicsQuality: Int = 2, // 0=Low, 1=Medium, 2=High
    var frameRate: Int = 60,
    var showFps: Boolean = false,
    var showPing: Boolean = true,
    var cameraZoom: Float = 1.0f,
    var musicVolume: Float = 0.7f,
    var sfxVolume: Float = 0.8f,
    var voiceVolume: Float = 1.0f,
    var joystickSize: Int = 1, // 0=Small, 1=Medium, 2=Large
    var joystickOpacity: Float = 0.7f,
    var rightJoystick: Boolean = false,
    var quickCast: Boolean = false,
    var skillAim: Boolean = true,
    var autoAttack: Boolean = true,
    var lockMode: Boolean = false,
    var showDamageNumbers: Boolean = true,
    var showHealNumbers: Boolean = true,
    var minimapSize: Int = 1,
    var chatEnabled: Boolean = true,
    var notifications: Boolean = true,
    var screenShake: Boolean = true,
    var bloodEffect: Boolean = true,
    var deathRecap: Boolean = true,
    var recommendedItems: Boolean = true,
    var levelUpNotification: Boolean = true
) : Serializable
