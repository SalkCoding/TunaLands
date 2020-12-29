package com.salkcoding.tunalands.commands.sub

import com.salkcoding.tunalands.gui.render.openBanListGui
import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.util.errorFormat
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class BanList : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        when {
            label == "banlist" && args.isEmpty() -> {
                val player = sender as? Player
                if (player != null) {
                    val uuid = player.uniqueId
                    val lands = landManager.getPlayerLands(uuid)
                    if (lands != null) {
                        val data = lands.memberMap[uuid]!!
                        when (data.rank) {
                            Rank.OWNER, Rank.DELEGATOR, Rank.MEMBER -> player.openBanListGui(true, data.rank)
                            else -> player.sendMessage("권한이 없습니다.".errorFormat())
                        }
                    } else player.sendMessage("해당 명령어는 땅 소유자와 관리 대리인만 사용가능합니다.".errorFormat())
                } else sender.sendMessage("콘솔에서는 사용할 수 없는 명령어입니다.".errorFormat())
                return true
            }
        }
        return false
    }
}