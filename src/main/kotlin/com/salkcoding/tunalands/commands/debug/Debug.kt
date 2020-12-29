package com.salkcoding.tunalands.commands.debug

import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.util.errorFormat
import com.salkcoding.tunalands.util.infoFormat
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Debug : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!sender.isOp || args.isEmpty())
            return false

        when {
            args[0] == "info" && args.size == 2 -> {
                val name = args[1]
                val list = landManager.getPlayerLandList(Bukkit.getOfflinePlayer(name).uniqueId)
                if (list != null) sender.sendMessage("$name 소유의 땅 목록: ${list.joinToString(separator = ", ")}".infoFormat())
                else sender.sendMessage("")
                return true
            }
            args[0] == "buy" && args.size == 1 -> {
                val player = sender as? Player
                if (player != null) {
                    landManager.buyLand(player, player.location.block)
                } else {
                    sender.sendMessage("콘솔에서는 사용 불가능한 명령어입니다.".errorFormat())
                }
                return true
            }
            args[0] == "sell" && args.size == 1 -> {
                val player = sender as? Player
                if (player != null) {
                    landManager.sellLand(player, player.chunk)
                } else {
                    sender.sendMessage("콘솔에서는 사용 불가능한 명령어입니다.".errorFormat())
                }
                return true
            }
            else -> return false
        }
    }
}