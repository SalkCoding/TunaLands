package com.salkcoding.tunalands.bungee

import com.google.common.io.ByteStreams
import com.salkcoding.tunalands.channelName
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener
import java.io.ByteArrayInputStream
import java.io.DataInputStream

class IncomingListener : PluginMessageListener {

    override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray) {
        if (channel != channelName) return

        val inByteArray = ByteStreams.newDataInput(message)
        //Sub channel
        when (inByteArray.readUTF()) {
            "Teleport" -> {
                val length: Short = inByteArray.readShort()
                val messageBytes = ByteArray(length.toInt())
                inByteArray.readFully(messageBytes)

                val messageIn = DataInputStream(ByteArrayInputStream(messageBytes))

                val world = messageIn.readUTF()
                val x = messageIn.readDouble()
                val y = messageIn.readDouble()
                val z = messageIn.readDouble()
                val location = Location(Bukkit.getWorld(world), x, y, z)
                player.teleportAsync(location)
            }
        }
    }
}