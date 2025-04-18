package repository

import api.GitHubApi
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import model.User
import java.io.File

class UserRepository(private val api: GitHubApi) {
    private val gson = Gson()
    private val cacheFile = File("user_cache.json")
    private val userCache = mutableMapOf<String, User>()

    init {
        loadCache()
    }

    private fun loadCache() {
        if (cacheFile.exists()) {
            val json = cacheFile.readText()
            val type = object : TypeToken<Map<String, User>>() {}.type
            userCache.putAll(gson.fromJson(json, type))
        }
    }

    private fun saveCache() {
        val json = gson.toJson(userCache)
        cacheFile.writeText(json)
    }

    suspend fun getUser(username: String): User {
        val normalizedUsername = username.lowercase()
        return userCache[normalizedUsername] ?: fetchAndCacheUser(normalizedUsername)
    }

    private suspend fun fetchAndCacheUser(username: String): User {
        val user = api.getUser(username)
        val repos = api.getUserRepos(username)
        val userWithRepos = user.copy(repos = repos)
        userCache[username] = userWithRepos
        saveCache()
        return userWithRepos
    }

    fun searchUser(username: String): User? {
        return userCache[username.lowercase()]
    }

    fun searchRepositories(query: String): List<model.Repository> {
        return userCache.values
            .flatMap { it.repos }
            .filter { it.name.contains(query, ignoreCase = true) }
    }

    fun getAllCachedUsers(): List<User> {
        return userCache.values.toList()
    }
} 