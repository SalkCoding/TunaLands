package com.salkcoding.tunalands.listener

import com.salkcoding.tunalands.gui.render.settinggui.loreChatMap
import com.salkcoding.tunalands.gui.render.settinggui.welcomeMessageChatSet
import com.salkcoding.tunalands.landManager
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

class ChatListener : Listener {

    @EventHandler
    fun onChat(event: AsyncPlayerChatEvent) {
        if (event.isCancelled) return

        val player = event.player
        val uuid = player.uniqueId
        if (uuid in loreChatMap) {
            val count = loreChatMap[uuid]!!
            val lands = landManager.getPlayerLands(uuid) ?: return
            val lore =
                when (event.message) {
                    "\\n" -> ""
                    else -> ChatColor.translateAlternateColorCodes('&', event.message)
                }
            lands.lore[count] = lore

            if (count + 1 >= 3)
                loreChatMap.remove(uuid)
            else {
                loreChatMap.replace(uuid, count + 1)
                player.sendMessage("설명 ${count + 1}번째 줄: $lore")
                event.isCancelled = true
            }
        } else if (uuid in welcomeMessageChatSet) {
            val lands = landManager.getPlayerLands(uuid) ?: return
            val lore =
                when (event.message) {
                    "\\n" -> ""
                    else -> ChatColor.translateAlternateColorCodes('&', event.message)
                }

            lands.welcomeMessage = lore
            welcomeMessageChatSet.remove(uuid)
        }
    }
}