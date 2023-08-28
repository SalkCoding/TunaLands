package com.salkcoding.tunalands.listener

import com.salkcoding.tunalands.*
import com.salkcoding.tunalands.api.event.LandCreateEvent
import com.salkcoding.tunalands.lands.LandType
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.util.*
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPistonExtendEvent
import org.bukkit.event.block.BlockPistonRetractEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent

class CoreListener : Listener {

    @EventHandler(priority = EventPriority.LOW)
    fun onCorePlace(event: BlockPlaceEvent) {
        val placedBlock = event.block
        if (placedBlock.world.name in configuration.ignoreWorld) return

        val player = event.player
        if (placedBlock.type != configuration.protect.coreBlockType || !player.isSneaking) return

        event.isCancelled = true
        if (configuration.limitWorld.contains(placedBlock.world.name)) {
            player.sendErrorTipMessage("${ChatColor.RED}해당 월드에서는 코어를 만들 수 없습니다!")
            return
        }

        //Core block replace
        var lands = landManager.getPlayerLands(player.uniqueId)
        if (lands != null) {
            //Must be an owner
            val data = lands.memberMap[player.uniqueId]!!
            when (data.rank) {
                Rank.DELEGATOR, Rank.MEMBER -> {
                    player.sendErrorTipMessage("${ChatColor.RED}현재 소속된 땅을 탈퇴 해야만, 땅을 만들 수 있습니다.")
                    return
                }

                Rank.PARTTIMEJOB -> {
                    player.sendErrorTipMessage("${ChatColor.RED}알바를 그만둬야만, 땅을 만들 수 있습니다.")
                    return
                }

                Rank.VISITOR -> {
                    player.sendErrorTipMessage("${ChatColor.RED}현재 방문중인 땅을 나가야 땅을 만들 수 있습니다.")
                    return
                }

                else -> {
                    /*
                    Ignored
                    Owner = Continue core replacing
                    */
                }
            }

            event.isCancelled = false

            val query = event.block.chunk.toQuery()
            if (query !in lands.landMap || lands.landMap[query] == LandType.FARM) {
                player.sendErrorTipMessage("${ChatColor.RED}자신의 일반 땅에만 코어 블럭을 재배치 할 수 있습니다.")
                return
            }

            val price = configuration.protect.replaceCoreBlockPrice.toDouble()
            if (player.hasNotEnoughMoney(price)) {
                val delta = price - economy.getBalance(player)
                player.sendErrorTipMessage("${ChatColor.RED}${"%.2f".format(delta)}캔이 부족합니다.")
                return
            }

            displayManager.removeDisplay(lands)

            economy.withdrawPlayer(player, price)

            //Remove previous core blocks
            lands.removeCoreBlock(false)

            //Data replace
            lands.downCoreLocation = event.block.location
            lands.upCoreLocation = lands.downCoreLocation.clone().add(0.0, 1.0, 0.0)
            lands.upCoreLocation.block.type = Material.CHEST

            displayManager.createDisplay(lands)

            player.sendMessage("코어 블럭이 해당 위치로 이전되었습니다.".infoFormat())
            return
        }

        if (landManager.isProtectedLand(placedBlock.chunk)) {
            player.sendErrorTipMessage("${ChatColor.RED}다른 사람의 땅에는 코어를 만들 수 없습니다!")
            return
        }

        val price = configuration.protect.createPrice.toDouble()
        if (player.hasNotEnoughMoney(price)) {
            val delta = price - economy.getBalance(player)
            player.sendErrorTipMessage("${ChatColor.RED}${"%.2f".format(delta)}캔이 부족합니다.")
            return
        }
        economy.withdrawPlayer(player, price)

        val chest = placedBlock.getRelative(0, 1, 0)
        chest.type = Material.CHEST

        lands = landManager.createLand(player, chest, placedBlock)
        player.sendMessage("해당 위치의 땅을 구매했습니다.".infoFormat())
        player.sendMessage("코어 블럭 위치를 옮기려면, 앉은 상태에서 코어 블럭을 일반 땅에 설치하면 옮길 수 있습니다. (소유자 전용)".infoFormat())
        player.sendMessage("코어 이전 비용: ${configuration.protect.replaceCoreBlockPrice}캔".infoFormat())
        Bukkit.getPluginManager().callEvent(
            LandCreateEvent(
                lands,
                player,
                placedBlock.location
            )
        )
        event.isCancelled = false
    }

    @EventHandler(priority = EventPriority.LOW)
    fun onCoreBreak(event: BlockBreakEvent) {
        if (event.isCancelled) return

        val chunk = event.block.chunk
        val lands = landManager.getLandsWithChunk(chunk) ?: return

        val player = event.player
        if (!landManager.isProtectedLand(chunk)) return
        //Core protection
        val block = event.block
        if (block.type != Material.CHEST && block.type != configuration.protect.coreBlockType) return

        val upCoreLocation = lands.upCoreLocation
        val downCoreLocation = lands.downCoreLocation

        if (block.location == upCoreLocation || block.location == downCoreLocation) {
            player.sendErrorTipMessage("${ChatColor.RED}코어 블럭과 코어 창고는 부술 수 없습니다.")
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onCoreExpanded(event: BlockPistonExtendEvent) {
        val lands = landManager.getLandsWithChunk(event.block.chunk) ?: return
        event.blocks.forEach { block ->
            if (lands.downCoreLocation == block.location || lands.upCoreLocation == block.location) {
                event.isCancelled = true
                return
            }
        }
    }

    @EventHandler
    fun onCoreRetract(event: BlockPistonRetractEvent) {
        val lands = landManager.getLandsWithChunk(event.block.chunk) ?: return
        event.blocks.forEach { block ->
            if (lands.downCoreLocation == block.location || lands.upCoreLocation == block.location) {
                event.isCancelled = true
                return
            }
        }
    }

    @EventHandler
    fun onCoreInteract(event: PlayerInteractEvent) {
        if (event.isCancelled) return

        val clickedBlock = event.clickedBlock ?: return
        if (clickedBlock.type != configuration.protect.coreBlockType) return

        val lands = landManager.getLandsWithChunk(clickedBlock.chunk) ?: return
        val clickedLocation = clickedBlock.location
        if (lands.downCoreLocation == clickedLocation) {
            event.player.sendErrorTipMessage("${ChatColor.RED}코어 블럭과는 상호 작용할 수 없습니다.")
            event.isCancelled = true
        }
    }
}