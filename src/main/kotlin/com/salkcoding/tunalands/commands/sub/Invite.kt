package com.salkcoding.tunalands.commands.sub

import com.salkcoding.tunalands.bungee.proxyPlayerSet
import com.salkcoding.tunalands.bungeeApi
import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.lands.canRejoin
import com.salkcoding.tunalands.lands.getRejoinCooldown
import com.salkcoding.tunalands.tunaLands
import com.salkcoding.tunalands.util.errorFormat
import com.salkcoding.tunalands.util.infoFormat
import com.salkcoding.tunalands.util.warnFormat
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask
import java.util.*

val inviteMap = mutableMapOf<UUID, InviteData>()

class Invite : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        when {
            label == "invite" && args.size == 1 -> {
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
                val lands = landManager.getPlayerLands(player.uniqueId, Rank.OWNER, Rank.DELEGATOR)
                if (lands != null) {
                    val targetOffline = Bukkit.getOfflinePlayerIfCached(targetName)
                    if (targetOffline == null) {
                        player.sendMessage("존재하지 않는 유저입니다.".errorFormat())
                        return
                    }

                    val targetUUID = targetOffline.uniqueId
                    val targetLands = landManager.getPlayerLands(
                        targetUUID,
                        Rank.OWNER,
                        Rank.DELEGATOR,
                        Rank.MEMBER,
                        Rank.PARTTIMEJOB
                    )
                    if (targetLands != null) {
                        player.sendMessage("해당 플레이어는 이미 땅에 소속되어있습니다.".errorFormat())
                        return
                    }

                    if (!targetOffline.canRejoin()) {
                        val rejoin = targetOffline.getRejoinCooldown()!! - System.currentTimeMillis()
                        player.sendMessage(
                            "해당 플레이어는 ${
                                rejoin / 86400000
                            }일 ${
                                (rejoin / 3600000) % 24
                            }시간 ${
                                (rejoin / 60000) % 60
                            }분 ${
                                (rejoin / 1000) % 60
                            }초가 지나야 초대를 받을 수 있습니다.".errorFormat()
                        )
                        return
                    }

                    if (targetUUID == player.uniqueId) {
                        player.sendMessage("자신을 초대할 수는 없습니다.".errorFormat())
                        return
                    }

                    if (targetUUID in lands.banMap) {
                        player.sendMessage("밴 당한 유저는 초대하실 수 없습니다.".errorFormat())
                        return
                    }

                    player.sendMessage("${targetName}에게 멤버 초대장를 보냈습니다.".infoFormat())

                    //Online in current server
                    if (targetOffline.isOnline) {
                        val target = targetOffline.player!!
                        target.sendMessage("${player.name}이/가 당신을 ${lands.ownerName}의 멤버로 초대했습니다.".infoFormat())
                        target.sendMessage("수락하시려면, /ld accept를 거부하시려면, /ld deny을 입력해주세요.".infoFormat())

                        inviteMap[target.uniqueId] =
                            InviteData(
                                player,
                                target,
                                Rank.MEMBER,
                                Bukkit.getScheduler().runTaskLater(tunaLands, Runnable {
                                    player.sendMessage("${target.name}가 초대에 응하지 않았습니다.".warnFormat())
                                    target.sendMessage("초대가 만료되었습니다.".warnFormat())
                                    inviteMap.remove(target.uniqueId)
                                }, 600)//Later 30 seconds
                            )
                    } else {//Target is not online or in proxy server
                        if (targetUUID in proxyPlayerSet) {//In proxy server
                            bungeeApi.sendMessage(
                                targetName,
                                "${player.name}이/가 당신을 ${lands.ownerName}의 멤버로 초대했습니다.".infoFormat()
                            )
                            bungeeApi.sendMessage(
                                targetName,
                                "수락하시려면, /ld accept를 거부하시려면, /ld deny을 입력해주세요.".infoFormat()
                            )
                            val inviteData = InviteData(
                                player,
                                targetOffline,
                                Rank.MEMBER,
                                Bukkit.getScheduler().runTaskLater(tunaLands, Runnable {
                                    player.sendMessage("${targetName}가 초대에 응하지 않았습니다.".warnFormat())
                                    if (targetOffline.isOnline)
                                        targetOffline.player?.sendMessage("초대가 만료되었습니다.".warnFormat())
                                    else
                                        bungeeApi.sendMessage(
                                            targetName,
                                            "초대가 만료되었습니다.".warnFormat()
                                        )
                                    inviteMap.remove(targetUUID)
                                }, 600)//Later 30 seconds
                            )
                            inviteMap[targetUUID] = inviteData
                        } else player.sendMessage("해당 플레이어를 찾을 수 없습니다.".errorFormat())//Not online
                    }
                } else player.sendMessage("해당 명령어는 땅 소유자와 관리 대리인만 사용가능합니다.".errorFormat())
            } else {
                val hostName = offlinePlayer.name
                val lands = landManager.getPlayerLands(offlinePlayer.uniqueId, Rank.OWNER, Rank.DELEGATOR)
                if (lands != null) {
                    val targetOffline = Bukkit.getOfflinePlayerIfCached(targetName)
                    if (targetOffline == null) {
                        bungeeApi.sendMessage(hostName, "존재하지 않는 유저입니다.".errorFormat())
                        return
                    }

                    val targetUUID = targetOffline.uniqueId
                    val targetLands = landManager.getPlayerLands(
                        targetUUID,
                        Rank.OWNER,
                        Rank.DELEGATOR,
                        Rank.MEMBER,
                        Rank.PARTTIMEJOB
                    )
                    if (targetLands != null) {
                        bungeeApi.sendMessage(hostName, "해당 플레이어는 이미 땅에 소속되어있습니다.".errorFormat())
                        return
                    }

                    if (!targetOffline.canRejoin()) {
                        val rejoin = targetOffline.getRejoinCooldown()!! - System.currentTimeMillis()
                        bungeeApi.sendMessage(
                            hostName,
                            "해당 플레이어는 ${
                                rejoin / 86400000
                            }일 ${
                                (rejoin / 3600000) % 24
                            }시간 ${
                                (rejoin / 60000) % 60
                            }분 ${
                                (rejoin / 1000) % 60
                            }초가 지나야 초대를 받을 수 있습니다.".errorFormat()
                        )
                        return
                    }

                    if (targetUUID == offlinePlayer.uniqueId) {
                        bungeeApi.sendMessage(hostName, "자신을 초대할 수는 없습니다.".errorFormat())
                        return
                    }

                    if (targetUUID in lands.banMap) {
                        bungeeApi.sendMessage(hostName, "밴 당한 유저는 초대하실 수 없습니다.".errorFormat())
                        return
                    }

                    bungeeApi.sendMessage(hostName, "${targetName}에게 멤버 초대장를 보냈습니다.".infoFormat())

                    //Online in current server
                    if (targetOffline.isOnline) {
                        val target = targetOffline.player!!
                        target.sendMessage("${offlinePlayer.name}이/가 당신을 ${lands.ownerName}의 멤버로 초대했습니다.".infoFormat())
                        target.sendMessage("수락하시려면, /ld accept를 거부하시려면, /ld deny을 입력해주세요.".infoFormat())

                        inviteMap[target.uniqueId] =
                            InviteData(
                                offlinePlayer,
                                target,
                                Rank.MEMBER,
                                Bukkit.getScheduler().runTaskLater(tunaLands, Runnable {
                                    bungeeApi.sendMessage(hostName, "${target.name}가 초대에 응하지 않았습니다.".warnFormat())
                                    target.sendMessage("초대가 만료되었습니다.".warnFormat())
                                    inviteMap.remove(target.uniqueId)
                                }, 600)//Later 30 seconds
                            )
                    } else {//Target is not online or in proxy server
                        if (targetUUID in proxyPlayerSet) {//In proxy server
                            bungeeApi.sendMessage(
                                targetName,
                                "${offlinePlayer.name}이/가 당신을 ${lands.ownerName}의 멤버로 초대했습니다.".infoFormat()
                            )
                            bungeeApi.sendMessage(
                                targetName,
                                "수락하시려면, /ld accept를 거부하시려면, /ld deny을 입력해주세요.".infoFormat()
                            )
                            val inviteData = InviteData(
                                offlinePlayer,
                                targetOffline,
                                Rank.MEMBER,
                                Bukkit.getScheduler().runTaskLater(tunaLands, Runnable {
                                    bungeeApi.sendMessage(hostName, "${targetName}가 초대에 응하지 않았습니다.".warnFormat())
                                    if (targetOffline.isOnline)
                                        targetOffline.player?.sendMessage("초대가 만료되었습니다.".warnFormat())
                                    else
                                        bungeeApi.sendMessage(
                                            targetName,
                                            "초대가 만료되었습니다.".warnFormat()
                                        )
                                    inviteMap.remove(targetUUID)
                                }, 600)//Later 30 seconds
                            )
                            inviteMap[targetUUID] = inviteData
                        } else bungeeApi.sendMessage(hostName, "해당 플레이어를 찾을 수 없습니다.".errorFormat())//Not online
                    }
                } else bungeeApi.sendMessage(hostName, "해당 명령어는 땅 소유자와 관리 대리인만 사용가능합니다.".errorFormat())
            }
        }
    }
}

data class InviteData(val host: OfflinePlayer, val target: OfflinePlayer, val targetRank: Rank, val task: BukkitTask)