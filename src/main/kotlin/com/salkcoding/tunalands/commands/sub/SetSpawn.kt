package com.salkcoding.tunalands.commands.sub

import com.salkcoding.tunalands.configuration
import com.salkcoding.tunalands.economy
import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.util.errorFormat
import com.salkcoding.tunalands.util.hasEnoughMoney
import com.salkcoding.tunalands.util.infoFormat
import com.salkcoding.tunalands.util.toQuery
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SetSpawn : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        when {
            label == "setspawn" && args.size == 1 -> {
                val player = sender as? Player
                if (player != null) {
                    val lands = landManager.getPlayerLands(player.uniqueId, Rank.OWNER, Rank.DELEGATOR)
                    if (lands != null) {
                        if (player.location.chunk.toQuery() !in lands.landList) {
                            player.sendMessage("자신의 보호된 땅 이외의 위치에서는 스폰을 설정할 수 없습니다!".errorFormat())
                            return true
                        }

                        val price = configuration.command.setSpawnPrice.toDouble()
                        if (player.hasEnoughMoney(price)) {
                            player.sendMessage("캔이 부족합니다.".errorFormat())
                            return true
                        }
                        economy.withdrawPlayer(player, price)

                        val data = lands.memberMap[player.uniqueId]!!
                        when (data.rank) {
                            Rank.OWNER -> {
                                when (args[0]) {
                                    "main" -> lands.memberSpawn = player.location
                                    "visitor" -> lands.visitorSpawn = player.location
                                    else -> return false
                                }
                                player.sendMessage("해당 위치를 스폰으로 설정하였습니다.".infoFormat())
                            }
                            Rank.DELEGATOR -> {
                                if (!lands.delegatorSetting.canSetSpawn) {
                                    player.sendMessage("권한이 없습니다!".errorFormat())
                                    return true
                                }

                                when (args[0]) {
                                    "main" -> lands.memberSpawn = player.location
                                    "visitor" -> lands.visitorSpawn = player.location
                                    else -> return false
                                }

                                player.sendMessage("해당 위치를 스폰으로 설정하였습니다.".infoFormat())
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