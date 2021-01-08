package com.salkcoding.tunalands.bungee;

import com.salkcoding.tunalands.channelName
import com.salkcoding.tunalands.util.toDataInputStream
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener
import java.util.*

val proxyPlayerSet = mutableSetOf<UUID>()

class PlayerListListener : PluginMessageListener {

    override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray) {
        if (channel != channelName) return

        val inMessage = message.toDataInputStream()
        val subChannel = inMessage.readUTF()
        val len = inMessage.readShort()
        val byteArray = ByteArray(len.toInt())
        inMessage.readFully(byteArray)
        val result = byteArray.toDataInputStream()
        val uuid = UUID.fromString(result.readUTF())
        when (subChannel) {//SubChannel
            "PlayerJoin" -> proxyPlayerSet.add(uuid)
            "PlayerQuit" -> {
                Bukkit.getPlayer(uuid) ?: return
                proxyPlayerSet.remove(uuid)
            }
        }
    }
}
