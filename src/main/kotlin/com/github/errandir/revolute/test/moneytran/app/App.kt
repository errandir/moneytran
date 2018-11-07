package com.github.errandir.revolute.test.moneytran.app

import com.github.errandir.revolute.test.moneytran.storage.InMemoryStorage
import com.github.errandir.revolute.test.moneytran.storage.Storage
import com.github.errandir.revolute.test.moneytran.types.Id
import com.github.errandir.revolute.test.moneytran.types.Money
import io.javalin.Javalin

fun main(args: Array<String>) {

    val storage = InMemoryStorage(). also { init(it) }
    val app = Javalin.create().start(7000)

    app.post("/transaction") { ctx ->
        val transactionId = ctx.runCatching { validatedBody<PrepareTransaction>() }
            .getOrNull() ?.value ?.applyTo(storage) ?.value ?: -1
        ctx.result("$transactionId")
    }

    app.post("/transaction/:id") { ctx ->
        val success = ctx.pathParam("id").toLongOrNull()
            ?.let { storage.commitTransaction(Id(it)) } ?: false
        ctx.result(if (success) "1" else "0")
    }
}

fun init(storage: Storage) {
    fun Storage.createAccount(name: String, amount: Long) {
        createAccount(name).also { deposit(it, Money(amount)) }
    }
    storage.createAccount("Alice", 100)
    storage.createAccount("Bob", 200)
    storage.createAccount("Cooper", 300)
    storage.createAccount("Daniel", 400)
    storage.createAccount("Elsa", 500)
}
