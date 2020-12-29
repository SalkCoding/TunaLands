package com.salkcoding.tunalands.commands.sub

import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.util.errorFormat
import com.salkcoding.tunalands.util.infoFormat
import com.salkcoding.tunalands.util.warnFormat
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Leave : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        when {
            label == "leave" && args.isEmpty() -> {
                val player = sender as? Player
                if (player != null) {
                    val lands = landManager.getPlayerLands(player.uniqueId)
                    if (lands != null) {
                        val data = lands.memberMap[player.uniqueId]!!
                        if (data.rank == Rank.OWNER) {
                            player.sendMessage("소유자는 땅을 삭제하기전에는 땅에서 나갈 수 없습니다.".errorFormat())
                            return true
                        }

                        lands.memberMap.remove(player.uniqueId)

                        player.sendMessage("${lands.ownerName}의 땅을 떠났습니다.".infoFormat())
                        lands.memberMap.forEach { (uuid, _) ->
                            val administration = Bukkit.getPlayer(uuid) ?: return@forEach
                            administration.sendMessage("${player.name}이/가 땅을 떠났습니다.".warnFormat())
                        }
                    } else player.sendMessage("해당 명령어는 땅 소유자와 관리 대리인만 사용가능합니다.".errorFormat())
                } else sender.sendMessage("콘솔에서는 사용할 수 없는 명령어입니다.".errorFormat())
                return true
            }
        }
        return false
    }
}