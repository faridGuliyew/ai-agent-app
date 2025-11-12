package com.example.agentapp.utils

class FeedbackChannel<T : Any, Out : Any> {
    val observers = mutableMapOf<String, suspend (T) -> Out?>()

    suspend fun send(data: T): Out? {
        for (observer in observers.values) {
            observer.invoke(data)?.let {
                return it
            }
        }

        return null
    }

    fun addObserver(tag: String, block: suspend (T) -> Out?) {
        observers.put(tag, block)
    }

    fun removeObserver(tag: String) {
        observers.remove(tag)
    }
}