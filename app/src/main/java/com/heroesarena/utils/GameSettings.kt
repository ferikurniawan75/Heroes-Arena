package com.heroesarena.utils

import android.content.Context
import android.content.SharedPreferences
import com.heroesarena.models.*
import com.google.gson.Gson

/**
 * Game settings manager - persists all game settings and player data
 */
object GameSettings {
    private const val PREFS_NAME = "heroes_arena_prefs"
    private const val KEY_PLAYER_PROFILE = "player_profile"
    private const val KEY_FIRST_LAUNCH = "first_launch"
    private const val KEY_GRAPHICS_QUALITY = "graphics_quality"
    private const val KEY_FRAME_RATE = "frame_rate"
    private const val KEY_MUSIC_VOLUME = "music_volume"
    private const val KEY_SFX_VOLUME = "sfx_volume"
    private const val KEY_JOYSTICK_SIZE = "joystick_size"
    private const val KEY_JOYSTICK_OPACITY = "joystick_opacity"

    private lateinit var prefs: SharedPreferences
    private val gson = Gson()

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun isFirstLaunch(): Boolean = prefs.getBoolean(KEY_FIRST_LAUNCH, true)

    fun setFirstLaunchComplete() {
        prefs.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply()
    }

    // Player Profile
    fun savePlayerProfile(profile: PlayerProfile) {
        prefs.edit().putString(KEY_PLAYER_PROFILE, gson.toJson(profile)).apply()
    }

    fun loadPlayerProfile(): PlayerProfile {
        val json = prefs.getString(KEY_PLAYER_PROFILE, null)
        return if (json != null) {
            gson.fromJson(json, PlayerProfile::class.java)
        } else {
            createDefaultProfile()
        }
    }

    private fun createDefaultProfile(): PlayerProfile {
        val profile = PlayerProfile(
            id = "player_${System.currentTimeMillis()}",
            name = "Player",
            ownedHeroes = mutableListOf(
                "blade_dancer", "storm_archer" // Free starter heroes
            )
        )
        savePlayerProfile(profile)
        return profile
    }

    // Graphics settings
    fun getGraphicsQuality(): Int = prefs.getInt(KEY_GRAPHICS_QUALITY, 2)
    fun setGraphicsQuality(quality: Int) = prefs.edit().putInt(KEY_GRAPHICS_QUALITY, quality).apply()

    fun getFrameRate(): Int = prefs.getInt(KEY_FRAME_RATE, 60)
    fun setFrameRate(fps: Int) = prefs.edit().putInt(KEY_FRAME_RATE, fps).apply()

    // Audio settings
    fun getMusicVolume(): Float = prefs.getFloat(KEY_MUSIC_VOLUME, 0.7f)
    fun setMusicVolume(vol: Float) = prefs.edit().putFloat(KEY_MUSIC_VOLUME, vol).apply()

    fun getSfxVolume(): Float = prefs.getFloat(KEY_SFX_VOLUME, 0.8f)
    fun setSfxVolume(vol: Float) = prefs.edit().putFloat(KEY_SFX_VOLUME, vol).apply()

    // Controls
    fun getJoystickSize(): Int = prefs.getInt(KEY_JOYSTICK_SIZE, 1)
    fun setJoystickSize(size: Int) = prefs.edit().putInt(KEY_JOYSTICK_SIZE, size).apply()

    fun getJoystickOpacity(): Float = prefs.getFloat(KEY_JOYSTICK_OPACITY, 0.7f)
    fun setJoystickOpacity(opacity: Float) = prefs.edit().putFloat(KEY_JOYSTICK_OPACITY, opacity).apply()
}
