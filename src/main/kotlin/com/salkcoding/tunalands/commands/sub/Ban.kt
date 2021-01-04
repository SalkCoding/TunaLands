package com.salkcoding.tunalands.commands.sub

import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.Lands
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.lands.recordLeft
import com.salkcoding.tunalands.util.errorFormat
import com.salkcoding.tunalands.util.infoFormat
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Ban : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        when {
            label == "ban" && args.size == 1 -> {
                val player = sender as? Player
                if (player != null) {
                    val lands = landManager.getPlayerLands(player.uniqueId, Rank.OWNER, Rank.DELEGATOR)
                    if (lands != null) {
                        val data = lands.memberMap[player.uniqueId]!!
                        if(data.rank == Rank.DELEGATOR && !lands.delegatorSetting.canBan){
                            player.sendMessage("권한이 없습니다!".errorFormat())
                            return true
                        }

                        val target = Bukkit.getPlayer(args[0])
                        if (target == null) {
                            player.sendMessage("존재하지 않는 유저입니다!".errorFormat())
                            return true
                        }

                        if (target.uniqueId == player.uniqueId) {
                            player.sendMessage("자신을 밴할 수는 없습니다.".errorFormat())
                            return true
                        }

                        val targetData = lands.memberMap[target.uniqueId]
                        //Try to ban member
                        if (targetData != null) {
                            if (targetData.rank == Rank.OWNER) {
                                player.sendMessage("소유자는 밴할 수 없습니다.".errorFormat())
                                return true
                            }
                            lands.memberMap.remove(target.uniqueId)
                        }
                        target.recordLeft()

                        //Else ban another player
                        player.sendMessage("${target.name}을/를 밴하였습니다.".infoFormat())
                        target.sendMessage("${lands.ownerName}의 땅에서 ${player.name}에 의해 밴당하셨습니다.".infoFormat())

                        lands.banMap[target.uniqueId] =
                            Lands.BanData(target.uniqueId, System.currentTimeMillis())
                    } else player.sendMessage("해당 명령어는 땅 소유자와 관리 대리인만 사용가능합니다.".errorFormat())
                } else sender.sendMessage("콘솔에서는 사용할 수 없는 명령어입니다.".errorFormat())
                return true
            }
        }
        return false
    }
}