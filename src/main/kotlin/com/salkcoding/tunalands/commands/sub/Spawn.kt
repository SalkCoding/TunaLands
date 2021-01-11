package com.salkcoding.tunalands.commands.sub

import com.salkcoding.tunalands.configuration
import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.util.TeleportCooltime
import com.salkcoding.tunalands.util.errorFormat
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
                    val lands = landManager.getPlayerLands(player.uniqueId, Rank.OWNER, Rank.DELEGATOR, Rank.MEMBER)
                    if (lands != null) {
                        val data = lands.memberMap[uuid]!!
                        val spawn = when (data.rank) {
                            Rank.OWNER, Rank.DELEGATOR, Rank.MEMBER -> lands.memberSpawn
                            else -> lands.visitorSpawn
                        }
                        TeleportCooltime.addPlayer(player, spawn, configuration.command.spawnCooldown, null, false)
                    } else player.sendMessage("해당 명령어는 땅의 소유자, 관리 대리인, 멤버만 사용가능합니다.".errorFormat())
                } else sender.sendMessage("콘솔에서는 사용할 수 없는 명령어입니다.".errorFormat())
                return true
            }
        }
        return false
    }
}