package com.salkcoding.tunalands.commands.sub

import com.salkcoding.tunalands.bungeeApi
import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.tunaLands
import com.salkcoding.tunalands.util.errorFormat
import com.salkcoding.tunalands.util.infoFormat
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.io.File
import java.util.*

class Delete : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        when {
            label == "delete" && args.isEmpty() -> {
                val player = sender as? Player
                if (player != null) {
                    work(player)
                } else sender.sendMessage("콘솔에서는 사용할 수 없는 명령어입니다.".errorFormat())
                return true
            }
        }
        return false
    }

    companion object {
        fun work(uuid: UUID) {
            val offlinePlayer = Bukkit.getOfflinePlayer(uuid)
            work(offlinePlayer)
        }

        private fun work(offlinePlayer: OfflinePlayer) {
            if (offlinePlayer.isOnline) {
                val player = offlinePlayer.player!!
                val lands = landManager.getPlayerLands(player.uniqueId, Rank.OWNER)
                if (lands != null) {
                    if (lands.memberMap.size == 1) {
                        val upCore = lands.upCore
                        val downCore = lands.downCore
                        upCore.block.type = Material.AIR
                        downCore.block.type = Material.AIR

                        landManager.deleteLands(player)

                        Bukkit.getScheduler().runTaskAsynchronously(tunaLands, Runnable {
                            val folder = File(tunaLands.dataFolder, "userdata")
                            if (folder.exists()) {
                                val file = File(folder, "${player.uniqueId}.json")
                                if (file.exists())
                                    file.delete()
                            }
                        })
                        player.sendMessage("땅을 삭제했습니다.".infoFormat())
                    } else player.sendMessage("모든 멤버가 나가기전까지는 땅을 삭제할 수 없습니다.".errorFormat())
                } else player.sendMessage("해당 명령어는 땅 소유자만 사용가능합니다.".errorFormat())
            } else {
                val hostName = offlinePlayer.name
                val lands = landManager.getPlayerLands(offlinePlayer.uniqueId, Rank.OWNER)
                if (lands != null) {
                    if (lands.memberMap.size == 1) {
                        val upCore = lands.upCore
                        val downCore = lands.downCore
                        //Destroy core naturally
                        upCore.block.type = Material.AIR
                        downCore.block.type = Material.AIR

                        landManager.deleteLands(offlinePlayer)

                        Bukkit.getScheduler().runTaskAsynchronously(tunaLands, Runnable {
                            val folder = File(tunaLands.dataFolder, "userdata")
                            if (folder.exists()) {
                                val file = File(folder, "${offlinePlayer.uniqueId}.json")
                                if (file.exists())
                                    file.delete()
                            }
                        })
                        bungeeApi.sendMessage(hostName, "땅을 삭제했습니다.".infoFormat())
                    } else bungeeApi.sendMessage(hostName, "모든 멤버가 나가기전까지는 땅을 삭제할 수 없습니다.".errorFormat())
                } else bungeeApi.sendMessage(hostName, "해당 명령어는 땅 소유자만 사용가능합니다.".errorFormat())
            }
        }
    }
}