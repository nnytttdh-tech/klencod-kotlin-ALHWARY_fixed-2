package com.example.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

object ChatRepository {

    private val scope = CoroutineScope(Dispatchers.Main)

    private val initialUsers = listOf(
        User(
            id = "ai_bot",
            name = "ALHWARY AI ✦",
            username = "alhwary_ai",
            email = "ai@alhwary.app",
            isOnline = true,
            lastMessage = "مرحباً بك! أنا مساعد الهواري الذكي، كيف أساعدك اليوم؟",
            unreadCount = 0,
            bio = "المساعد الذكي لتطبيق الهواري • للترجمة والإجابة السريعة",
            isAiAssistant = true
        )
    )

    private val _users = MutableStateFlow(initialUsers)
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private val _messages = MutableStateFlow<Map<String, List<Message>>>(emptyMap())
    val messages: StateFlow<Map<String, List<Message>>> = _messages.asStateFlow()

    private val _typingUsers = MutableStateFlow<Set<String>>(emptySet())
    val typingUsers: StateFlow<Set<String>> = _typingUsers.asStateFlow()

    private val _statuses = MutableStateFlow(emptyList<StatusUpdate>())
    val statuses: StateFlow<List<StatusUpdate>> = _statuses.asStateFlow()

    private val _calls = MutableStateFlow(emptyList<CallRecord>())
    val calls: StateFlow<List<CallRecord>> = _calls.asStateFlow()

    // Active call simulation state
    data class ActiveCallState(
        val isActive: Boolean = false,
        val user: User? = null,
        val isVideo: Boolean = false,
        val isMuted: Boolean = false,
        val isSpeaker: Boolean = false,
        val callDurationSeconds: Int = 0
    )

    private val _activeCall = MutableStateFlow(ActiveCallState())
    val activeCall: StateFlow<ActiveCallState> = _activeCall.asStateFlow()

    init {
        _messages.value = emptyMap()
    }

    private fun getCurrentTime(): String {
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
        return sdf.format(Date())
    }

    fun markAsRead(userId: String) {
        _users.update { list ->
            list.map { if (it.id == userId) it.copy(unreadCount = 0) else it }
        }
    }

    fun sendMessage(userId: String, text: String) {
        if (text.isBlank()) return
        val time = getCurrentTime()
        val newMsg = Message(
            id = UUID.randomUUID().toString(),
            senderId = "me",
            receiverId = userId,
            text = text.trim(),
            timestamp = time,
            isFromMe = true,
            status = MessageStatus.READ
        )

        // Add to messages
        _messages.update { currentMap ->
            val list = currentMap[userId].orEmpty() + newMsg
            currentMap + (userId to list)
        }

        // Update user lastMessage
        _users.update { list ->
            list.map { if (it.id == userId) it.copy(lastMessage = text.trim(), lastSeen = time) else it }
        }

        // Simulate contact reply
        triggerAutoReply(userId, text.trim())
    }

    private fun triggerAutoReply(userId: String, incomingText: String) {
        val user = _users.value.find { it.id == userId } ?: return

        scope.launch {
            delay(800)
            _typingUsers.update { it + userId }
            delay(1500)
            _typingUsers.update { it - userId }

            val replyText = generateReply(user, incomingText)
            val replyMsg = Message(
                id = UUID.randomUUID().toString(),
                senderId = userId,
                receiverId = "me",
                text = replyText,
                timestamp = getCurrentTime(),
                isFromMe = false,
                status = MessageStatus.READ
            )

            _messages.update { currentMap ->
                val list = currentMap[userId].orEmpty() + replyMsg
                currentMap + (userId to list)
            }

            _users.update { list ->
                list.map {
                    if (it.id == userId) it.copy(lastMessage = replyText) else it
                }
            }
        }
    }

