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

class Cancel : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (label == "cancel" && args.size == 1) {
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
                val lands = landManager.getPlayerLands(offlinePlayer.uniqueId, Rank.OWNER, Rank.DELEGATOR)
                if (lands != null) {
                    val target = Bukkit.getOfflinePlayerIfCached(targetName)
                    if (target == null) {
                        player.sendMessage("존재하지 않는 유저입니다!".errorFormat())
                        return
                    }

                    if (target.uniqueId in inviteMap) {
                        val data = inviteMap[target.uniqueId]!!
                        player.sendMessage("${targetName}에게 보낸 초대가 취소되었습니다.".infoFormat())
                        if (data.target.isOnline) {
                            data.target.player!!.sendMessage("${player.name}가 초대를 취소하였습니다.".errorFormat())
                        } else {
                            bungeeApi.sendMessage(
                                targetName,
                                "${player.name}가 초대를 취소하였습니다.".errorFormat()
                            )
                        }
                        data.task.cancel()
                        inviteMap.remove(data.target.uniqueId)
                    } else player.sendMessage("보낸 초대가 없습니다.".errorFormat())
                } else player.sendMessage("해당 명령어는 땅 소유자와 관리 대리인만 사용가능합니다.".errorFormat())
            } else {
                val hostName = offlinePlayer.name
                val lands = landManager.getPlayerLands(offlinePlayer.uniqueId, Rank.OWNER, Rank.DELEGATOR)
                if (lands != null) {
                    val target = Bukkit.getOfflinePlayerIfCached(targetName)
                    if (target == null) {
                        bungeeApi.sendMessage(hostName, "존재하지 않는 유저입니다!".errorFormat())
                        return
                    }

                    if (target.uniqueId in inviteMap) {
                        val data = inviteMap[target.uniqueId]!!
                        bungeeApi.sendMessage(hostName, "${targetName}에게 보낸 초대가 취소되었습니다.".infoFormat())
                        if (data.target.isOnline) {
                            data.target.player!!.sendMessage("${hostName}가 초대를 취소하였습니다.".errorFormat())
                        } else {
                            bungeeApi.sendMessage(
                                targetName,
                                "${hostName}가 초대를 취소하였습니다.".errorFormat()
                            )
                        }
                        data.task.cancel()
                        inviteMap.remove(data.target.uniqueId)
                    } else bungeeApi.sendMessage(hostName, "보낸 초대가 없습니다.".errorFormat())
                } else bungeeApi.sendMessage(hostName, "해당 명령어는 땅 소유자와 관리 대리인만 사용가능합니다.".errorFormat())
            }
        }
    }
}