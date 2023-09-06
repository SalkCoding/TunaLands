package com.salkcoding.tunalands.commands.sub

import com.salkcoding.tunalands.bukkitLinkedAPI
import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.util.errorFormat
import com.salkcoding.tunalands.util.infoFormat
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

class Demote : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) return false

        val player = sender as? Player
        if (player == null) {
            sender.sendMessage("콘솔에서는 사용할 수 없는 명령어입니다.".errorFormat())
            return true
        }

        work(player, args[0])
        return true
    }

    companion object {
        fun work(uuid: UUID, targetName: String) {
            val offlinePlayer = Bukkit.getOfflinePlayer(uuid)
            work(offlinePlayer, targetName)
        }

        private fun work(offlinePlayer: OfflinePlayer, targetName: String) {
            if (offlinePlayer.isOnline) {
                val player = offlinePlayer.player!!
                val lands = landManager.getPlayerLands(player.uniqueId, Rank.OWNER)
                if (lands != null) {
                    val targetOffline = Bukkit.getOfflinePlayerIfCached(targetName)
                    if (targetOffline == null) {
                        player.sendMessage("존재하지 않는 유저입니다!".errorFormat())
                        return
                    }

                    if (targetOffline.uniqueId == player.uniqueId) {
                        player.sendMessage("관리 대리인만 강등이 가능합니다.".errorFormat())
                        return
                    }

                    val targetOfflineData = lands.memberMap[targetOffline.uniqueId]
                    if (targetOfflineData != null) {
                        when (targetOfflineData.rank) {
                            Rank.DELEGATOR -> {
                                targetOfflineData.rank = Rank.MEMBER

                                if (targetOffline.isOnline)
                                    targetOffline.player!!.sendMessage("${player.name}이/가 당신을 멤버로 강등시켰습니다.".infoFormat())
                                else
                                    bukkitLinkedAPI.sendMessageAcrossServer(
                                        targetName,
                                        "${player.name}이/가 당신을 멤버로 강등시켰습니다.".infoFormat()
                                    )
                                player.sendMessage("${targetName}이/가 멤버로 강등되었습니다..".infoFormat())
                            }

                            else -> player.sendMessage("관리 대리인만 강등이 가능합니다.".errorFormat())
                        }
                    } else player.sendMessage("${targetName}은/는 당신의 땅에 소속되어있지 않습니다.".errorFormat())
                } else player.sendMessage("해당 명령어는 땅 소유자와 관리 대리인만 사용가능합니다.".errorFormat())
            } else {
                val hostName = offlinePlayer.name
                val lands = landManager.getPlayerLands(offlinePlayer.uniqueId, Rank.OWNER)
                if (lands != null) {
                    val targetOffline = Bukkit.getOfflinePlayerIfCached(targetName)
                    if (targetOffline == null) {
                        bukkitLinkedAPI.sendMessageAcrossServer(hostName, "존재하지 않는 유저입니다!".errorFormat())
                        return
                    }

                    if (targetOffline.uniqueId == offlinePlayer.uniqueId) {
                        bukkitLinkedAPI.sendMessageAcrossServer(hostName, "관리 대리인만 강등이 가능합니다.".errorFormat())
                        return
                    }

                    val targetOfflineData = lands.memberMap[targetOffline.uniqueId]
                    if (targetOfflineData != null) {
                        when (targetOfflineData.rank) {
                            Rank.DELEGATOR -> {
                                targetOfflineData.rank = Rank.MEMBER

                                if (targetOffline.isOnline)
                                    targetOffline.player!!.sendMessage("${hostName}이/가 당신을 멤버로 강등시켰습니다.".infoFormat())
                                else
                                    bukkitLinkedAPI.sendMessageAcrossServer(
                                        targetName,
                                        "${hostName}이/가 당신을 멤버로 강등시켰습니다.".infoFormat()
                                    )
                                bukkitLinkedAPI.sendMessageAcrossServer(
                                    hostName,
                                    "${targetName}이/가 멤버로 강등되었습니다..".infoFormat()
                                )
                            }

                            else -> bukkitLinkedAPI.sendMessageAcrossServer(
                                hostName,
                                "관리 대리인만 강등이 가능합니다.".errorFormat()
                            )
                        }
                    } else bukkitLinkedAPI.sendMessageAcrossServer(
                        hostName,
                        "${targetName}은/는 당신의 땅에 소속되어있지 않습니다.".errorFormat()
                    )
                } else bukkitLinkedAPI.sendMessageAcrossServer(
                    hostName,
                    "해당 명령어는 땅 소유자와 관리 대리인만 사용가능합니다.".errorFormat()
                )
            }
        }
    }
}