package com.salkcoding.tunalands.listener

import com.salkcoding.tunalands.gui.render.settinggui.loreChatMap
import com.salkcoding.tunalands.gui.render.settinggui.welcomeMessageChatMap
import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.data.lands.Rank
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

class LoreChatListener : Listener {

    @EventHandler
    fun onChat(event: AsyncPlayerChatEvent) {
        if (event.isCancelled) return

        val player = event.player
        val uuid = player.uniqueId
        if (uuid in loreChatMap) {
            val count = loreChatMap[uuid]!!
            if (count >= 3) {
                loreChatMap.remove(uuid)
                return
            }
            val lands = landManager.getPlayerLands(uuid, Rank.OWNER, Rank.DELEGATOR)
            if (lands == null) {
                loreChatMap.remove(player.uniqueId)
                return
            }
            val lore =
                when (event.message) {
                    "\\n" -> ""
                    else -> ChatColor.translateAlternateColorCodes('&', event.message)
                }
            lands.lore[count] = lore


            loreChatMap.replace(uuid, count + 1)
            player.sendMessage("설명 ${count + 1}번째 줄: $lore")
            event.isCancelled = true
        } else if (uuid in welcomeMessageChatMap) {
            val count = welcomeMessageChatMap[uuid]!!
            if (count >= 3) {
                welcomeMessageChatMap.remove(uuid)
                return
            }
            val lands = landManager.getPlayerLands(uuid, Rank.OWNER, Rank.DELEGATOR)
            if (lands == null) {
                welcomeMessageChatMap.remove(player.uniqueId)
                return
            }
            val lore =
                when (event.message) {
                    "\\n" -> ""
                    else -> ChatColor.translateAlternateColorCodes('&', event.message)
                }
            lands.welcomeMessage[count] = lore

            welcomeMessageChatMap.replace(uuid, count + 1)
            player.sendMessage("설명 ${count + 1}번째 줄: $lore")
            event.isCancelled = true
        }
    }
}