    private fun generateReply(user: User, prompt: String): String {
        if (user.isAiAssistant) {
            val lower = prompt.lowercase()
            return when {
                "مرحبا" in prompt || "السلام" in prompt || "hello" in lower || "hi" in lower ->
                    "أهلاً وسهلاً بك! تطبيق الهواري يدعم أكثر من 15 لغة عالمية، وميزات ذكية لتسهيل التواصل الفوري. هل تود تجربة ميزة معينة؟"
                "ترجم" in prompt || "translate" in lower ->
                    "بالتأكيد! تطبيق الهواري يوفر ترجمة ذكية فورية لجميع المحادثات بنقرة واحدة."
                "الهواري" in prompt || "alhwary" in lower ->
                    "الهواري هو تطبيق مراسلة عربي وعالمي متكامل، يتميز بالسرعة والجمال وسهولة الاستخدام وأمان المحادثات."
                else ->
                    "شكراً لرسالتك! بصفتي مساعد الهواري الذكي، أنا جاهز دوماً لمساعدتك في التلخيص، الترجمة، أو الرد على استفساراتك."
            }
        }

        return "تم استلام رسالتك بنجاح، شكراً لتواصلك عبر تطبيق الهواري!"
    }

    fun translateMessage(messageId: String, userId: String) {
        _messages.update { currentMap ->
            val list = currentMap[userId].orEmpty().map { msg ->
                if (msg.id == messageId) {
                    val translated = when {
                        "こんにちは" in msg.text -> "مرحباً! كيف حالك؟ (ترجمة من اليابانية)"
                        "See you" in msg.text -> "أراك غداً! (ترجمة من الإنجليزية)"
                        "Cómo estás" in msg.text -> "كيف حالك؟ أنا معجب بالواجهة. (ترجمة من الإسبانية)"
                        "À bientôt" in msg.text -> "إلى اللقاء قريباً! (ترجمة من الفرنسية)"
                        "Guten Tag" in msg.text -> "طاب يومك! (ترجمة من الألمانية)"
                        "नमस्ते" in msg.text -> "مرحباً، كيف حالك؟ (ترجمة من الهندية)"
                        "你好" in msg.text -> "مرحباً! سعيد بالتعرف عليك. (ترجمة من الصينية)"
                        else -> "الترجمة الذكية: " + msg.text
                    }
                    msg.copy(translatedText = translated)
                } else msg
            }
            currentMap + (userId to list)
        }
    }

    fun addNewContact(name: String, email: String): User {
        val newId = UUID.randomUUID().toString()
        val cleanName = name.ifBlank { "مستخدم جديد" }
        val cleanEmail = email.ifBlank { "" }
        val generatedUsername = cleanName.trim().lowercase(Locale.ROOT)
            .replace(Regex("[^a-z0-9_]+"), "_")
            .trim('_')
            .ifBlank { "user_${newId.take(6)}" }
        val newUser = User(
            id = newId,
            name = cleanName,
            username = generatedUsername,
            email = cleanEmail,
            isOnline = true,
            lastMessage = "تمت إضافة جهة الاتصال",
            unreadCount = 0,
            bio = "مستخدم جديد في تطبيق الهواري"
        )
        _users.update { listOf(newUser) + it }
        _messages.update { currentMap ->
            currentMap + (newId to listOf(
                Message(
                    id = UUID.randomUUID().toString(),
                    senderId = newId,
                    receiverId = "me",
                    text = "مرحباً! لقد أضفتني إلى جهات اتصالك في تطبيق الهواري.",
                    timestamp = getCurrentTime(),
                    isFromMe = false
                )
            ))
        }
        return newUser
    }

    fun startCall(user: User, isVideo: Boolean) {
        _activeCall.value = ActiveCallState(
            isActive = true,
            user = user,
            isVideo = isVideo,
            isMuted = false,
            isSpeaker = isVideo
        )

        // Log call
        val record = CallRecord(
            id = UUID.randomUUID().toString(),
            userId = user.id,
            userName = user.name,
            userEmail = user.email,
            type = if (isVideo) CallType.VIDEO else CallType.VOICE,
            direction = CallDirection.OUTGOING,
            timestamp = "الآن",
            duration = "جارٍ..."
        )
        _calls.update { listOf(record) + it }
    }

    fun endCall() {
        _activeCall.value = ActiveCallState(isActive = false)
    }

    fun toggleMute() {
        _activeCall.update { it.copy(isMuted = !it.isMuted) }
    }

    fun toggleSpeaker() {
        _activeCall.update { it.copy(isSpeaker = !it.isSpeaker) }
    }
}
