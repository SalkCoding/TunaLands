package com.salkcoding.tunalands.commands.sub

import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.Lands
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.util.errorFormat
import com.salkcoding.tunalands.util.infoFormat
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Spawn : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        when {
            label == "spawn" && args.isEmpty() -> {
                val player = sender as? Player
                if (player != null) {
                    val uuid = player.uniqueId
                    val lands = landManager.getPlayerLands(uuid)
                    if (lands != null) {
                        val data = lands.memberMap[uuid]!!
                        when (data.rank) {
                            Rank.OWNER, Rank.DELEGATOR, Rank.MEMBER -> player.teleportAsync(lands.memberSpawn)
                            else -> player.teleportAsync(lands.visitorSpawn)
                        }
                        //TODO Cooldown system
                    } else player.sendMessage("해당 명령어는 땅 소유자와 관리 대리인만 사용가능합니다.".errorFormat())
                } else sender.sendMessage("콘솔에서는 사용할 수 없는 명령어입니다.".errorFormat())
                return true
            }
        }
        return false
    }
}