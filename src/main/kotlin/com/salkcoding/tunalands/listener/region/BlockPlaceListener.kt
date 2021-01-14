package com.salkcoding.tunalands.listener.region

import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.util.errorFormat
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent


class BlockPlaceListener : Listener {

    @EventHandler
    fun onProtect(event: BlockPlaceEvent) {
        if (event.isCancelled) return

        val chunk = event.block.chunk
        val lands = landManager.getLandsWithChunk(chunk) ?: return

        val player = event.player
        if (!landManager.isProtectedLand(chunk)) return

        val block = event.block
        //Block break following rank
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
                Material.MELON_STEM,
                Material.PUMPKIN_STEM,
                Material.CACTUS,
                Material.SUGAR_CANE,
                Material.CHORUS_FLOWER,
                Material.BAMBOO_SAPLING,
                Material.KELP -> {
                    if (!setting.canSow)
                        event.isCancelled = true
                }
                Material.FIRE -> {
                    if (!setting.useFlintAndSteel)
                        event.isCancelled = true
                }
                else -> {
                    if (!setting.placeBlock)
                        event.isCancelled = true
                }
            }
        } else event.isCancelled = true

        if (event.isCancelled)
            player.sendMessage("권한이 없습니다!".errorFormat())
    }
}