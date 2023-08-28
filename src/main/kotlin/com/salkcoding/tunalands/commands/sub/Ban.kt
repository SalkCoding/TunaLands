package com.salkcoding.tunalands.commands.sub

import com.google.gson.JsonObject
import com.salkcoding.tunalands.*
import com.salkcoding.tunalands.lands.Lands
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

class Ban : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (label == "ban" && args.size == 1) {
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
                    val data = lands.memberMap[player.uniqueId]!!
                    if (data.rank == Rank.DELEGATOR && !lands.delegatorSetting.canBan) {
                        player.sendMessage("권한이 없습니다!".errorFormat())
                        return
                    }

                    val targetOffline = Bukkit.getOfflinePlayerIfCached(targetName)
                    if (targetOffline == null) {
                        player.sendMessage("존재하지 않는 유저입니다!".errorFormat())
                        return
                    }

                    val targetUUID = targetOffline.uniqueId
                    if (targetUUID == player.uniqueId) {
                        player.sendMessage("자신을 밴할 수는 없습니다.".errorFormat())
                        return
                    }

                    val targetData = lands.memberMap[targetUUID]
                    //Try to ban member
                    if (targetData != null) {
                        if (targetData.rank == Rank.OWNER) {
                            player.sendMessage("소유자는 밴할 수 없습니다.".errorFormat())
                            return
                        }
                        val beforeCnt = lands.getFullTimeMemberSize()
                        lands.memberMap.remove(targetUUID)
                        val afterCnt = lands.getFullTimeMemberSize()
                        lands.fuelRecomputeAndSave(beforeCnt, afterCnt)
                    }
                    leftManager.recordLeft(targetUUID)

                    player.sendMessage("${targetOffline.name}을/를 밴하였습니다.".infoFormat())
                    if (targetOffline.isOnline)
                        targetOffline.player!!.sendMessage("${lands.landsName}에서 ${player.name}에 의해 밴당하셨습니다.".infoFormat())
                    else
                        bukkitLinkedAPI.sendMessageAcrossServer(
                            targetName,
                            "${lands.landsName}에서 ${player.name}에 의해 밴당하셨습니다.".infoFormat()
                        )

                    lands.banMap[targetUUID] =
                        Lands.BanData(targetUUID, System.currentTimeMillis())

                    val json = JsonObject()
                    json.addProperty("uuid", targetUUID.toString())
                    metamorphosis.send("com.salkcoding.tunalands.sync_ban", json.toString())

                } else player.sendMessage("해당 명령어는 땅 소유자와 관리 대리인만 사용가능합니다.".errorFormat())
            } else {
                val hostName = offlinePlayer.name
                val lands = landManager.getPlayerLands(offlinePlayer.uniqueId, Rank.OWNER, Rank.DELEGATOR)
                if (lands != null) {
                    val data = lands.memberMap[offlinePlayer.uniqueId]!!
                    if (data.rank == Rank.DELEGATOR && !lands.delegatorSetting.canBan) {
                        bukkitLinkedAPI.sendMessageAcrossServer(hostName, "권한이 없습니다!".errorFormat())
                        return
                    }

                    val targetOffline = Bukkit.getOfflinePlayerIfCached(targetName)
                    if (targetOffline == null) {
                        bukkitLinkedAPI.sendMessageAcrossServer(hostName, "존재하지 않는 유저입니다!".errorFormat())
                        return
                    }

                    val targetUUID = targetOffline.uniqueId
                    if (targetUUID == offlinePlayer.uniqueId) {
                        bukkitLinkedAPI.sendMessageAcrossServer(hostName, "자신을 밴할 수는 없습니다.".errorFormat())
                        return
                    }

                    val targetData = lands.memberMap[targetUUID]
                    //Try to ban member
                    if (targetData != null) {
                        if (targetData.rank == Rank.OWNER) {
                            bukkitLinkedAPI.sendMessageAcrossServer(hostName, "소유자는 밴할 수 없습니다.".errorFormat())
                            return
                        }
                        val beforeCnt = lands.getFullTimeMemberSize()
                        lands.memberMap.remove(targetUUID)
                        val afterCnt = lands.getFullTimeMemberSize()
                        lands.fuelRecomputeAndSave(beforeCnt, afterCnt)
                    }
                    leftManager.recordLeft(targetUUID)

                    //Else ban another player
                    bukkitLinkedAPI.sendMessageAcrossServer(hostName, "${targetOffline.name}을/를 밴하였습니다.".infoFormat())
                    if (targetOffline.isOnline)
                        targetOffline.player!!.sendMessage("${lands.landsName}에서 ${offlinePlayer.name}에 의해 밴당하셨습니다.".infoFormat())
                    else
                        bukkitLinkedAPI.sendMessageAcrossServer(
                            targetName,
                            "${lands.landsName}에서 ${offlinePlayer.name}에 의해 밴당하셨습니다.".infoFormat()
                        )

                    lands.banMap[targetUUID] =
                        Lands.BanData(targetUUID, System.currentTimeMillis())

                    val json = JsonObject()
                    json.addProperty("uuid", targetUUID.toString())
                    metamorphosis.send("com.salkcoding.tunalands.sync_ban", json.toString())
                } else bukkitLinkedAPI.sendMessageAcrossServer(
                    hostName,
                    "해당 명령어는 땅 소유자와 관리 대리인만 사용가능합니다.".errorFormat()
                )
            }
        }
    }
}