package com.salkcoding.tunalands.bungee

import com.google.common.io.ByteStreams
import com.salkcoding.tunalands.tunaLands
import io.github.leonardosnt.bungeechannelapi.BungeeChannelApi
import org.bukkit.entity.Player
import java.util.*

class ReloadListener : BungeeChannelApi.ForwardConsumer {

    override fun accept(channel: String, player: Player, data: ByteArray) {
        val inMessage = ByteStreams.newDataInput(data)
        val size = inMessage.readInt()
        for (i in 1..size) {
            proxyPlayerSet.add(UUID.fromString(inMessage.readUTF()))
        }
        tunaLands.logger.info("UUID $size were accepted")
    }
}