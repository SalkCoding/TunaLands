package com.salkcoding.tunalands.listener.region

import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.util.errorFormat
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

class BlockBreakListener : Listener {

    @EventHandler(priority = EventPriority.HIGH)
    fun onProtect(event: BlockBreakEvent) {
        if (event.isCancelled) return

        val chunk = event.block.chunk
        val lands = landManager.getLandsWithChunk(chunk) ?: return
        if (!lands.enable) return

        val player = event.player
        if (!landManager.isProtectedLand(chunk)) return

        val block = event.block
        if (player.uniqueId in lands.memberMap) {
            val setting = when (lands.memberMap[player.uniqueId]!!.rank) {
                Rank.OWNER, Rank.DELEGATOR -> return
                Rank.MEMBER -> lands.memberSetting
                Rank.PARTTIMEJOB -> lands.partTimeJobSetting
                Rank.VISITOR -> lands.visitorSetting
            }

            when (block.type) {
                Material.WHEAT,
                Material.POTATOES,
                Material.CARROTS,
                Material.BEETROOTS,
                Material.NETHER_WART,
                Material.COCOA,
                Material.MELON,
                Material.MELON_STEM,
                Material.PUMPKIN,
                Material.PUMPKIN_STEM,
                Material.CACTUS,
                Material.SUGAR_CANE,
                Material.CHORUS_PLANT,
                Material.CHORUS_FLOWER,
                Material.BAMBOO,
                Material.BAMBOO_SAPLING,
                Material.KELP,
                Material.KELP_PLANT
                -> {
                    if (!setting.canHarvest)
                        event.isCancelled = true
                }
                Material.ITEM_FRAME -> {
                    if (!setting.breakItemFrame)
                        event.isCancelled = true
                }
                else -> {
                    if (!setting.breakBlock)
                        event.isCancelled = true
                }
            }
        } else event.isCancelled = true

        if (event.isCancelled)
            player.sendMessage("권한이 없습니다.".errorFormat())
    }
}