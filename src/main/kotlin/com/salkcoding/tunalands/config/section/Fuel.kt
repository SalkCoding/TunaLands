package com.salkcoding.tunalands.config.section

import com.salkcoding.tunalands.lands.Lands
import com.salkcoding.tunalands.lands.Rank

data class Fuel(
    val price: Double,
    val defaultFuel: Int,
    val imposeTime: Int,
    val fuelRequirements: List<FuelRequirement>
) {
    fun getFuelRequirement(land: Lands): FuelRequirement {
        return fuelRequirements.filter {
            land.memberMap.filter { (_, it) ->
                it.rank != Rank.VISITOR && it.rank != Rank.PARTTIMEJOB
            }.size <= it.numOfMembers
        }.minOrNull() ?: fuelRequirements.maxOf { it }
    }

    data class FuelRequirement(
        val numOfMembers: Int,
        val dayPerFuel: Int
    ) : Comparable<FuelRequirement> {
        override fun compareTo(other: FuelRequirement): Int {
            return this.dayPerFuel.compareTo(other.dayPerFuel)
        }
    }
}