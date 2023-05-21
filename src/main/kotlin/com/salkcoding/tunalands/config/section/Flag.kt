package com.salkcoding.tunalands.config.section

data class Flag(
    val takeProtectFlagPrice: Double,
    val releaseProtectFlagPrice: Double,

    val takeFarmFlagPrice: Double,
    val releaseFarmFlagPrice: Double,

    val limitFarmOccupied: Int
)