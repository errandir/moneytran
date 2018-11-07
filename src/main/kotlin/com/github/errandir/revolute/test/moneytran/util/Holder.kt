package com.github.errandir.revolute.test.moneytran.util

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class Holder<T>(@Volatile private var value: T) {
    private val lock = ReentrantLock()

    fun read(): T {
        return value
    }

    fun <R> locked(action: (Accessor<T>) -> R): R {
        return withLock(action)
    }

    fun <R> withLock(action: Accessor<T>.() -> R): R {
        return lock.withLock { accessor().action() }
    }

    private fun accessor() = object : Accessor<T> {

        override fun get() = value

        override fun set(newValue: T) {
            value = newValue
        }

        override fun update(updater: (T) -> T) {
            value = updater.invoke(value)
        }
    }
}