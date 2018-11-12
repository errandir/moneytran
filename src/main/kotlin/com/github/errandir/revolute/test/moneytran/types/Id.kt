package com.github.errandir.revolute.test.moneytran.types

import java.util.concurrent.atomic.AtomicLong

data class Id<T>(val value: Long) : Comparable<Id<T>> {

    override fun compareTo(other: Id<T>): Int {
        return this.value.compareTo(other.value)
    }

    override fun toString(): String {
        return value.toString()
    }

    class Supplier<T> {
        private var lastValue = AtomicLong()
        fun next(): Id<T> {
            return Id(lastValue.incrementAndGet())
        }
    }
}