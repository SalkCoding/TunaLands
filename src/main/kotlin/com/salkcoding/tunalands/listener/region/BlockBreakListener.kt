package com.salkcoding.tunalands.listener.region

import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.data.lands.Rank
import com.salkcoding.tunalands.util.errorFormat
import com.salkcoding.tunalands.util.sendErrorTipMessage
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

class BlockBreakListener : Listener {

    @EventHandler(priority = EventPriority.HIGH)
    fun onProtect(event: BlockBreakEvent) {
        if (event.isCancelled) return
        if (event.player.isOp) return

        val player = event.player
        val chunk = event.block.chunk
        val block = event.block
        val lands = landManager.getLandsWithChunk(chunk)
        if (lands == null) {
            player.sendErrorTipMessage("${ChatColor.RED}중립 지역에서는 블럭을 파괴할 수 없습니다!")
            event.isCancelled = true
            return
        }

        if (!landManager.isProtectedLand(chunk)) return

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
            player.sendErrorTipMessage("${ChatColor.RED}권한이 없습니다!")
    }
}