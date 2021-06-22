package com.salkcoding.tunalands.commands.sub

import com.salkcoding.tunalands.bungeeApi
import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.util.errorFormat
import com.salkcoding.tunalands.util.infoFormat
import com.salkcoding.tunalands.util.warnFormat
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

class SetLeader : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (label == "setleader" && args.size == 1) {
            val player = sender as? Player
            if (player != null) {
                work(player, args[0])
            } else sender.sendMessage("콘솔에서는 사용할 수 없는 명령어입니다.".errorFormat())
            return true
        }
        return false
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
                    val data = lands.memberMap[player.uniqueId]!!
                    val targetOffline = Bukkit.getOfflinePlayerIfCached(targetName)
                    if (targetOffline == null) {
                        player.sendMessage("존재하지 않는 유저입니다!".errorFormat())
                        return
                    }

                    if (targetOffline.uniqueId == player.uniqueId) {
                        player.sendMessage("당신은 이미 땅의 소유자입니다.".errorFormat())
                        return
                    }

                    val targetData = lands.memberMap[targetOffline.uniqueId]
                    if (targetData != null) {
                        when (targetData.rank) {
                            Rank.DELEGATOR, Rank.MEMBER -> {
                                if (targetOffline.isOnline) {
                                    val target = targetOffline.player!!
                                    landManager.changeChunksOwner(player, target)

                                    targetData.rank = Rank.OWNER
                                    data.rank = Rank.DELEGATOR

                                    target.sendMessage("${player.name}의 땅의 소유자가 되었습니다.".infoFormat())
                                } else {
                                    lands.ownerName = targetName
                                    landManager.changeChunksOwner(player, targetOffline)

                                    targetData.rank = Rank.OWNER
                                    data.rank = Rank.DELEGATOR

                                    bungeeApi.sendMessage(
                                        targetName,
                                        "${player.name}의 땅의 소유자가 되었습니다.".infoFormat()
                                    )
                                }

                                player.sendMessage("${targetName}은/는 이제 땅의 소유자입니다.".infoFormat())
                                player.sendMessage("관리 대리인으로 강등되셨습니다.".warnFormat())

                                lands.sendMessageToOnlineMembers("${targetName}이/가 새로운 땅의 소유자가 되었습니다.".infoFormat())
                            }
                            else -> player.sendMessage("관리 대리인과 멤버만 소유자가 될 수 있습니다.".errorFormat())
                        }
                    } else player.sendMessage("${targetName}은/는 당신의 땅에 소속되어있지 않습니다.".errorFormat())
                } else player.sendMessage("해당 명령어는 땅 소유자만 사용가능합니다.".errorFormat())
            } else {
                val hostName = offlinePlayer.name
                val lands = landManager.getPlayerLands(offlinePlayer.uniqueId, Rank.OWNER)
                if (lands != null) {
                    val data = lands.memberMap[offlinePlayer.uniqueId]!!
                    val targetOffline = Bukkit.getOfflinePlayerIfCached(targetName)
                    if (targetOffline == null) {
                        bungeeApi.sendMessage(hostName, "존재하지 않는 유저입니다!".errorFormat())
                        return
                    }

                    if (targetOffline.uniqueId == offlinePlayer.uniqueId) {
                        bungeeApi.sendMessage(hostName, "당신은 이미 땅의 소유자입니다.".errorFormat())
                        return
                    }

                    val targetData = lands.memberMap[targetOffline.uniqueId]
                    if (targetData != null) {
                        when (targetData.rank) {
                            Rank.DELEGATOR, Rank.MEMBER -> {
                                if (targetOffline.isOnline) {
                                    val target = targetOffline.player!!
                                    lands.ownerName = target.name
                                    landManager.changeChunksOwner(offlinePlayer, target)

                                    targetData.rank = Rank.OWNER
                                    data.rank = Rank.DELEGATOR

                                    target.sendMessage("${hostName}의 땅의 소유자가 되었습니다.".infoFormat())
                                } else {
                                    lands.ownerName = targetName
                                    landManager.changeChunksOwner(offlinePlayer, targetOffline)

                                    targetData.rank = Rank.OWNER
                                    data.rank = Rank.DELEGATOR

                                    bungeeApi.sendMessage(
                                        targetName,
                                        "${hostName}의 땅의 소유자가 되었습니다.".infoFormat()
                                    )
                                }

                                bungeeApi.sendMessage(hostName, "${targetName}은/는 이제 땅의 소유자입니다.".infoFormat())
                                bungeeApi.sendMessage(hostName, "관리 대리인으로 강등되셨습니다.".warnFormat())

                                lands.sendMessageToOnlineMembers("${targetName}이/가 새로운 땅의 소유자가 되었습니다.".infoFormat())
                            }
                            else -> bungeeApi.sendMessage(hostName, "관리 대리인과 멤버만 소유자가 될 수 있습니다.".errorFormat())
                        }
                    } else bungeeApi.sendMessage(hostName, "${targetName}은/는 당신의 땅에 소속되어있지 않습니다.".errorFormat())
                } else bungeeApi.sendMessage(hostName, "해당 명령어는 땅 소유자만 사용가능합니다.".errorFormat())
            }
        }
    }
}