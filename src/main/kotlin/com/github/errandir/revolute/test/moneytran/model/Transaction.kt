package com.github.errandir.revolute.test.moneytran.model

import com.github.errandir.revolute.test.moneytran.types.Id
import com.github.errandir.revolute.test.moneytran.types.Money
import java.time.LocalDateTime

data class Transaction(
    val id: Id<Transaction>, val timestamp: LocalDateTime, val state: State,
    val src: Id<Account>, val dst: Id<Account>, val money: Money
) {
    fun but(state: State): Transaction {
        return Transaction(id, timestamp, state, src, dst, money)
    }

    enum class State { DECLINED, PREPARED, SUCCESS }
}