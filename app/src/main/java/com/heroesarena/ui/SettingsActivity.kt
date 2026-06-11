package com.heroesarena.ui

import android.os.Bundle
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import com.heroesarena.R
import com.heroesarena.utils.GameSettings

/**
 * Settings Activity - comprehensive game settings
 */
class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings_container, SettingsFragment())
            .commit()

        findViewById<Button>(R.id.btnBack)?.setOnClickListener { finish() }
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            val context = preferenceManager.context
            val screen = preferenceManager.createPreferenceScreen(context)

            // === GRAPHICS ===
            val graphicsCategory = androidx.preference.PreferenceCategory(context).apply {
                title = "Graphics"
            }
            screen.addPreference(graphicsCategory)

            val graphicsQuality = androidx.preference.ListPreference(context).apply {
                key = "graphics_quality"
                title = "Graphics Quality"
                entries = arrayOf("Low", "Medium", "High")
                entryValues = arrayOf("0", "1", "2")
                setDefaultValue("2")
                summary = "High"
            }
            graphicsCategory.addPreference(graphicsQuality)

            val frameRate = androidx.preference.ListPreference(context).apply {
                key = "frame_rate"
                title = "Frame Rate"
                entries = arrayOf("30 FPS", "60 FPS", "90 FPS", "120 FPS")
                entryValues = arrayOf("30", "60", "90", "120")
                setDefaultValue("60")
                summary = "60 FPS"
            }
            graphicsCategory.addPreference(frameRate)

            val showFps = androidx.preference.SwitchPreferenceCompat(context).apply {
                key = "show_fps"
                title = "Show FPS Counter"
                summary = "Display frames per second on screen"
                setDefaultValue(false)
            }
            graphicsCategory.addPreference(showFps)

            val showPing = androidx.preference.SwitchPreferenceCompat(context).apply {
                key = "show_ping"
                title = "Show Ping"
                summary = "Display network latency"
                setDefaultValue(true)
            }
            graphicsCategory.addPreference(showPing)

            val screenShake = androidx.preference.SwitchPreferenceCompat(context).apply {
                key = "screen_shake"
                title = "Screen Shake"
                summary = "Camera shake on big impacts"
                setDefaultValue(true)
            }
            graphicsCategory.addPreference(screenShake)

            val bloodEffect = androidx.preference.SwitchPreferenceCompat(context).apply {
                key = "blood_effect"
                title = "Blood Effects"
                summary = "Show blood effects in combat"
                setDefaultValue(true)
            }
            graphicsCategory.addPreference(bloodEffect)

            val damageNumbers = androidx.preference.SwitchPreferenceCompat(context).apply {
                key = "damage_numbers"
                title = "Damage Numbers"
                summary = "Show floating damage numbers"
                setDefaultValue(true)
            }
            graphicsCategory.addPreference(damageNumbers)

            // === AUDIO ===
            val audioCategory = androidx.preference.PreferenceCategory(context).apply {
                title = "Audio"
            }
            screen.addPreference(audioCategory)

            val musicVolume = androidx.preference.SeekBarPreference(context).apply {
                key = "music_volume"
                title = "Music Volume"
                min = 0
                max = 100
                setDefaultValue(70)
                summary = "70%"
            }
            audioCategory.addPreference(musicVolume)

            val sfxVolume = androidx.preference.SeekBarPreference(context).apply {
                key = "sfx_volume"
                title = "SFX Volume"
                min = 0
                max = 100
                setDefaultValue(80)
                summary = "80%"
            }
            audioCategory.addPreference(sfxVolume)

            val voiceVolume = androidx.preference.SeekBarPreference(context).apply {
                key = "voice_volume"
                title = "Voice Volume"
                min = 0
                max = 100
                setDefaultValue(100)
                summary = "100%"
            }
            audioCategory.addPreference(voiceVolume)

            // === CONTROLS ===
            val controlsCategory = androidx.preference.PreferenceCategory(context).apply {
                title = "Controls"
            }
            screen.addPreference(controlsCategory)

            val joystickSize = androidx.preference.ListPreference(context).apply {
                key = "joystick_size"
                title = "Joystick Size"
                entries = arrayOf("Small", "Medium", "Large")
                entryValues = arrayOf("0", "1", "2")
                setDefaultValue("1")
                summary = "Medium"
            }
            controlsCategory.addPreference(joystickSize)

            val joystickOpacity = androidx.preference.SeekBarPreference(context).apply {
                key = "joystick_opacity"
                title = "Joystick Opacity"
                min = 20
                max = 100
                setDefaultValue(70)
                summary = "70%"
            }
            controlsCategory.addPreference(joystickOpacity)

            val quickCast = androidx.preference.SwitchPreferenceCompat(context).apply {
                key = "quick_cast"
                title = "Quick Cast"
                summary = "Skills cast immediately without aiming"
                setDefaultValue(false)
            }
            controlsCategory.addPreference(quickCast)

            val autoAttack = androidx.preference.SwitchPreferenceCompat(context).apply {
                key = "auto_attack"
                title = "Auto Attack"
                summary = "Automatically attack nearest enemy"
                setDefaultValue(true)
            }
            controlsCategory.addPreference(autoAttack)

            val lockMode = androidx.preference.SwitchPreferenceCompat(context).apply {
                key = "lock_mode"
                title = "Hero Lock Mode"
                summary = "Lock attacks to targeted hero"
                setDefaultValue(false)
            }
            controlsCategory.addPreference(lockMode)

            // === GAMEPLAY ===
            val gameplayCategory = androidx.preference.PreferenceCategory(context).apply {
                title = "Gameplay"
            }
            screen.addPreference(gameplayCategory)

            val recommendedItems = androidx.preference.SwitchPreferenceCompat(context).apply {
                key = "recommended_items"
                title = "Recommended Items"
                summary = "Show item recommendations in-game"
                setDefaultValue(true)
            }
            gameplayCategory.addPreference(recommendedItems)

            val levelUpNotif = androidx.preference.SwitchPreferenceCompat(context).apply {
                key = "level_up_notification"
                title = "Level Up Notification"
                summary = "Show level up effects and sounds"
                setDefaultValue(true)
            }
            gameplayCategory.addPreference(levelUpNotif)

            val deathRecap = androidx.preference.SwitchPreferenceCompat(context).apply {
                key = "death_recap"
                title = "Death Recap"
                summary = "Show damage breakdown when dying"
                setDefaultValue(true)
            }
            gameplayCategory.addPreference(deathRecap)

            val minimapSize = androidx.preference.ListPreference(context).apply {
                key = "minimap_size"
                title = "Minimap Size"
                entries = arrayOf("Small", "Medium", "Large")
                entryValues = arrayOf("0", "1", "2")
                setDefaultValue("1")
                summary = "Medium"
            }
            gameplayCategory.addPreference(minimapSize)

            preferenceScreen = screen
        }
    }
}
