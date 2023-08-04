package com.salkcoding.tunalands.config.section

import com.salkcoding.tunalands.lands.Lands
import com.salkcoding.tunalands.lands.Rank
import org.bukkit.Material

data class Protect(
    val coreBlockType:Material,
    val createPrice: Int,
    val limitOccupied: List<LimitOccupied>
){
    fun getMaxOccupied(land: Lands): LimitOccupied {
        return limitOccupied.filter {
            land.memberMap.filter { (_, it) ->
                it.rank != Rank.VISITOR && it.rank != Rank.PARTTIMEJOB
            }.size <= it.numOfMembers
        }.minOrNull() ?: limitOccupied.maxOf { it }
    }

    data class LimitOccupied(
        val numOfMembers: Int,
        val maxChunkAmount: Int
    ) : Comparable<LimitOccupied> {
        override fun compareTo(other: LimitOccupied): Int {
            return this.maxChunkAmount.compareTo(other.maxChunkAmount)
        }
    }
}