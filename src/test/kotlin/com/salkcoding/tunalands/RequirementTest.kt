package com.salkcoding.tunalands

import com.salkcoding.tunalands.config.section.Flag
import com.salkcoding.tunalands.config.section.Fuel
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class RequirementTest {

    @Test
    fun fuelTest() {
        val fuelAddAmount = listOf(
            Fuel.AddAmount(1, 7),
            Fuel.AddAmount(3, 5),
            Fuel.AddAmount(5, 3),
            Fuel.AddAmount(7, 1)
        )

        val cnt = 1
        val fr = fuelAddAmount.filter {
            cnt >= it.numOfMembers
        }.minOrNull() ?: fuelAddAmount.maxOf { it }
        Assertions.assertEquals(7, fr.addAmount)
    }

    @Test
    fun activePriceTest() {
        val activePrice = listOf(
            Flag.ActivePrice(1, 5),
            Flag.ActivePrice(20, 5),
            Flag.ActivePrice(40, 7),
            Flag.ActivePrice(60, 9),
            Flag.ActivePrice(80, 11),
            Flag.ActivePrice(100, 13)
        )
        val cnt = 150
        val price = activePrice.filter {
            cnt >= it.chunk
        }.maxOrNull() ?: activePrice.maxOf { it }
        Assertions.assertEquals(13, price.price)
    }
}