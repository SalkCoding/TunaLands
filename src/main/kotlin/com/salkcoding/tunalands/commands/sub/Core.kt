package com.salkcoding.tunalands.commands.sub

import com.salkcoding.tunalands.gui.render.openMainGui
import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.util.errorFormat
import com.salkcoding.tunalands.util.sendErrorTipMessage
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Core : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val player = sender as? Player
        if (player == null) {
            sender.sendMessage("콘솔에서는 사용할 수 없는 명령어입니다.".errorFormat())
            return true
        }
        val uuid = player.uniqueId
        val lands = landManager.getPlayerLands(uuid, Rank.OWNER, Rank.DELEGATOR, Rank.MEMBER)
        if (lands == null) {
            player.sendMessage("해당 명령어는 땅의 소유자, 관리 대리인, 멤버만 사용가능합니다.".errorFormat())
            return true
        }
        when {
            args.isEmpty() -> {
                when (val rank = lands.memberMap[player.uniqueId]!!.rank) {
                    Rank.OWNER, Rank.DELEGATOR, Rank.MEMBER -> player.openMainGui(
                        lands,
                        rank
                    )

                    else -> player.sendErrorTipMessage("${ChatColor.RED}권한이 없습니다!")
                }
            }
/*
            args.size == 1 && args[0] == "spawn" -> {
                val spawn = lands.upCoreLocation
                TeleportCooltime.addPlayer(player, spawn, configuration.commandCooldown.spawnCooldown, {
                    //Remove visitor data
                    val previousLands = landManager.getPlayerLands(uuid, Rank.VISITOR)
                    if (previousLands != null) {
                        previousLands.memberMap.remove(uuid)
                        player.sendMessage("${previousLands.landsName}을/를 떠났습니다.".infoFormat())
                    }
                }, false)
            }
*/
            else -> return false
        }
        return true
    }
}