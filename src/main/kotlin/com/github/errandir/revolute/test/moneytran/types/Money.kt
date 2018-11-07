package com.github.errandir.revolute.test.moneytran.types

data class Money(val amount: Long) {

    init {
        require(amount >= 0)
    }

    operator fun plus(money: Money): Money {
        return Money(amount + money.amount)
    }

    operator fun minus(that: Money): Money? {
        if (this.amount < that.amount) return null
        return Money(this.amount - that.amount)
    }

    fun contains(that: Money): Boolean {
        return this.amount > that.amount
    }
}