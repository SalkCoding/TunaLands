package com.salkcoding.tunalands.listener

import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.leftManager
import com.salkcoding.tunalands.util.*
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.util.Vector
import java.util.*

class InOutListener : Listener {

    private val enterSet = mutableMapOf<UUID, Int>()

    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        if (event.isCancelled) return

        val player = event.player
        val uuid = player.uniqueId
        val lands = landManager.getLandsWithChunk(player.chunk)
        if (lands != null) {
            if (player.uniqueId in lands.banMap) {
                player.sendErrorTipMessage("${ChatColor.RED}밴 당한 지역에는 접근하실 수 없습니다!")
                val newLocation = player.location.add(player.eyeLocation.direction.multiply(Vector(-2, 0, -2)))
                player.teleportAsync(newLocation)
                return
            }

            if (uuid !in enterSet) {
                enterSet[uuid] = lands.hashCode()
                player.sendTitle("", lands.landsName, 10, 20, 10)
            } else {
                val old = enterSet[uuid]
                val present = lands.hashCode()
                if (old != present) {
                    enterSet[uuid] = present
                    player.sendTitle("", lands.landsName, 10, 20, 10)
                }
            }
        } else {
            if (uuid in enterSet) {
                enterSet.remove(uuid)
                player.sendTitle("", "${ChatColor.WHITE}비점유 지역", 10, 20, 10)

                val playerUUID = player.uniqueId
                val visitLands = landManager.getPlayerLands(playerUUID, Rank.VISITOR) ?: return
                val rank = visitLands.memberMap[playerUUID]!!.rank

                visitLands.memberMap.remove(playerUUID)

                player.sendMessage("${visitLands.landsName}을/를 떠났습니다.".infoFormat())
                leftManager.recordLeft(playerUUID)

                visitLands.sendMessageToOnlineMembers("${ChatColor.GRAY}[${rank.toColoredText()}${ChatColor.GRAY}] ${ChatColor.GREEN}${player.name}${ChatColor.WHITE}이/가 땅을 떠났습니다.".warnFormat())
            }
        }
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player
        enterSet.remove(player.uniqueId)
        val landsList = landManager.getPlayerLandsList(player.uniqueId)
        landsList.forEach { lands ->
            lands.memberMap[player.uniqueId]!!.lastLogin = System.currentTimeMillis()
        }
    }
}