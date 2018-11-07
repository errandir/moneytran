package com.github.errandir.revolute.test.moneytran.util

interface Accessor<T> {
    fun get(): T
    fun set(newValue: T)
    fun update(updater: (T) -> T)
}