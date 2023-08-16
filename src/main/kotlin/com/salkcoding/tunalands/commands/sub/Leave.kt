package com.salkcoding.tunalands.commands.sub

import com.salkcoding.tunalands.*
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.util.errorFormat
import com.salkcoding.tunalands.util.infoFormat
import com.salkcoding.tunalands.util.toColoredText
import com.salkcoding.tunalands.util.warnFormat
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.OfflinePlayer
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*
import kotlin.math.roundToLong

class Leave : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (label == "leave" && args.isEmpty()) {
            val player = sender as? Player
            if (player != null) {
                work(player)
            } else sender.sendMessage("콘솔에서는 사용할 수 없는 명령어입니다.".errorFormat())
            return true
        }
        return false
    }

    companion object {

        fun work(uuid: UUID) {
            val player = Bukkit.getOfflinePlayer(uuid)
            work(player)
        }

        private fun work(offlinePlayer: OfflinePlayer) {
            if (offlinePlayer.isOnline) {
                val player = offlinePlayer.player!!
                val playerUUID = player.uniqueId
                val lands =
                    landManager.getPlayerLands(playerUUID, Rank.VISITOR)
                        ?: landManager.getPlayerLands(playerUUID, Rank.PARTTIMEJOB)
                        ?: landManager.getPlayerLands(playerUUID, Rank.OWNER, Rank.DELEGATOR, Rank.MEMBER)
                if (lands != null) {
                    val data = lands.memberMap[playerUUID]!!
                    if (data.rank == Rank.OWNER) {
                        player.sendMessage("소유자는 땅을 삭제하기전에는 땅에서 나갈 수 없습니다.".errorFormat())
                        return
                    }

                    lands.memberMap.remove(playerUUID)

                    lands.dayPerFuel =
                        configuration.fuel.getFuelRequirement(lands).dayPerFuel

                    player.sendMessage("${lands.ownerName}의 땅을 떠났습니다.".infoFormat())
                    leftManager.recordLeft(playerUUID)

                    lands.sendMessageToOnlineMembers("${ChatColor.GRAY}[${data.rank.toColoredText()}${ChatColor.GRAY}] ${ChatColor.GREEN}${player.name}${ChatColor.WHITE}이/가 땅을 떠났습니다.".warnFormat())
                } else player.sendMessage("어느 땅에도 소속되어있지 않습니다.".errorFormat())
            } else {
                val playerUUID = offlinePlayer.uniqueId
                val hostName = offlinePlayer.name
                val lands =
                    landManager.getPlayerLands(playerUUID, Rank.VISITOR)
                        ?: landManager.getPlayerLands(playerUUID, Rank.PARTTIMEJOB)
                        ?: landManager.getPlayerLands(playerUUID, Rank.OWNER, Rank.DELEGATOR, Rank.MEMBER)
                if (lands != null) {
                    val data = lands.memberMap[playerUUID]!!
                    if (data.rank == Rank.OWNER) {
                        bukkitLinkedAPI.sendMessageAcrossServer(
                            hostName,
                            "소유자는 땅을 삭제하기전에는 땅에서 나갈 수 없습니다.".errorFormat()
                        )
                        return
                    }

                    lands.memberMap.remove(playerUUID)

                    lands.dayPerFuel =
                        configuration.fuel.getFuelRequirement(lands).dayPerFuel

                    bukkitLinkedAPI.sendMessageAcrossServer(hostName, "${lands.ownerName}의 땅을 떠났습니다.".infoFormat())
                    leftManager.recordLeft(playerUUID)

                    lands.sendMessageToOnlineMembers("${hostName}이/가 땅을 떠났습니다.".warnFormat())
                } else bukkitLinkedAPI.sendMessageAcrossServer(hostName, "어느 땅에도 소속되어있지 않습니다.".errorFormat())
            }

            metamorphosis.send("com.salkcoding.tunalands.sync_leave", offlinePlayer.uniqueId.toString())
        }
    }
}