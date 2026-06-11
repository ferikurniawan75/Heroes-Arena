package com.heroesarena.network

/**
 * Network manager stub for multiplayer and online features.
 * Online features are not currently available in this build.
 * All API methods report errors via callback to indicate the service is unavailable.
 */
class NetworkManager private constructor() {

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

    fun isOnline(): Boolean = false

    // === API Stubs ===

    /**
     * Player login/authentication (stub)
     */
    fun login(username: String, password: String, callback: ApiCallback<LoginResponse>) {
        callback.onError("Online features are not available in this build")
    }

    /**
     * Start matchmaking for a game mode (stub)
     */
    fun startMatchmaking(mode: String, heroId: String, callback: ApiCallback<MatchResponse>) {
        callback.onError("Online features are not available in this build")
    }

    /**
     * Report match results (stub)
     */
    fun reportMatchResult(matchId: String, result: Boolean, stats: Map<String, Any>, callback: ApiCallback<Boolean>) {
        callback.onError("Online features are not available in this build")
    }

    /**
     * Get leaderboard (stub)
     */
    fun getLeaderboard(season: String = "current", callback: ApiCallback<List<LeaderboardEntry>>) {
        callback.onError("Online features are not available in this build")
    }

    /**
     * Get player profile from server (stub)
     */
    fun getPlayerProfile(playerId: String, callback: ApiCallback<PlayerProfileResponse>) {
        callback.onError("Online features are not available in this build")
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
