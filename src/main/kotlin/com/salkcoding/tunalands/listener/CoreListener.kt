package com.salkcoding.tunalands.listener

import com.salkcoding.tunalands.configuration
import com.salkcoding.tunalands.economy
import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.data.lands.Rank
import com.salkcoding.tunalands.util.*
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPistonExtendEvent
import org.bukkit.event.block.BlockPlaceEvent

class CoreListener : Listener {

    @EventHandler(priority = EventPriority.HIGH)
    fun onCorePlace(event: BlockPlaceEvent) {
        if (event.isCancelled) return

        val chest = event.block
        if (chest.type == Material.CHEST && event.player.isSneaking) {
            val coreBlock = chest.getRelative(0, -1, 0)
            if (coreBlock.type == configuration.protect.coreBlock) {
                val player = event.player

                if (landManager.getPlayerLands(player.uniqueId, Rank.OWNER, Rank.DELEGATOR, Rank.MEMBER) != null
                    || landManager.getLandsWithChunk(chest.chunk) != null
                    || coreBlock.world.name in configuration.limitWorld
                )
                    return

                val price = configuration.protect.createPrice.toDouble()
                if (player.hasEnoughMoney(price)) {
                    player.sendMessage("캔이 부족합니다.".errorFormat())
                    return
                }
                economy.withdrawPlayer(player, price)

                landManager.buyLand(player, chest, coreBlock)
            }
        }
    }

    @EventHandler
    fun onCoreBreak(event: BlockBreakEvent) {
        if (event.isCancelled) return

        val chunk = event.block.chunk
        val lands = landManager.getLandsWithChunk(chunk) ?: return

        val player = event.player
        if (landManager.isProtectedLand(chunk)) {
            //Core protection
            val block = event.block
            if (block.type == Material.CHEST || block.type == configuration.protect.coreBlock) {
                val upCore = lands.upCore
                val downCore = lands.downCore

                if (block.isSameLocation(upCore.world.name, upCore.blockX, upCore.blockY, upCore.blockZ) ||
                    block.isSameLocation(downCore.world.name, downCore.blockX, downCore.blockY, downCore.blockZ)
                ) {
                    player.sendMessage("코어 블럭과 코어 창고는 부술 수 없습니다.".warnFormat())
                    event.isCancelled = true
                }
            }
        }
    }

    @EventHandler
    fun onExpanded(event: BlockPistonExtendEvent) {
        val lands = landManager.getLandsWithChunk(event.block.chunk) ?: return
        event.blocks.forEach {
            if (lands.downCore == it || lands.upCore == it) {
                event.isCancelled = true
                return
            }
        }
    }
}