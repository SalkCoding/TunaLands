package com.salkcoding.tunalands.config.section

import com.salkcoding.tunalands.lands.Lands

data class Flag(
    val takeProtectFlagPrice: Double,
    val releaseProtectFlagPrice: Double,
    val activePrice: List<ActivePrice>,

    val takeFarmFlagPrice: Double,
    val releaseFarmFlagPrice: Double,

    val limitFarmOccupied: Int
){
    fun getActivePrice(land: Lands): ActivePrice {
        return activePrice.filter {
            land.landMap.size >= it.chunk
        }.maxOrNull()?: activePrice.maxOf { it }
    }

    data class ActivePrice(
        val chunk: Int,
        val price: Double
    ) : Comparable<ActivePrice> {
        override fun compareTo(other: ActivePrice): Int {
            return this.price.compareTo(other.price)
        }
    }
}