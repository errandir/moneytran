package com.github.errandir.revolute.test.moneytran.app

import com.github.errandir.revolute.test.moneytran.model.Transaction
import com.github.errandir.revolute.test.moneytran.storage.Storage
import com.github.errandir.revolute.test.moneytran.types.Id
import com.github.errandir.revolute.test.moneytran.types.Money

data class PrepareTransaction(val src: Long = -1, val dst: Long = -1, val amount: Long = -1) {
    fun applyTo(storage: Storage): Id<Transaction>? {
        if (amount <= 0) return null
        return storage.prepareTransaction(Id(src), Id(dst), Money(amount))
    }
}