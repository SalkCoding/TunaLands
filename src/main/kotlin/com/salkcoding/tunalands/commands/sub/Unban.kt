package com.salkcoding.tunalands.commands.sub

import com.salkcoding.tunalands.bungeeApi
import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.data.lands.Rank
import com.salkcoding.tunalands.util.errorFormat
import com.salkcoding.tunalands.util.infoFormat
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

class Unban : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        when {
            label == "unban" && args.size == 1 -> {
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

                    player.sendMessage("${targetName}의 밴을 해제했습니다.".infoFormat())
                    if (targetOffline.isOnline)
                        targetOffline.player!!.sendMessage("${player.name}의 땅의 밴이 해제되었습니다.".infoFormat())
                    else
                        bungeeApi.sendMessage(targetName, "${player.name}의 땅의 밴이 해제되었습니다.".infoFormat())

                    lands.banMap.remove(targetOffline.uniqueId)
                } else player.sendMessage("해당 명령어는 땅 소유자와 관리 대리인만 사용가능합니다.".errorFormat())
            } else {
                val hostName = offlinePlayer.name
                val lands = landManager.getPlayerLands(offlinePlayer.uniqueId, Rank.OWNER, Rank.DELEGATOR)
                if (lands != null) {
                    val data = lands.memberMap[offlinePlayer.uniqueId]!!
                    if (data.rank == Rank.DELEGATOR && !lands.delegatorSetting.canBan) {
                        bungeeApi.sendMessage(hostName, "권한이 없습니다!".errorFormat())
                        return
                    }

                    val targetOffline = Bukkit.getOfflinePlayerIfCached(targetName)
                    if (targetOffline == null) {
                        bungeeApi.sendMessage(hostName, "존재하지 않는 유저입니다!".errorFormat())
                        return
                    }

                    bungeeApi.sendMessage(hostName, "${targetName}의 밴을 해제했습니다.".infoFormat())
                    if (targetOffline.isOnline)
                        targetOffline.player!!.sendMessage("${hostName}의 땅의 밴이 해제되었습니다.".infoFormat())
                    else
                        bungeeApi.sendMessage(targetName, "${hostName}의 땅의 밴이 해제되었습니다.".infoFormat())

                    lands.banMap.remove(targetOffline.uniqueId)
                } else bungeeApi.sendMessage(hostName, "해당 명령어는 땅 소유자와 관리 대리인만 사용가능합니다.".errorFormat())
            }
        }
    }
}