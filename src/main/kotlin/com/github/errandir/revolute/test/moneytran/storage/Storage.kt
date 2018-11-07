package com.github.errandir.revolute.test.moneytran.storage

import com.github.errandir.revolute.test.moneytran.model.Account
import com.github.errandir.revolute.test.moneytran.model.Transaction
import com.github.errandir.revolute.test.moneytran.types.Id
import com.github.errandir.revolute.test.moneytran.types.Money

interface Storage {

    fun getAccount(id: Id<Account>): Account?
    fun createAccount(name: String): Id<Account>
    fun countAccounts(): Long
    fun deposit(id: Id<Account>, money: Money): Boolean
    fun drawAll(id: Id<Account>): Money
    fun getTransaction(id: Id<Transaction>): Transaction?

    fun prepareTransaction(src: Id<Account>, dst: Id<Account>, money: Money): Id<Transaction>?
    fun commitTransaction(id: Id<Transaction>): Boolean
}