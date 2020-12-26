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

class ThrowListener : Listener {

    @EventHandler
    fun onThrow(event: PlayerInteractEvent) {
        if (event.useItemInHand() == Event.Result.DENY) return
        if (event.action != Action.RIGHT_CLICK_BLOCK && event.action != Action.RIGHT_CLICK_AIR) return

        val player = event.player
        val lands = landManager.getLandsWithChunk(player.chunk) ?: return
        if (!lands.enable) return

        val setting = when (lands.memberMap[player.uniqueId]!!.rank) {
            Rank.MEMBER -> lands.memberSetting
            Rank.PARTTIMEJOB -> lands.partTimeJobSetting
            Rank.VISITOR -> lands.visitorSetting
            else -> null
        } ?: return

        val mainType = player.inventory.itemInMainHand.type
        val offType = player.inventory.itemInOffHand.type
        when {
            mainType == Material.EGG || offType == Material.EGG -> {
                if (setting.throwEgg)
                    event.isCancelled = true
            }
            mainType == Material.FISHING_ROD || offType == Material.FISHING_ROD -> {
                if (setting.canFishing)
                    event.isCancelled = true
            }
        }

        if (event.useItemInHand() == Event.Result.DENY)
            player.sendMessage("You don't have a permission!".errorFormat())
    }
}