package viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import model.User
import model.Repository
import repository.UserRepository

class UserViewModel(val repository: UserRepository) {
    private val scope = CoroutineScope(Dispatchers.IO)

    fun fetchUser(username: String, onSuccess: (User) -> Unit, onError: (Throwable) -> Unit) {
        scope.launch {
            try {
                val user = repository.getUser(username)
                onSuccess(user)
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    fun searchUser(username: String): User? {
        return repository.searchUser(username)
    }

    fun searchRepositories(query: String): List<Repository> {
        return repository.searchRepositories(query)
    }

    fun getAllCachedUsers(): List<User> {
        return repository.getAllCachedUsers()
    }
} 