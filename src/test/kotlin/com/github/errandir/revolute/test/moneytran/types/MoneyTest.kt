package com.github.errandir.revolute.test.moneytran.types

import kotlin.test.Test

class MoneyTest {

    @Test(expected = IllegalArgumentException::class) fun negativeValue() {
        Money(-100)
    }
}