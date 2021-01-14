package com.salkcoding.tunalands.bungee;

import com.salkcoding.tunalands.tunaLands
import com.salkcoding.tunalands.util.toDataInputStream
import io.github.leonardosnt.bungeechannelapi.BungeeChannelApi
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

val proxyPlayerSet = mutableSetOf<UUID>()

class PlayerListListener : BungeeChannelApi.ForwardConsumer {

    override fun accept(channel: String, player: Player, data: ByteArray) {
        val result = data.toDataInputStream()
        val uuid = UUID.fromString(result.readUTF())
        when (channel) {//SubChannel
            "tunalands-playerjoin" -> proxyPlayerSet.add(uuid)
            "tunalands-playerquit" -> {
                Bukkit.getScheduler().runTaskLater(tunaLands, Runnable {
                    if (Bukkit.getPlayer(uuid) == null)
                        proxyPlayerSet.remove(uuid)
                }, 5)
            }
        }
    }
}
