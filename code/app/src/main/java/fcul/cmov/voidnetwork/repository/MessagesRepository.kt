package fcul.cmov.voidnetwork.repository

import fcul.cmov.voidnetwork.domain.Message
import fcul.cmov.voidnetwork.ui.utils.MAX_RECENT_MESSAGES

object MessagesRepository {

    private val _messages = mutableListOf<Message>()
    val messages: List<Message> get() = _messages

    operator fun plusAssign(message: Message) {
        synchronized(this) {
            _messages.add(message)
            if (_messages.size > MAX_RECENT_MESSAGES) {
                _messages.removeFirst()
            }
        }
    }

    fun load(messages: List<Message>) {
        synchronized(this) {
            _messages.clear()
            _messages.addAll(messages)
        }
    }
}
