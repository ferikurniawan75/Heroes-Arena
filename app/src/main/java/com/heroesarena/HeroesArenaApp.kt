package com.heroesarena

import android.app.Application
import android.os.StrictMode
import com.heroesarena.utils.GameSettings

class HeroesArenaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        GameSettings.init(this)

        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            )
        }
    }

    companion object {
        lateinit var instance: HeroesArenaApp
            private set
    }
}
