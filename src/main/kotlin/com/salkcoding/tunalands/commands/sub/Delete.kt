package com.salkcoding.tunalands.commands.sub

import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.util.errorFormat
import com.salkcoding.tunalands.util.infoFormat
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Delete : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        when {
            label == "delete" && args.isEmpty() -> {
                val player = sender as? Player
                if (player != null) {
                    val lands = landManager.getPlayerLands(player.uniqueId)
                    if (lands != null) {
                        val data = lands.memberMap[player.uniqueId]!!
                        if (data.rank == Rank.OWNER) {
                            if (lands.memberMap.size == 1) {
                                val upCore = lands.upCore
                                val downCore = lands.downCore
                                //Destroy core naturally
                                upCore.block.breakNaturally()
                                downCore.block.breakNaturally()

                                landManager.deleteLands(player)
                                player.sendMessage("땅을 삭제했습니다.".infoFormat())
                            } else player.sendMessage("모든 멤버가 나가기전까지는 땅을 삭제할 수 없습니다.".errorFormat())
                        } else player.sendMessage("권한이 없습니다.".errorFormat())
                    } else player.sendMessage("해당 명령어는 땅 소유자와 관리 대리인만 사용가능합니다.".errorFormat())
                } else sender.sendMessage("콘솔에서는 사용할 수 없는 명령어입니다.".errorFormat())
                return true
            }
        }
        return false
    }
}