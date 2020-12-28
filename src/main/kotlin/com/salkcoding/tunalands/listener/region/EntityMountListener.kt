package com.salkcoding.tunalands.listener.region

import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.util.errorFormat
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.spigotmc.event.entity.EntityMountEvent

class EntityMountListener : Listener {

    @EventHandler
    fun onMount(event: EntityMountEvent) {
        if (event.isCancelled) return

        val lands = landManager.getLandsWithChunk(event.mount.chunk) ?: return
        if (!lands.enable) return

        val player = event.entity as? Player ?: return

        val setting = when (lands.memberMap[player.uniqueId]!!.rank) {
            Rank.MEMBER -> lands.memberSetting
            Rank.PARTTIMEJOB -> lands.partTimeJobSetting
            else -> lands.visitorSetting
        }

        when (event.mount.type) {
            EntityType.MINECART -> {
                if (setting.useMinecart)
                    event.isCancelled = true
            }
            EntityType.BOAT -> {
                if (setting.useBoat)
                    event.isCancelled = true
            }
            EntityType.PIG,
            EntityType.STRIDER,
            EntityType.DONKEY,
            EntityType.MULE,
            EntityType.HORSE -> {
                if (setting.canRiding)
                    event.isCancelled = true
            }
            else -> return
        }

        if (event.isCancelled)
            player.sendMessage("권한이 없습니다.".errorFormat())
    }
}