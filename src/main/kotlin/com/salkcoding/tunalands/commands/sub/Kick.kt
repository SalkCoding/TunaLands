package com.salkcoding.tunalands.commands.sub

import com.salkcoding.tunalands.bukkitLinkedAPI
import com.salkcoding.tunalands.configuration
import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.leftManager
import com.salkcoding.tunalands.util.errorFormat
import com.salkcoding.tunalands.util.infoFormat
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*
import kotlin.math.roundToLong

class Kick : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (label == "kick" && args.size == 1) {
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
                val lands = landManager.getPlayerLands(player.uniqueId, Rank.OWNER, Rank.DELEGATOR)
                if (lands != null) {
                    val targetOffline = Bukkit.getOfflinePlayerIfCached(targetName)
                    if (targetOffline == null) {
                        player.sendMessage("존재하지 않는 유저입니다!".errorFormat())
                        return
                    }

                    val targetUUID = targetOffline.uniqueId
                    if (targetUUID == player.uniqueId) {
                        player.sendMessage("스스로를 쫓아낼 수는 없습니다.".errorFormat())
                        return
                    }

                    val targetData = lands.memberMap[targetUUID]
                    if (targetData != null) {
                        if (targetData.rank == Rank.OWNER) {
                            player.sendMessage("소유자를 쫓아낼 수는 없습니다.".errorFormat())
                            return
                        }
                        lands.memberMap.remove(targetUUID)
                        leftManager.recordLeft(targetUUID)
                        lands.dayPerFuel =
                            configuration.fuel.getFuelRequirement(lands).dayPerFuel

                        player.sendMessage("${targetName}을/를 쫓아냈습니다.".infoFormat())
                        if (targetOffline.isOnline)
                            targetOffline.player!!.sendMessage("${player.name}이/가 당신을 ${lands.ownerName}의 땅에서 당신을 쫓아냈습니다.".infoFormat())
                        else
                            bukkitLinkedAPI.sendMessageAcrossServer(
                                targetName,
                                "${player.name}이/가 당신을 ${lands.ownerName}의 땅에서 당신을 쫓아냈습니다.".infoFormat()
                            )

                    } else player.sendMessage("${targetName}은/는 당신의 땅에 소속되어있지 않습니다.".errorFormat())
                } else player.sendMessage("해당 명령어는 땅 소유자와 관리 대리인만 사용가능합니다.".errorFormat())
            } else {
                val hostName = offlinePlayer.name
                val lands = landManager.getPlayerLands(offlinePlayer.uniqueId, Rank.OWNER, Rank.DELEGATOR)
                if (lands != null) {
                    val targetOffline = Bukkit.getOfflinePlayerIfCached(targetName)
                    if (targetOffline == null) {
                        bukkitLinkedAPI.sendMessageAcrossServer(hostName, "존재하지 않는 유저입니다!".errorFormat())
                        return
                    }

                    val targetUUID = targetOffline.uniqueId
                    if (targetUUID == offlinePlayer.uniqueId) {
                        bukkitLinkedAPI.sendMessageAcrossServer(hostName, "스스로를 쫓아낼 수는 없습니다.".errorFormat())
                        return
                    }

                    val targetData = lands.memberMap[targetUUID]
                    if (targetData != null) {
                        if (targetData.rank == Rank.OWNER) {
                            bukkitLinkedAPI.sendMessageAcrossServer(hostName, "소유자를 쫓아낼 수는 없습니다.".errorFormat())
                            return
                        }

                        lands.memberMap.remove(targetUUID)
                        lands.dayPerFuel = configuration.fuel.getFuelRequirement(lands).dayPerFuel
                        leftManager.recordLeft(targetUUID)

                        bukkitLinkedAPI.sendMessageAcrossServer(hostName, "${targetName}을/를 쫓아냈습니다.".infoFormat())
                        if (targetOffline.isOnline)
                            targetOffline.player!!.sendMessage("${hostName}이/가 당신을 ${lands.ownerName}의 땅에서 당신을 쫓아냈습니다.".infoFormat())
                        else
                            bukkitLinkedAPI.sendMessageAcrossServer(
                                targetName,
                                "${hostName}이/가 당신을 ${lands.ownerName}의 땅에서 당신을 쫓아냈습니다.".infoFormat()
                            )

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