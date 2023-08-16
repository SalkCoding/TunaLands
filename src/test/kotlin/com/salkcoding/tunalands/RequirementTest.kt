package com.salkcoding.tunalands

import com.salkcoding.tunalands.config.section.Flag
import com.salkcoding.tunalands.config.section.Fuel
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class RequirementTest {

    @Test
    fun fuelTest() {
        val fuelRequirements = listOf(
            Fuel.FuelRequirement(1, 1),
            Fuel.FuelRequirement(2, 2),
            Fuel.FuelRequirement(3, 3),
            Fuel.FuelRequirement(4, 4)
        )
        for (i in 1..4) {
            val fr = fuelRequirements.filter {
                i <= it.numOfMembers
            }.minOrNull() ?: fuelRequirements.maxOf { it }
            Assertions.assertEquals(i, fr.dayPerFuel)
        }
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