package com.salkcoding.tunalands.listener.region

import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.util.errorFormat
import org.bukkit.Material
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class PhysicalInteractListener : Listener {

    @EventHandler
    fun onPhysical(event: PlayerInteractEvent) {
        if (event.useInteractedBlock() == Event.Result.DENY) return
        if (event.action != Action.PHYSICAL) return
        val block = event.clickedBlock!!

        val lands = landManager.getLandsWithChunk(block.chunk) ?: return

        val player = event.player
        if (player.uniqueId in lands.memberMap) {
            val setting = when (lands.memberMap[player.uniqueId]!!.rank) {
                Rank.OWNER, Rank.DELEGATOR -> return
                Rank.MEMBER -> lands.memberSetting
                Rank.PARTTIMEJOB -> lands.partTimeJobSetting
                Rank.VISITOR -> lands.visitorSetting
            }

            when (block.type) {
                Material.STONE_PRESSURE_PLATE,
                Material.OAK_PRESSURE_PLATE,
                Material.SPRUCE_PRESSURE_PLATE,
                Material.BIRCH_PRESSURE_PLATE,
                Material.JUNGLE_PRESSURE_PLATE,
                Material.ACACIA_PRESSURE_PLATE,
                Material.DARK_OAK_PRESSURE_PLATE,
                Material.CRIMSON_PRESSURE_PLATE,
                Material.WARPED_PRESSURE_PLATE,
                Material.POLISHED_BLACKSTONE_PRESSURE_PLATE -> {
                    if (!setting.usePressureSensor)
                        event.isCancelled = true
                }
                Material.FARMLAND -> {
                    if (!setting.canRuinFarmland)
                        event.isCancelled = true
                }
                else -> return
            }
        } else event.isCancelled = true

        if (event.useInteractedBlock() == Event.Result.DENY)
            player.sendMessage("권한이 없습니다!".errorFormat())
    }
}