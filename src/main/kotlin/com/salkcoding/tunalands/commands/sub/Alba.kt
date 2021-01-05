package com.salkcoding.tunalands.commands.sub

import com.salkcoding.tunalands.bungee.BungeeSender
import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.playerListListener
import com.salkcoding.tunalands.tunaLands
import com.salkcoding.tunalands.util.errorFormat
import com.salkcoding.tunalands.util.infoFormat
import com.salkcoding.tunalands.util.warnFormat
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.concurrent.Executors

class Alba : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        when {
            label == "alba" && args.size == 1 -> {
                val player = sender as? Player
                if (player != null) {
                    val lands = landManager.getPlayerLands(player.uniqueId, Rank.OWNER, Rank.DELEGATOR)
                    if (lands != null) {
                        val targetName = args[0]
                        val targetOffline = Bukkit.getOfflinePlayerIfCached(targetName)
                        if (targetOffline == null) {
                            player.sendMessage("존재하지 않는 유저입니다!".errorFormat())
                            return true
                        }

                        if (targetOffline.uniqueId == player.uniqueId) {
                            player.sendMessage("자신을 초대할 수는 없습니다!".errorFormat())
                            return true
                        }

                        if (targetOffline.uniqueId in lands.memberMap) {
                            if (lands.memberMap[targetOffline.uniqueId]!!.rank != Rank.VISITOR) {
                                player.sendMessage("해당 플레이어는 이미 땅에 소속되어있습니다.".errorFormat())
                                return true
                            }
                        }

                        if (targetOffline.uniqueId in lands.banMap) {
                            player.sendMessage("밴 당한 유저는 초대하실 수 없습니다!".errorFormat())
                            return true
                        }

                        player.sendMessage("${targetOffline.name}에게 알바로 채용하겠다는 의사를 보냈습니다.".infoFormat())
                        //Online in current server
                        if (targetOffline.isOnline) {
                            val target = targetOffline.player!!
                            target.sendMessage("${player.name}이/가 당신을 ${lands.ownerName}의 알바로 채용하고자 합니다.".infoFormat())
                            target.sendMessage("수락하시려면, /ld accept를 거부하시려면, /ld deny을 입력해주세요.".infoFormat())

                            inviteMap[target.uniqueId] =
                                InviteData(
                                    player,
                                    target,
                                    Rank.PARTTIMEJOB,
                                    Bukkit.getScheduler().runTaskLater(tunaLands, Runnable {
                                        player.sendMessage("${target.name}이/가 당신의 채용에 응하지 않았습니다.".warnFormat())
                                        target.sendMessage("초대가 만료되었습니다.".warnFormat())
                                        inviteMap.remove(target.uniqueId)
                                    }, 600)//Later 30 seconds
                                )
                        } else {//Target is not online or in proxy server
                            val service = Executors.newSingleThreadExecutor()
                            service.submit {
                                val proxyPlayerList = playerListListener.getPlayerList(player)
                                Bukkit.getScheduler().runTask(tunaLands, Runnable {
                                    if (targetName in proxyPlayerList) {//In proxy server
                                        BungeeSender.sendMessage(
                                            targetName,
                                            "${player.name}이/가 당신을 ${lands.ownerName}의 알바로 채용하고자 합니다.".infoFormat()
                                        )
                                        BungeeSender.sendMessage(
                                            targetName,
                                            "수락하시려면, /ld accept를 거부하시려면, /ld deny을 입력해주세요.".infoFormat()
                                        )
                                        val inviteData = InviteData(
                                            player,
                                            targetOffline,
                                            Rank.PARTTIMEJOB,
                                            Bukkit.getScheduler().runTaskLater(tunaLands, Runnable {
                                                player.sendMessage("${targetOffline.name}이/가 당신의 채용에 응하지 않았습니다.".warnFormat())
                                                BungeeSender.sendMessage(targetName, "초대가 만료되었습니다.".warnFormat())
                                                inviteMap.remove(targetOffline.uniqueId)
                                            }, 600)//Later 30 seconds
                                        )
                                        inviteMap[targetOffline.uniqueId] = inviteData
                                    } else player.sendMessage("해당 플레이어를 찾을 수 없습니다.".errorFormat())//Not online
                                })
                            }
                        }
                    } else player.sendMessage("해당 명령어는 땅 소유자와 관리 대리인만 사용가능합니다!".errorFormat())
                } else sender.sendMessage("콘솔에서는 사용할 수 없는 명령어입니다.".errorFormat())
                return true
            }
        }
        return false
    }
}