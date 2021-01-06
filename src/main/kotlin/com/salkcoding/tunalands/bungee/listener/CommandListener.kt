package com.salkcoding.tunalands.bungee.listener

import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener

class CommandListener : PluginMessageListener {

    //Receive Plugin message from other server
    override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray) {
        TODO("Not yet implemented")
    }
}