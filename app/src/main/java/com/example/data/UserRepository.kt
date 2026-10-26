package com.example.data

object UserRepository {
    fun getUsers(): List<User> = ChatRepository.users.value
}
