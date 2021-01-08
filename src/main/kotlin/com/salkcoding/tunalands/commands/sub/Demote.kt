package com.salkcoding.tunalands.commands.sub

import com.salkcoding.tunalands.bungeeApi
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
        when {
            label == "demote" && args.size == 1 -> {
                val player = sender as? Player
                if (player != null) {
                    work(player, args[0])
                } else sender.sendMessage("콘솔에서는 사용할 수 없는 명령어입니다.".errorFormat())
                return true
            }
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
                            Rank.MEMBER -> {
                                targetOfflineData.rank = Rank.MEMBER

                                if (targetOffline.isOnline)
                                    targetOffline.player!!.sendMessage("${player.name}이/가 당신을 멤버로 강등시켰습니다.".infoFormat())
                                else
                                    bungeeApi.sendMessage(
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
                        bungeeApi.sendMessage(hostName,"존재하지 않는 유저입니다!".errorFormat())
                        return
                    }

                    if (targetOffline.uniqueId == offlinePlayer.uniqueId) {
                        bungeeApi.sendMessage(hostName,"관리 대리인만 강등이 가능합니다.".errorFormat())
                        return
                    }

                    val targetOfflineData = lands.memberMap[targetOffline.uniqueId]
                    if (targetOfflineData != null) {
                        when (targetOfflineData.rank) {
                            Rank.MEMBER -> {
                                targetOfflineData.rank = Rank.MEMBER

                                if (targetOffline.isOnline)
                                    targetOffline.player!!.sendMessage("${hostName}이/가 당신을 멤버로 강등시켰습니다.".infoFormat())
                                else
                                    bungeeApi.sendMessage(
                                        targetName,
                                        "${hostName}이/가 당신을 멤버로 강등시켰습니다.".infoFormat()
                                    )
                                bungeeApi.sendMessage(hostName,"${targetName}이/가 멤버로 강등되었습니다..".infoFormat())
                            }
                            else -> bungeeApi.sendMessage(hostName,"관리 대리인만 강등이 가능합니다.".errorFormat())
                        }
                    } else bungeeApi.sendMessage(hostName,"${targetName}은/는 당신의 땅에 소속되어있지 않습니다.".errorFormat())
                } else bungeeApi.sendMessage(hostName,"해당 명령어는 땅 소유자와 관리 대리인만 사용가능합니다.".errorFormat())
            }
        }
    }
}