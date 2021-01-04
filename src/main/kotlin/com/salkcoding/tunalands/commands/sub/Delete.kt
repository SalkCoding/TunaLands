package com.salkcoding.tunalands.commands.sub

import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.tunaLands
import com.salkcoding.tunalands.util.errorFormat
import com.salkcoding.tunalands.util.infoFormat
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.io.File

class Delete : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        when {
            label == "delete" && args.isEmpty() -> {
                val player = sender as? Player
                if (player != null) {
                    val lands = landManager.getPlayerLands(player.uniqueId, Rank.OWNER)
                    if (lands != null) {
                        if (lands.memberMap.size == 1) {
                            val upCore = lands.upCore
                            val downCore = lands.downCore
                            //Destroy core naturally
                            upCore.block.breakNaturally()
                            downCore.block.breakNaturally()

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
                } else sender.sendMessage("콘솔에서는 사용할 수 없는 명령어입니다.".errorFormat())
                return true
            }
        }
        return false
    }
}