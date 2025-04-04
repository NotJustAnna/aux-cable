package net.notjustanna.auxcable.state

import io.reactivex.rxjava3.subjects.PublishSubject

enum class MessageType {
    INFO,
    WARNING,
    SUCCESS,
    ERROR
}

data class Message(val type: MessageType, val content: String)

class Logger {
    val subject = PublishSubject.create<Message>()

    fun info(content: String) {
        send(Message(MessageType.INFO, content))
    }

    fun warning(content: String) {
        send(Message(MessageType.WARNING, content))
    }

    fun success(content: String) {
        send(Message(MessageType.SUCCESS, content))
    }

    fun error(content: String) {
        send(Message(MessageType.ERROR, content))
    }

    private fun send(message: Message) {
        subject.onNext(message)
    }
}