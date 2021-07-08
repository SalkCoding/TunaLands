package com.salkcoding.tunalands.commands.sub

import com.salkcoding.tunalands.bukkitLinkedAPI
import com.salkcoding.tunalands.util.errorFormat
import com.salkcoding.tunalands.util.infoFormat
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

class Deny : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (label == "deny" && args.isEmpty()) {
            val player = sender as? Player
            if (player != null) {
                work(player)
            } else sender.sendMessage("콘솔에서는 사용할 수 없는 명령어입니다.".errorFormat())
            return true
        }
        return false
    }

    companion object {

        fun work(uuid: UUID) {
            val offlinePlayer = Bukkit.getOfflinePlayer(uuid)
            work(offlinePlayer)
        }

        private fun work(offlinePlayer: OfflinePlayer) {
            val uuid = offlinePlayer.uniqueId
            if (offlinePlayer.isOnline) {
                val player = offlinePlayer.player!!
                if (inviteMap.containsKey(uuid)) {
                    val data = inviteMap[uuid]!!
                    val host = data.host

                    player.sendMessage("초대를 거절했습니다.".infoFormat())
                    if (host.isOnline)
                        host.player!!.sendMessage("${player.name}이/가 당신의 초대를 거절하였습니다.".infoFormat())
                    else
                        bukkitLinkedAPI.sendMessageAcrossServer(host.name, "${player.name}이/가 당신의 초대를 거절하였습니다.".infoFormat())

                    data.task.cancel()
                    inviteMap.remove(uuid)
                } else player.sendMessage("받은 초대가 없습니다.".errorFormat())
            } else {
                if (inviteMap.containsKey(uuid)) {
                    val data = inviteMap[uuid]!!
                    val host = data.host

                    bukkitLinkedAPI.sendMessageAcrossServer(offlinePlayer.name, "초대를 거절했습니다.".infoFormat())
                    if (host.isOnline)
                        host.player!!.sendMessage("${offlinePlayer.name}이/가 당신의 초대를 거절하였습니다.".infoFormat())
                    else
                        bukkitLinkedAPI.sendMessageAcrossServer(host.name, "${offlinePlayer.name}이/가 당신의 초대를 거절하였습니다.".infoFormat())

                    data.task.cancel()
                    inviteMap.remove(uuid)
                } else bukkitLinkedAPI.sendMessageAcrossServer(offlinePlayer.name, "받은 초대가 없습니다.".errorFormat())
            }
        }
    }
}