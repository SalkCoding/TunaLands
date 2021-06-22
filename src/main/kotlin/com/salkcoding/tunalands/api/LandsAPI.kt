package com.salkcoding.tunalands.api

import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.landManager
import org.bukkit.Bukkit
import java.util.*

object LandsAPI {

    fun getPlayerRank(uuid: UUID): Rank? {
        return when (val lands = landManager.getPlayerLands(uuid)) {
            null -> null
            else -> lands.memberMap[uuid]!!.rank
        }
    }

    fun getPlayerLandsMemberList(uuid: UUID): List<String>? {
        return when (val lands = landManager.getPlayerLands(uuid)) {
            null -> null
            else -> {
                val memberMap = lands.memberMap
                val list = mutableListOf<String>()
                memberMap.keys.forEach { member ->
                    Bukkit.getOfflinePlayer(member).name?.let { list.add(it) }
                }
                list
            }
        }
    }
}