package com.github.errandir.revolute.test.moneytran.storage

import com.github.errandir.revolute.test.moneytran.model.Account
import com.github.errandir.revolute.test.moneytran.model.Transaction
import com.github.errandir.revolute.test.moneytran.model.Transaction.State.*
import com.github.errandir.revolute.test.moneytran.noMoney
import com.github.errandir.revolute.test.moneytran.types.Id
import com.github.errandir.revolute.test.moneytran.types.Money
import com.github.errandir.revolute.test.moneytran.util.Accessor
import com.github.errandir.revolute.test.moneytran.util.Holder
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

class InMemoryStorage : Storage {

    private val accountIds = Id.Supplier<Account>()
    private val transactionIds = Id.Supplier<Transaction>()

    private val accounts = ConcurrentHashMap<Id<Account>, Holder<Account>>(1000000, 100f, 1000)
    private val preparedTransactions = ConcurrentHashMap<Id<Transaction>, Holder<Transaction?>>(1000, 0.75f, 1000)
    private val finishedTransactions = ConcurrentHashMap<Id<Transaction>, Transaction>(1000000000, 100f, 1000)

    override fun getAccount(id: Id<Account>): Account? {
        return accounts[id]?.read()
    }

    override fun createAccount(name: String): Id<Account> {
        return accountIds.next().also {
            accounts[it] = Holder(Account(it, name, noMoney))
        }
    }

    override fun countAccounts(): Long {
        return accounts.size.toLong()
    }

    override fun deposit(id: Id<Account>, money: Money): Boolean {
        accounts[id]
            ?.withLock { update { it.withMore(money) } }
            ?: return false
        return true
    }

    override fun drawAll(id: Id<Account>): Money {
        return accounts[id]?.withLock {
            val account = get()
            set(account.withAnother(noMoney))
            account.money
        } ?: noMoney
    }

    override fun getTransaction(id: Id<Transaction>): Transaction? {
        return preparedTransactions[id]?.read()
            ?: finishedTransactions[id]
    }

    override fun prepareTransaction(src: Id<Account>, dst: Id<Account>, money: Money): Id<Transaction>? {
        if (dst == src) return null
        if (money.amount <= 0L) return null
        accounts[src] ?: return null
        accounts[dst] ?: return null
        if (!accounts[src]!!.read().has(money)) return null
        return transactionIds.next().also {
            preparedTransactions[it] = Holder<Transaction?>(
                Transaction(it, LocalDateTime.now(), PREPARED, src, dst, money))
        }
    }

    override fun commitTransaction(id: Id<Transaction>): Boolean {
        preparedTransactions[id]?.withLock {
            val transaction = get() ?: return@withLock
            val transferred = transfer(accounts[transaction.src]!!, accounts[transaction.dst]!!, transaction.money)
            finishedTransactions[id] = transaction.but(if (transferred) SUCCESS else DECLINED)
            preparedTransactions.remove(id)
        }
        return finishedTransactions[id]?.state == SUCCESS
    }

    private fun transfer(srcAccount: Holder<Account>, dstAccount: Holder<Account>, money: Money): Boolean {

        fun transfer(src: Accessor<Account>, dst: Accessor<Account>, money: Money): Boolean {
            return src.get().withLess(money)?.also { srcWithLessMoney ->
                src.set(srcWithLessMoney)
                dst.update { it.withMore(money) }
            } != null
        }

        return if (srcAccount.read().id < dstAccount.read().id) {
            srcAccount.locked { src -> dstAccount.locked { dst -> transfer(src, dst, money) } }
        } else {
            dstAccount.locked { dst -> srcAccount.locked { src -> transfer(src, dst, money) } }
        }
    }

}