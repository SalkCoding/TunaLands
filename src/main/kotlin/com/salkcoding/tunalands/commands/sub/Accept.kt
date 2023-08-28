package com.salkcoding.tunalands.commands.sub

import com.salkcoding.tunalands.api.event.LandJoinEvent
import com.salkcoding.tunalands.bukkitLinkedAPI
import com.salkcoding.tunalands.configuration
import com.salkcoding.tunalands.landManager
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
import kotlin.math.roundToLong

class Accept : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (label == "accept" && args.isEmpty()) {
            val player = sender as? Player
            if (player != null) {
                work(player)
            } else sender.sendMessage("콘솔에서는 사용 불가능한 명령어입니다.".errorFormat())
            return true
        }
        return false
    }

    companion object {

        fun work(uuid: UUID) {
            val offlinePlayer = Bukkit.getOfflinePlayer(uuid)
            work(offlinePlayer)
        }

        private fun work(offlinePlayer: OfflinePlayer) {
            val uuid = offlinePlayer.uniqueId
            if (offlinePlayer.isOnline) {
                val player = offlinePlayer.player!!
                if (inviteMap.containsKey(uuid)) {
                    val data = inviteMap[uuid]!!
                    val host = data.host
                    val lands = landManager.getPlayerLands(host.uniqueId, Rank.OWNER, Rank.DELEGATOR)
                    if (lands != null) {
                        if (data.targetRank == Rank.MEMBER) {
                            if (lands.memberMap.filter { it.value.rank == Rank.OWNER || it.value.rank == Rank.DELEGATOR || it.value.rank == Rank.MEMBER }.size >= configuration.maxMemberLimit) {
                                player.sendMessage("해당 마을은 이미 최대 인원 ${configuration.maxMemberLimit}명에 도달하여 초대를 수락할 수 없습니다!".errorFormat())
                                return
                            }
                        } else if (data.targetRank == Rank.PARTTIMEJOB) {
                            if (lands.memberMap.filter { it.value.rank == Rank.PARTTIMEJOB }.size >= configuration.maxAlbaLimit) {
                                player.sendMessage("해당 마을은 이미 최대 알바 인원 ${configuration.maxAlbaLimit}명에 도달하여 초대를 수락할 수 없습니다!".errorFormat())
                                return
                            }
                        }

                        val day = configuration.commandCooldown.rejoinCooldown / 1728000
                        player.sendMessage("초대를 수락했습니다.".infoFormat())
                        player.sendMessage("/tl leave를 통해 탈퇴할 수 있으며, ${day}일 후 다른 땅에 재가입 가능합니다.".infoFormat())

                        val rankString = when (data.targetRank) {
                            Rank.MEMBER -> "멤버"
                            Rank.PARTTIMEJOB -> "알바"
                            else -> "null"
                        }

                        lands.sendMessageToOnlineMembers("${player.name}님이 $rankString 등급으로 땅에 가입되었습니다.".infoFormat())

                        landManager.getPlayerLands(
                            player.uniqueId,
                            Rank.VISITOR
                        )?.memberMap?.remove(player.uniqueId)

                        Bukkit.getPluginManager().callEvent(
                            LandJoinEvent(
                                lands,
                                player,
                                data.targetRank
                            )
                        )
                        val present = System.currentTimeMillis()
                        val beforeCnt = lands.getFullTimeMemberSize()
                        lands.memberMap[uuid] = Lands.MemberData(uuid, data.targetRank, present, present)
                        val afterCnt = lands.getFullTimeMemberSize()
                        lands.fuelRecomputeAndSave(beforeCnt, afterCnt, data.targetRank != Rank.PARTTIMEJOB)

                        data.task.cancel()
                        inviteMap.remove(uuid)
                    } else player.sendMessage("해당 땅이 더이상 존재하지 않습니다.".errorFormat())
                } else player.sendMessage("받은 초대가 없습니다.".errorFormat())
            } else {
                if (inviteMap.containsKey(uuid)) {
                    val data = inviteMap[uuid]!!
                    val host = data.host
                    val lands = landManager.getPlayerLands(host.uniqueId, Rank.OWNER, Rank.DELEGATOR)
                    if (lands != null) {
                        if (data.targetRank == Rank.MEMBER) {
                            if (lands.memberMap.filter { it.value.rank == Rank.OWNER || it.value.rank == Rank.DELEGATOR || it.value.rank == Rank.MEMBER }.size >= configuration.maxMemberLimit) {
                                bukkitLinkedAPI.sendMessageAcrossServer(
                                    offlinePlayer.name,
                                    "해당 마을은 이미 최대 인원 ${configuration.maxMemberLimit}명에 도달하여 초대를 수락할 수 없습니다!".errorFormat()
                                )
                                return
                            }
                        } else if (data.targetRank == Rank.PARTTIMEJOB) {
                            if (lands.memberMap.filter { it.value.rank == Rank.PARTTIMEJOB }.size >= configuration.maxAlbaLimit) {
                                bukkitLinkedAPI.sendMessageAcrossServer(
                                    offlinePlayer.name,
                                    "해당 마을은 이미 최대 알바 인원 ${configuration.maxAlbaLimit}명에 도달하여 초대를 수락할 수 없습니다!".errorFormat()
                                )
                                return
                            }
                        }

                        val day = configuration.commandCooldown.rejoinCooldown / 1728000
                        bukkitLinkedAPI.sendMessageAcrossServer(offlinePlayer.name, "초대를 수락했습니다.".infoFormat())
                        bukkitLinkedAPI.sendMessageAcrossServer(
                            offlinePlayer.name,
                            "/tl leave를 통해 탈퇴할 수 있으며, ${day}일 후 다른 땅에 재가입 가능합니다.".infoFormat()
                        )

                        val rankString = when (data.targetRank) {
                            Rank.MEMBER -> "멤버"
                            Rank.PARTTIMEJOB -> "알바"
                            else -> "null"
                        }

                        lands.sendMessageToOnlineMembers("${offlinePlayer.name}님이 $rankString 등급으로 땅에 가입되었습니다.".infoFormat())

                        landManager.getPlayerLands(
                            offlinePlayer.uniqueId,
                            Rank.VISITOR
                        )?.memberMap?.remove(offlinePlayer.uniqueId)

                        val present = System.currentTimeMillis()
                        val beforeCnt = lands.getFullTimeMemberSize()
                        lands.memberMap[uuid] = Lands.MemberData(uuid, data.targetRank, present, present)
                        val afterCnt = lands.getFullTimeMemberSize()
                        lands.fuelRecomputeAndSave(beforeCnt, afterCnt, data.targetRank != Rank.PARTTIMEJOB)

                        data.task.cancel()
                        inviteMap.remove(uuid)
                    } else bukkitLinkedAPI.sendMessageAcrossServer(
                        offlinePlayer.name,
                        "해당 땅이 더이상 존재하지 않습니다.".errorFormat()
                    )
                } else bukkitLinkedAPI.sendMessageAcrossServer(offlinePlayer.name, "받은 초대가 없습니다.".errorFormat())
            }
        }
    }
}