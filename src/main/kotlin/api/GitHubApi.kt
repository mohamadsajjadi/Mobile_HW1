package api

import model.User
import model.Repository
import retrofit2.http.GET
import retrofit2.http.Path

interface GitHubApi {
    @GET("users/{username}")
    suspend fun getUser(@Path("username") username: String): User

    @GET("users/{username}/repos")
    suspend fun getUserRepos(@Path("username") username: String): List<Repository>
} 