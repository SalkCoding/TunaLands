package com.salkcoding.tunalands.commands.sub

import com.salkcoding.tunalands.configuration
import com.salkcoding.tunalands.gui.render.openVisitGui
import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.Lands
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.util.TeleportCooltime
import com.salkcoding.tunalands.util.errorFormat
import com.salkcoding.tunalands.util.infoFormat
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Visit : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val player = sender as? Player
        if (player != null) {
            when (args.size) {
                1 -> {
                    val ownerName = args[0]
                    val ownerUUID = Bukkit.getPlayerUniqueId(ownerName)
                    if (ownerUUID == null) {
                        player.sendMessage("${ownerName}이라는 유저가 존재하지 않습니다.".errorFormat())
                        return true
                    }

                    val lands = landManager.getPlayerLands(ownerUUID, Rank.OWNER)
                    if (lands == null) {
                        player.sendMessage("${ownerName}이라는 유저는 땅을 소유하고 있지 않습니다.".errorFormat())
                        return true
                    }

                    if (!lands.enable) {
                        player.sendMessage("비활성화된 땅은 방문할 수 없습니다.".errorFormat())
                        return true
                    }

                    if (!lands.open) {
                        player.sendMessage("비공개 설정된 땅은 방문할 수 없습니다.".errorFormat())
                        return true
                    }

                    val uuid = player.uniqueId
                    if (uuid in lands.memberMap) {
                        val rank = lands.memberMap[uuid]!!.rank
                        if (rank != Rank.PARTTIMEJOB && rank != Rank.VISITOR) {
                            player.sendMessage("자신이 소속되어있는 땅에는 방문할 수 없습니다!".errorFormat())
                            return true
                        }
                    }

                    if (uuid in lands.memberMap) {
                        val rank = lands.memberMap[uuid]!!.rank
                        if (rank == Rank.VISITOR) {
                            player.sendMessage("이미 방문 중입니다!".errorFormat())
                            return true
                        }
                    }

                    if (uuid in lands.banMap) {
                        player.sendMessage("${ChatColor.GREEN}${lands.ownerName}${ChatColor.WHITE}의 땅에서 밴되었기 때문에 방문할 수 없습니다!".errorFormat())
                        return true
                    }

                    TeleportCooltime.addPlayer(
                        player,
                        lands.visitorSpawn,
                        configuration.commandCooldown.visitCooldown,
                        {
                            //Remove visitor data
                            val previousLands = landManager.getPlayerLands(uuid, Rank.VISITOR)
                            if (previousLands != null) {
                                previousLands.memberMap.remove(uuid)
                                player.sendMessage("${previousLands.ownerName}의 땅을 떠났습니다.".infoFormat())
                            }

                            if (uuid in lands.memberMap) {
                                if (lands.memberMap[uuid]!!.rank == Rank.PARTTIMEJOB)
                                    return@addPlayer
                            }

                            lands.landHistory.visitorCount += 1
                            val current = System.currentTimeMillis()
                            lands.memberMap[uuid] = Lands.MemberData(
                                uuid,
                                Rank.VISITOR,
                                current,
                                current
                            )

                            lands.welcomeMessage.forEach { welcomeMessage ->
                                player.sendMessage(welcomeMessage)
                            }

                            lands.sendMessageToOnlineMembers("${ChatColor.GREEN}${player.name}${ChatColor.WHITE}님이 땅에 방문했습니다.".infoFormat())
                        },
                        false
                    )
                }

                else -> player.openVisitGui()
            }
        } else sender.sendMessage("콘솔에서는 사용할 수 없는 명령어입니다.".errorFormat())
        return true
    }
}