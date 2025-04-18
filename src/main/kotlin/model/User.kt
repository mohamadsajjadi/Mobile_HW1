package model

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class User(
    val login: String,
    val followers: Int,
    val following: Int,
    @SerializedName("created_at")
    val createdAt: String,
    val repos: List<Repository> = emptyList()
)

data class Repository(
    val name: String,
    val description: String?,
    @SerializedName("html_url")
    val htmlUrl: String
) 