package com.salkcoding.tunalands.config.section

import com.salkcoding.tunalands.lands.Lands
import com.salkcoding.tunalands.lands.Rank

data class Fuel(
    val price: Double,
    val defaultFuel: Long,
    val fuelAddAmounts: List<AddAmount>
) {
    fun getFuelRequirement(land: Lands): AddAmount {
        return fuelAddAmounts.filter {
            land.memberMap.filter { (_, it) ->
                it.rank != Rank.VISITOR && it.rank != Rank.PARTTIMEJOB
            }.size >= it.numOfMembers
        }.minOrNull() ?: fuelAddAmounts.maxOf { it }
    }

    data class AddAmount(
        val numOfMembers: Int,
        val addAmount: Long
    ) : Comparable<AddAmount> {
        override fun compareTo(other: AddAmount): Int {
            return this.addAmount.compareTo(other.addAmount)
        }
    }
}