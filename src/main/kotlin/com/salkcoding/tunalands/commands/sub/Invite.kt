package com.salkcoding.tunalands.commands.sub

import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.lands.canRejoin
import com.salkcoding.tunalands.lands.getRejoinCooldown
import com.salkcoding.tunalands.tunaLands
import com.salkcoding.tunalands.util.errorFormat
import com.salkcoding.tunalands.util.infoFormat
import com.salkcoding.tunalands.util.warnFormat
import org.bukkit.Bukkit
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
                    val lands = landManager.getPlayerLands(player.uniqueId, Rank.OWNER, Rank.DELEGATOR)
                    if (lands != null) {
                        val target = Bukkit.getPlayer(args[0])
                        if (target == null) {
                            player.sendMessage("존재하지 않는 유저입니다!".errorFormat())
                            return true
                        }
                        val targetLands = landManager.getPlayerLands(
                            target.uniqueId,
                            Rank.OWNER,
                            Rank.DELEGATOR,
                            Rank.MEMBER,
                            Rank.PARTTIMEJOB
                        )
                        if (targetLands != null) {
                            player.sendMessage("해당 플레이어는 이미 땅에 소속되어있습니다.".errorFormat())
                            return true
                        }

                        if (!target.canRejoin()) {
                            val rejoin = target.getRejoinCooldown()!! - System.currentTimeMillis()
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
                            return true
                        }

                        if (target.uniqueId == player.uniqueId) {
                            player.sendMessage("자신을 초대할 수는 없습니다.".errorFormat())
                            return true
                        }

                        if (target.uniqueId in lands.banMap) {
                            player.sendMessage("밴 당한 유저는 초대하실 수 없습니다.".errorFormat())
                            return true
                        }

                        player.sendMessage("${target.name}에게 멤버 초대장를 보냈습니다.".infoFormat())
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
                    } else player.sendMessage("해당 명령어는 땅 소유자와 관리 대리인만 사용가능합니다.".errorFormat())
                } else sender.sendMessage("콘솔에서는 사용할 수 없는 명령어입니다.".errorFormat())
                return true
            }
        }
        return false
    }
}

data class InviteData(val host: Player, val target: Player, val targetRank: Rank, val task: BukkitTask)