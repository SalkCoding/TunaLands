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
import org.bukkit.event.block.BlockPlaceEvent


class BlockPlaceListener : Listener {

    @EventHandler(priority = EventPriority.HIGH)
    fun onProtect(event: BlockPlaceEvent) {
        if (event.isCancelled) return
        if (event.player.isOp) return

        val player = event.player
        val chunk = event.block.chunk
        val lands = landManager.getLandsWithChunk(chunk)
        if (lands == null) {
            player.sendErrorTipMessage("${ChatColor.RED}중립 지역에서는 블럭을 설치할 수 없습니다!")
            event.isCancelled = true
            return
        }

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
            player.sendErrorTipMessage("${ChatColor.RED}권한이 없습니다!")
    }
}