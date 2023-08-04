package com.salkcoding.tunalands

import com.salkcoding.tunalands.config.section.Fuel
import org.junit.Assert.assertEquals
import org.junit.jupiter.api.Test

class FuelRequirementTest {

    @Test
    fun requirementTest() {
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
            assertEquals(i, fr.dayPerFuel)
        }
    }
}