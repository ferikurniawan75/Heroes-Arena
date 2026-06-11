package com.heroesarena.audio

import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.IBinder
import com.heroesarena.utils.GameSettings

/**
 * Game music and sound effect service
 */
class GameMusicService : Service() {

    private var bgmPlayer: MediaPlayer? = null
    private var soundPool: SoundPool? = null
    private val soundIds = mutableMapOf<String, Int>()
    private var musicVolume = 0.7f
    private var sfxVolume = 0.8f

    override fun onCreate() {
        super.onCreate()
        musicVolume = GameSettings.getMusicVolume()
        sfxVolume = GameSettings.getSfxVolume()

        soundPool = SoundPool.Builder()
            .setMaxStreams(10)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY_BGM -> playBGM()
            ACTION_STOP_BGM -> stopBGM()
            ACTION_PLAY_SFX -> {
                val sfxName = intent.getStringExtra(EXTRA_SFX_NAME) ?: return START_NOT_STICKY
                playSFX(sfxName)
            }
            ACTION_SET_MUSIC_VOLUME -> {
                musicVolume = intent.getFloatExtra(EXTRA_VOLUME, 0.7f)
                bgmPlayer?.setVolume(musicVolume, musicVolume)
            }
            ACTION_SET_SFX_VOLUME -> {
                sfxVolume = intent.getFloatExtra(EXTRA_VOLUME, 0.8f)
            }
        }
        return START_NOT_STICKY
    }

    private fun playBGM() {
        // In a real game, load actual music files
        // For now, we'll set up the structure
        try {
            bgmPlayer?.release()
            // bgmPlayer = MediaPlayer.create(this, R.raw.bgm_main)
            // bgmPlayer?.isLooping = true
            // bgmPlayer?.setVolume(musicVolume, musicVolume)
            // bgmPlayer?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopBGM() {
        bgmPlayer?.stop()
        bgmPlayer?.release()
        bgmPlayer = null
    }

    private fun playSFX(name: String) {
        val soundId = soundIds[name] ?: return
        soundPool?.play(soundId, sfxVolume, sfxVolume, 1, 0, 1f)
    }

    override fun onDestroy() {
        stopBGM()
        soundPool?.release()
        soundPool = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val ACTION_PLAY_BGM = "play_bgm"
        const val ACTION_STOP_BGM = "stop_bgm"
        const val ACTION_PLAY_SFX = "play_sfx"
        const val ACTION_SET_MUSIC_VOLUME = "set_music_volume"
        const val ACTION_SET_SFX_VOLUME = "set_sfx_volume"
        const val EXTRA_SFX_NAME = "sfx_name"
        const val EXTRA_VOLUME = "volume"

        // SFX names
        const val SFX_ATTACK = "attack"
        const val SFX_SKILL_1 = "skill_1"
        const val SFX_SKILL_2 = "skill_2"
        const val SFX_SKILL_3 = "skill_3"
        const val SFX_ULTIMATE = "ultimate"
        const val SFX_KILL = "kill"
        const val SFX_DEATH = "death"
        const val SFX_LEVEL_UP = "level_up"
        const val SFX_TOWER_DESTROY = "tower_destroy"
        const val SFX_MINION_KILL = "minion_kill"
        const val SFX_BUTTON_CLICK = "button_click"
        const val SFX_VICTORY = "victory"
        const val SFX_DEFEAT = "defeat"
    }
}
