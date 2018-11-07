package com.github.errandir.revolute.test.moneytran.types

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MoneyTest {

    @Test(expected = IllegalArgumentException::class) fun negativeValue() {
        Money(-100)
    }

    @Test fun zeroValue() {
        Money(0)
    }

    @Test fun containsValue() {
        assertTrue(Money(0).contains(Money(0)))
        assertTrue(Money(123).contains(Money(0)))
        assertTrue(Money(123).contains(Money(0)))
        assertTrue(Money(123).contains(Money(45)))
        assertTrue(Money(123).contains(Money(123)))
        assertFalse(Money(0).contains(Money(45)))
        assertFalse(Money(45).contains(Money(123)))
    }
}