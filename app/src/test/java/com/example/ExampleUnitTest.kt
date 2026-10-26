package com.example

import com.example.data.ChatRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testInitialUsersLoaded() {
        val users = ChatRepository.users.value
        assertTrue(users.isNotEmpty())
        val ahmed = users.find { it.id == "1" }
        assertNotNull(ahmed)
        assertEquals("أحمد محمد", ahmed?.name)
    }

    @Test
    fun testAddNewContact() {
        val newUser = ChatRepository.addNewContact("علي حسن", "ali@gmail.com")
        val users = ChatRepository.users.value
        assertTrue(users.any { it.id == newUser.id })
    }

    @Test
    fun testMarkAsRead() {
        ChatRepository.markAsRead("1")
        val ahmed = ChatRepository.users.value.find { it.id == "1" }
        assertEquals(0, ahmed?.unreadCount)
    }
}
