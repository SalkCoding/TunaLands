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

class Hego : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        when {
            label == "hego" && args.size == 1 -> {
                val player = sender as? Player
                if (player != null) {
                    val lands = landManager.getPlayerLands(player.uniqueId)
                    if (lands != null) {
                        val data = lands.memberMap[player.uniqueId]!!
                        when (data.rank) {
                            Rank.OWNER, Rank.DELEGATOR -> {
                                val target = Bukkit.getPlayer(args[0])
                                if (target == null) {
                                    player.sendMessage("존재하지 않는 유저입니다.".errorFormat())
                                    return true
                                }

                                val targetData = lands.memberMap[target.uniqueId]
                                if (targetData != null) {
                                    if (targetData.rank == Rank.PARTTIMEJOB) {
                                        lands.memberMap.remove(target.uniqueId)

                                        player.sendMessage("${target.name}을/를 해고했습니다.".infoFormat())
                                        target.sendMessage("${player.name}이/가 당신을 ${lands.ownerName}의 땅에서 해고 했습니다.".infoFormat())
                                        return true
                                    }else player.sendMessage("${target.name}은/는 알바가 아닙니다.".errorFormat())
                                } else player.sendMessage("${target.name}은/는 당신의 땅의 소속되어있지 않습니다.".errorFormat())
                            }
                            else -> {
                                player.sendMessage("권한이 없습니다.".errorFormat())
                                return true
                            }
                        }
                    } else player.sendMessage("해당 명령어는 땅 소유자와 관리 대리인만 사용가능합니다.".errorFormat())
                } else sender.sendMessage("콘솔에서는 사용할 수 없는 명령어입니다.".errorFormat())
                return true
            }
        }
        return false
    }
}