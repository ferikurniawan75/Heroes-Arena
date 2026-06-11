package com.heroesarena.models

/**
 * Hero class enumeration - all hero types in the game
 */
enum class HeroClass(val displayName: String, val color: Long) {
    TANK("Tank", 0xFF4CAF50),
    FIGHTER("Fighter", 0xFFFF9800),
    ASSASSIN("Assassin", 0xFF9C27B0),
    MAGE("Mage", 0xFF2196F3),
    MARKSMAN("Marksman", 0xFFF44336),
    SUPPORT("Support", 0xFF00BCD4);

    fun isMelee(): Boolean = this == TANK || this == FIGHTER || this == ASSASSIN
    fun isRanged(): Boolean = this == MAGE || this == MARKSMAN || this == SUPPORT
}

/**
 * Rarity levels for heroes and items
 */
enum class Rarity(val displayName: String, val color: Long, val multiplier: Float) {
    COMMON("Common", 0xFF9E9E9E, 1.0f),
    UNCOMMON("Uncommon", 0xFF4CAF50, 1.2f),
    RARE("Rare", 0xFF2196F3, 1.5f),
    EPIC("Epic", 0xFF9C27B0, 2.0f),
    LEGENDARY("Legendary", 0xFFFFD740, 3.0f)
}

/**
 * Damage types
 */
enum class DamageType {
    PHYSICAL, MAGIC, TRUE
}

/**
 * Crowd control types
 */
enum class CCType(val duration: Float) {
    STUN(1.5f),
    SLOW(2.0f),
    SILENCE(2.0f),
    ROOT(1.5f),
    KNOCKUP(1.0f),
    FEAR(1.5f),
    TAUNT(1.5f),
    FROZEN(1.0f)
}

/**
 * Game modes available
 */
enum class GameMode(val displayName: String, val playersPerTeam: Int, val timeLimit: Int) {
    CLASSIC("Classic 5v5", 5, 1200),
    RANKED("Ranked Match", 5, 1200),
    BRAWL("Brawl Mode", 3, 600),
    CUSTOM("Custom Game", 5, 1200),
    VS_AI("VS AI", 5, 1200)
}

/**
 * Team enumeration
 */
enum class Team {
    BLUE, RED, NEUTRAL;

    fun opposite(): Team = when (this) {
        BLUE -> RED
        RED -> BLUE
        NEUTRAL -> NEUTRAL
    }
}

/**
 * Game states
 */
enum class GameState {
    WAITING, HERO_SELECT, LOADING, PLAYING, PAUSED, ENDED
}

/**
 * Lane positions
 */
enum class Lane {
    TOP, MID, BOTTOM, JUNGLE, ROAM
}
