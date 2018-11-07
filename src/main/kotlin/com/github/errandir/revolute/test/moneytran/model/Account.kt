package com.github.errandir.revolute.test.moneytran.model

import com.github.errandir.revolute.test.moneytran.types.Id
import com.github.errandir.revolute.test.moneytran.types.Money

data class Account(val id: Id<Account>, val name: String, val money: Money) {

    fun withAnother(money: Money): Account {
        return Account(id, name, money)
    }

    fun withMore(money: Money): Account {
        return Account(id, name, this.money + money)
    }

    fun withLess(money: Money): Account? {
        val updatedMoney = (this.money - money) ?: return null
        return Account(id, name, updatedMoney)
    }

    fun has(money: Money): Boolean {
        return this.money.contains(money)
    }
}