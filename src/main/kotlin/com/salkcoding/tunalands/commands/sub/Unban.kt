package com.salkcoding.tunalands.commands.sub

import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.util.errorFormat
import com.salkcoding.tunalands.util.infoFormat
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Unban : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        when {
            label == "unban" && args.size == 1 -> {
                val player = sender as? Player
                if (player != null) {
                    val lands = landManager.getPlayerLands(player.uniqueId)
                    if (lands != null) {
                        val data = lands.memberMap[player.uniqueId]!!
                        when (data.rank) {
                            Rank.OWNER, Rank.DELEGATOR -> {
                                val target = Bukkit.getPlayer(args[0])
                                if (target == null) {
                                    player.sendMessage("존재하지 않는 유저입니다!".errorFormat())
                                    return true
                                }

                                player.sendMessage("${target.name}의 밴을 해제했습니다.".infoFormat())
                                target.sendMessage("${player.name}의 땅의 밴이 해제되었습니다.".infoFormat())

                                lands.banMap.remove(target.uniqueId)
                            }
                            else -> player.sendMessage("권한이 없습니다!".errorFormat())
                        }
                    } else player.sendMessage("해당 명령어는 땅 소유자와 관리 대리인만 사용가능합니다.".errorFormat())
                } else sender.sendMessage("콘솔에서는 사용할 수 없는 명령어입니다.".errorFormat())
                return true
            }
        }
        return false
    }
}