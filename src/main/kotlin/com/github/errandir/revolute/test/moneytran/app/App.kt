package com.github.errandir.revolute.test.moneytran.app

import com.github.errandir.revolute.test.moneytran.storage.InMemoryStorage
import com.github.errandir.revolute.test.moneytran.storage.Storage
import com.github.errandir.revolute.test.moneytran.types.Id
import com.github.errandir.revolute.test.moneytran.types.Money
import io.javalin.Javalin

const val TRANSACTION_DECLINED = "-1"
const val SUCCESSFUL_COMMIT = "1"
const val FAILED_COMMIT = "0"

fun main(args: Array<String>) {
    app(sampleStorage()).start(7000)
}

fun app(storage: Storage) = Javalin.create().apply {

    post("/transaction") { ctx ->
        val prepareTransaction = ctx.runCatching { validatedBody<PrepareTransaction>() }.getOrNull()?.value
        val transactionId = prepareTransaction?.at(storage)
        ctx.result("${transactionId?.value ?: TRANSACTION_DECLINED}")
    }

    post("/transaction/:id") { ctx ->
        val success = ctx.pathParam("id").toLongOrNull()
            ?.let { storage.commitTransaction(Id(it)) } ?: false
        ctx.result(if (success) SUCCESSFUL_COMMIT else FAILED_COMMIT)
    }

}!!

fun sampleStorage(): Storage {

    fun Storage.createAccount(name: String, amount: Long) {
        createAccount(name).also { deposit(it, Money(amount)) }
    }

    return InMemoryStorage().apply {
        createAccount("Alice", 100)
        createAccount("Bob", 200)
        createAccount("Cooper", 300)
        createAccount("Daniel", 400)
        createAccount("Elsa", 500)
    }
}
