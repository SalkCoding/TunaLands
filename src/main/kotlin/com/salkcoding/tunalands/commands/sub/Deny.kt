package com.salkcoding.tunalands.commands.sub

import com.salkcoding.tunalands.util.errorFormat
import com.salkcoding.tunalands.util.infoFormat
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Deny : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        when {
            label == "deny" && args.isEmpty() -> {
                val player = sender as? Player
                if (player != null) {
                    val uuid = player.uniqueId
                    if (inviteMap.containsKey(uuid)) {
                        val data = inviteMap[uuid]!!
                        val host = data.host

                        player.sendMessage("초대를 거절했습니다.".infoFormat())
                        if (host.isOnline)
                            host.sendMessage("${player.name}이/가 당신의 초대를 거절하였습니다.".infoFormat())

                        data.task.cancel()
                        inviteMap.remove(uuid)
                    } else player.sendMessage("받은 초대가 없습니다.".errorFormat())
                } else sender.sendMessage("콘솔에서는 사용할 수 없는 명령어입니다.".errorFormat())
                return true
            }
        }
        return false
    }
}