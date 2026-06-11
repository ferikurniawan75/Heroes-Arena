package com.heroesarena.network

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Network manager for multiplayer and online features
 * Handles API communication, matchmaking, and real-time updates
 */
class NetworkManager private constructor() {

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    private val gson = Gson()
    private var authToken: String? = null
    private var baseUrl = "https://api.heroesarena.com/v1"

    companion object {
        @Volatile
        private var instance: NetworkManager? = null

        fun getInstance(): NetworkManager {
            return instance ?: synchronized(this) {
                instance ?: NetworkManager().also { instance = it }
            }
        }
    }

    fun setAuthToken(token: String) {
        authToken = token
    }

    fun setBaseUrl(url: String) {
        baseUrl = url
    }

    // === API Calls ===

    /**
     * Player login/authentication
     */
    fun login(username: String, password: String, callback: ApiCallback<LoginResponse>) {
        val body = gson.toJson(mapOf("username" to username, "password" to password))
            .toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("$baseUrl/auth/login")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onError(e.message ?: "Network error")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val loginResponse = gson.fromJson(responseBody, LoginResponse::class.java)
                    authToken = loginResponse.token
                    callback.onSuccess(loginResponse)
                } else {
                    callback.onError("Login failed: ${response.code}")
                }
            }
        })
    }

    /**
     * Start matchmaking for a game mode
     */
    fun startMatchmaking(mode: String, heroId: String, callback: ApiCallback<MatchResponse>) {
        val body = gson.toJson(mapOf("mode" to mode, "heroId" to heroId))
            .toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("$baseUrl/matchmaking/start")
            .post(body)
            .addAuthHeader()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onError(e.message ?: "Network error")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val matchResponse = gson.fromJson(responseBody, MatchResponse::class.java)
                    callback.onSuccess(matchResponse)
                } else {
                    callback.onError("Matchmaking failed: ${response.code}")
                }
            }
        })
    }

    /**
     * Report match results
     */
    fun reportMatchResult(matchId: String, result: Boolean, stats: Map<String, Any>, callback: ApiCallback<Boolean>) {
        val body = gson.toJson(mapOf(
            "matchId" to matchId,
            "result" to result,
            "stats" to stats
        )).toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("$baseUrl/match/result")
            .post(body)
            .addAuthHeader()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onError(e.message ?: "Network error")
            }

            override fun onResponse(call: Call, response: Response) {
                callback.onSuccess(response.isSuccessful)
            }
        })
    }

    /**
     * Get leaderboard
     */
    fun getLeaderboard(season: String = "current", callback: ApiCallback<List<LeaderboardEntry>>) {
        val request = Request.Builder()
            .url("$baseUrl/leaderboard?season=$season")
            .get()
            .addAuthHeader()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onError(e.message ?: "Network error")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val type = object : TypeToken<List<LeaderboardEntry>>() {}.type
                    val entries = gson.fromJson<List<LeaderboardEntry>>(responseBody, type)
                    callback.onSuccess(entries)
                } else {
                    callback.onError("Failed to load leaderboard")
                }
            }
        })
    }

    /**
     * Get player profile from server
     */
    fun getPlayerProfile(playerId: String, callback: ApiCallback<PlayerProfileResponse>) {
        val request = Request.Builder()
            .url("$baseUrl/player/$playerId")
            .get()
            .addAuthHeader()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onError(e.message ?: "Network error")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val profile = gson.fromJson(responseBody, PlayerProfileResponse::class.java)
                    callback.onSuccess(profile)
                } else {
                    callback.onError("Failed to load profile")
                }
            }
        })
    }

    private fun Request.Builder.addAuthHeader(): Request.Builder {
        authToken?.let { addHeader("Authorization", "Bearer $it") }
        return this
    }

    // === Response Models ===

    data class LoginResponse(
        val token: String,
        val playerId: String,
        val name: String
    )

    data class MatchResponse(
        val matchId: String,
        val serverUrl: String,
        val teammates: List<String>,
        val enemies: List<String>
    )

    data class LeaderboardEntry(
        val rank: Int,
        val playerId: String,
        val name: String,
        val rankTier: String,
        val points: Int
    )

    data class PlayerProfileResponse(
        val playerId: String,
        val name: String,
        val level: Int,
        val rank: String,
        val winRate: Float,
        val totalGames: Int
    )

    interface ApiCallback<T> {
        fun onSuccess(result: T)
        fun onError(message: String)
    }
}
