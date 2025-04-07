package net.notjustanna.auxcable.state.util

import io.reactivex.rxjava3.subjects.PublishSubject

class Flow {
    val subject = PublishSubject.create<Push>()

    fun push(id: String, extra: Any? = null) {
        subject.onNext(Push(id, extra))
    }

    data class Push(val id: String, val extra: Any? = null)
}