package com.heroesarena

import android.app.Application
import android.os.Build
import android.os.StrictMode
import com.heroesarena.utils.GameSettings

class HeroesArenaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        GameSettings.init(this)

        // Enable StrictMode on debuggable builds (API 28+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && isDebuggable()) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            )
        }
    }

    private fun isDebuggable(): Boolean {
        return (applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0
    }

    companion object {
        lateinit var instance: HeroesArenaApp
            private set
    }
}
