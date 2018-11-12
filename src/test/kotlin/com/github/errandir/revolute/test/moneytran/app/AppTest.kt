package com.github.errandir.revolute.test.moneytran.app

import io.javalin.Javalin
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AppTest {

    private val port = 7000

    private var app: Javalin? = null
    
    @BeforeTest fun start() {
        app = app(sampleStorage()).start(port)
    }

    @AfterTest fun stop() {
        app!!.stop()
        app = null
    }

    private fun prepareTransaction(data: Any? = null, json: Any? = null) = khttp.post(
        url = "http://localhost:$port/transaction", data = data, json = json
    ).text

    private fun commitTransaction(id: Any, data: Any? = null) = khttp.post(
        url = "http://localhost:$port/transaction/$id"
    ).text

    @Test fun validTransaction() {
        assertEquals(FAILED_COMMIT, commitTransaction("1"))
        assertEquals("1", prepareTransaction(json = mapOf("src" to 1, "dst" to 2, "amount" to 50)))
        assertEquals(SUCCESSFUL_COMMIT, commitTransaction("1"))
        assertEquals(SUCCESSFUL_COMMIT, commitTransaction("1", "Some data that has no meaning"))
    }

    @Test fun concurrentTransactions() {
        assertEquals("1", prepareTransaction(json = mapOf("src" to 1, "dst" to 2, "amount" to 75)))
        assertEquals("2", prepareTransaction(json = mapOf("src" to 1, "dst" to 2, "amount" to 75)))
        assertEquals(SUCCESSFUL_COMMIT, commitTransaction("2"))
        assertEquals(FAILED_COMMIT, commitTransaction("1"))
    }

    @Test fun insufficientFunds() {
        assertEquals(TRANSACTION_DECLINED, prepareTransaction(json = mapOf("src" to 1, "dst" to 2, "amount" to 150)))
    }

    @Test fun invalidData() {
        assertEquals(TRANSACTION_DECLINED, prepareTransaction(data = "invalid"))
    }

    @Test fun absentSrc() {
        assertEquals(TRANSACTION_DECLINED, prepareTransaction(json = mapOf("dst" to 2, "amount" to 150)))
    }

    @Test fun absentDst() {
        assertEquals(TRANSACTION_DECLINED, prepareTransaction(json = mapOf("src" to 1, "amount" to 150)))
    }

    @Test fun absentAmount() {
        assertEquals(TRANSACTION_DECLINED, prepareTransaction(json = mapOf("src" to 1, "dst" to 2)))
    }

    @Test fun zeroAmount() {
        assertEquals(TRANSACTION_DECLINED, prepareTransaction(json = mapOf("src" to 1, "dst" to 2, "amount" to 0)))
    }

    @Test fun negativeAmount() {
        assertEquals(TRANSACTION_DECLINED, prepareTransaction(json = mapOf("src" to 1, "dst" to 2, "amount" to -50)))
    }

    @Test fun invalidSrc() {
        assertEquals(TRANSACTION_DECLINED, prepareTransaction(json = mapOf("src" to "invalid", "dst" to 2, "amount" to 50)))
    }

    @Test fun invalidDst() {
        assertEquals(TRANSACTION_DECLINED, prepareTransaction(json = mapOf("src" to 1, "dst" to "invalid", "amount" to 50)))
    }

    @Test fun invalidAmount() {
        assertEquals(TRANSACTION_DECLINED, prepareTransaction(json = mapOf("src" to 1, "dst" to "invalid", "amount" to "invalid")))
    }

    @Test fun nonExistingSrc() {
        assertEquals(TRANSACTION_DECLINED, prepareTransaction(json = mapOf("src" to -1, "dst" to 2, "amount" to 50)))
        assertEquals(TRANSACTION_DECLINED, prepareTransaction(json = mapOf("src" to 0, "dst" to 2, "amount" to 50)))
        assertEquals(TRANSACTION_DECLINED, prepareTransaction(json = mapOf("src" to 7, "dst" to 2, "amount" to 50)))
    }

    @Test fun nonExistingDst() {
        assertEquals(TRANSACTION_DECLINED, prepareTransaction(json = mapOf("src" to 1, "dst" to 7, "amount" to 50)))
    }
}