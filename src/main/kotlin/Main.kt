package org.example

import api.GitHubApi
import model.User
import model.Repository
import repository.UserRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import viewmodel.UserViewModel
import kotlin.system.exitProcess
import kotlinx.coroutines.runBlocking

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.github.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api = retrofit.create(GitHubApi::class.java)
    val repository = UserRepository(api)
    val viewModel = UserViewModel(repository)

    while (true) {
        println("\nGitHub User Data Fetcher")
        println("1. Fetch GitHub user data")
        println("2. Display cached users")
        println("3. Search user in cache")
        println("4. Search repositories in cache")
        println("5. Exit")
        print("Enter your choice: ")

        when (readlnOrNull()?.toIntOrNull()) {
            1 -> fetchUserData(viewModel)
            2 -> displayCachedUsers(viewModel)
            3 -> searchUser(viewModel)
            4 -> searchRepositories(viewModel)
            5 -> exitProcess(0)
            else -> println("Invalid choice. Please try again.")
        }
    }
}

private fun fetchUserData(viewModel: UserViewModel) {
    print("Enter GitHub username: ")
    val username = readlnOrNull() ?: return

    runBlocking {
        try {
            val user = viewModel.repository.getUser(username)
            println("\nUser Information:")
            println("Username: ${user.login}")
            println("Followers: ${user.followers}")
            println("Following: ${user.following}")
            println("Created at: ${user.createdAt}")
            println("\nPublic Repositories:")
            user.repos.forEach { repo ->
                println("- ${repo.name}: ${repo.description ?: "No description"}")
                println("  URL: ${repo.htmlUrl}")
            }
        } catch (e: Exception) {
            println("Error fetching user data: ${e.message}")
        }
    }
}

private fun displayCachedUsers(viewModel: UserViewModel) {
    val users = viewModel.getAllCachedUsers()
    if (users.isEmpty()) {
        println("No cached users found.")
        return
    }

    println("\nCached Users:")
    users.forEach { user ->
        println("- ${user.login} (${user.repos.size} repositories)")
    }
}

private fun searchUser(viewModel: UserViewModel) {
    print("Enter username to search: ")
    val username = readlnOrNull() ?: return

    val user = viewModel.searchUser(username)
    if (user == null) {
        println("User not found in cache.")
        return
    }

    println("\nUser Information:")
    println("Username: ${user.login}")
    println("Followers: ${user.followers}")
    println("Following: ${user.following}")
    println("Created at: ${user.createdAt}")
}

private fun searchRepositories(viewModel: UserViewModel) {
    print("Enter repository name to search: ")
    val query = readlnOrNull() ?: return

    val repos = viewModel.searchRepositories(query)
    if (repos.isEmpty()) {
        println("No repositories found matching '$query'.")
        return
    }

    println("\nMatching Repositories:")
    repos.forEach { repo ->
        println("- ${repo.name}: ${repo.description ?: "No description"}")
        println("  URL: ${repo.htmlUrl}")
    }
}