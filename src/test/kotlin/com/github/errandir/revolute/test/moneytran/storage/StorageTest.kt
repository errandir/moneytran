package com.github.errandir.revolute.test.moneytran.storage

import com.github.errandir.revolute.test.moneytran.model.Account
import com.github.errandir.revolute.test.moneytran.model.Transaction
import com.github.errandir.revolute.test.moneytran.noMoney
import com.github.errandir.revolute.test.moneytran.types.Id
import com.github.errandir.revolute.test.moneytran.types.Money
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import kotlin.test.*

abstract class StorageTest(freshStorage: Storage) {

    private val storage = freshStorage
    private val dummyId: Id<Account>
    private val aliceId: Id<Account>
    private val bobId: Id<Account>

    private val m500 = Money(500)
    private val m123 = Money(123)
    private val m321 = Money(321)
    private val m111 = Money(111)
    private val m300 = Money(300)
    private val m501 = Money(501)

    init {
        assertEquals(0, storage.countAccounts())
        dummyId = Id(-1)
        assertNull(storage.getAccount(dummyId))
        assertEquals(noMoney, storage.drawAll(dummyId))

        aliceId = storage.createAccount("Alice")
        assertEquals(1L, storage.countAccounts())
        checkAccount(aliceId, "Alice", noMoney)

        bobId = storage.createAccount("Bob")
        assertEquals(2L, storage.countAccounts())
        checkAccount(aliceId, "Alice", noMoney)
        checkAccount(bobId, "Bob", noMoney)

        assertEquals(noMoney, storage.drawAll(aliceId))
        storage.deposit(aliceId, m123)
        assertEquals(m123, storage.getAccount(aliceId)?.money)
        assertEquals(noMoney, storage.getAccount(bobId)?.money)

        storage.deposit(aliceId, m321)
        assertEquals(m123 + m321, storage.getAccount(aliceId)?.money)
        assertEquals(noMoney, storage.getAccount(bobId)?.money)

        storage.deposit(bobId, m111)
        assertEquals(m123 + m321, storage.drawAll(aliceId))
        assertEquals(noMoney, storage.getAccount(aliceId)?.money)
        assertEquals(m111, storage.getAccount(bobId)?.money)
    }

    private fun checkAccount(id: Id<Account>, expectedName: String, expectedMoney: Money) {
        val account = storage.getAccount(id)
        assertNotNull(account)
        assertEquals(id, account.id)
        assertEquals(expectedName, account.name)
        assertEquals(expectedMoney, account.money)
    }

    private fun checkAccount(id: Id<Account>, expectedMoney: Money?) {
        val account = storage.getAccount(id)
        assertNotNull(account)
        assertEquals(expectedMoney, account.money)
    }

    @BeforeTest fun init() {
        storage.drawAll(aliceId)
        storage.deposit(aliceId, m500)
        storage.drawAll(bobId)
        storage.deposit(bobId, m500)
    }

    @Test fun validTransaction() {
        val transaction = storage.prepareTransaction(aliceId, bobId, m300)
        assertNotNull(transaction)
        checkAccount(aliceId, m500)
        checkAccount(bobId, m500)
        assertEquals(true, storage.commitTransaction(transaction))
        checkAccount(aliceId, m500 - m300)
        checkAccount(bobId, m500 + m300)
        assertEquals(true, storage.commitTransaction(transaction))
    }

    @Test fun insufficientFunds() {
        assertNull(storage.prepareTransaction(aliceId, bobId, m501))
    }

    @Test fun concurrentTransactions() {
        val transaction1 = storage.prepareTransaction(aliceId, bobId, m300)
        val transaction2 = storage.prepareTransaction(aliceId, bobId, m300)
        assertNotNull(transaction2)
        checkAccount(aliceId, m500)
        checkAccount(bobId, m500)
        assertTrue(storage.commitTransaction(transaction2))
        assertFalse(storage.commitTransaction(transaction1!!))
        checkAccount(aliceId, m500 - m300)
        checkAccount(bobId, m500 + m300)
        assertTrue(storage.commitTransaction(transaction2))
        assertFalse(storage.commitTransaction(transaction1))
    }

    @Test fun absentSrc() {
        assertNull(storage.prepareTransaction(dummyId, bobId, m300))
    }

    @Test fun absentDst() {
        assertNull(storage.prepareTransaction(aliceId, dummyId, m300))
    }

    @Test fun sameAccount() {
        assertNull(storage.prepareTransaction(aliceId, aliceId, m300))
    }

    @Test open fun concurrentAccountsCreation() {
        val initialAccountsCount = storage.countAccounts()
        val threadCount = 10
        val accountPerThreadCount = 1000L
        val executor = Executors.newFixedThreadPool(threadCount)
        (1..threadCount).map { threadNumber ->
            executor.submit {
                (1..accountPerThreadCount).forEach { accountNumber ->
                    storage.createAccount("Acc-$threadNumber-$accountNumber")
                }
            }
        }.forEach { it.get() }
        assertEquals(threadCount * accountPerThreadCount, storage.countAccounts() - initialAccountsCount)
    }

    @Test(timeout = 1000) open fun concurrentTransactionsCreation() {
        val token = Any()
        val ids = ConcurrentHashMap<Id<Transaction>, Any>()
        val threadCount = 10
        val transactionPerThreadCount = 1000L
        val executor = Executors.newFixedThreadPool(threadCount)
        (1..threadCount).map {
            executor.submit {
                (1..transactionPerThreadCount).forEach {
                    ids[storage.prepareTransaction(aliceId, bobId, m123)!!] = token
                }
            }
        }.forEach { it.get() }
        assertEquals(threadCount * transactionPerThreadCount, ids.size.toLong())
    }

    @Test(timeout = 1000) open fun createManyTransactions() {
        (1..100_000).forEach {
            if (Thread.interrupted()) return println("Interrupted at $it transaction")
            val transaction1 = storage.prepareTransaction(aliceId, bobId, m300)!!
            val transaction2 = storage.prepareTransaction(bobId, aliceId, m300)!!
            storage.commitTransaction(transaction1)
            storage.commitTransaction(transaction2)
        }
    }
}