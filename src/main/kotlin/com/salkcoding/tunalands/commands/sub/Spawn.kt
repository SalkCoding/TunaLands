package com.salkcoding.tunalands.commands.sub

import com.salkcoding.tunalands.bungeeApi
import com.salkcoding.tunalands.configuration
import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.tunaLands
import com.salkcoding.tunalands.util.TeleportCooltime
import com.salkcoding.tunalands.util.errorFormat
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.IOException
import java.util.*

class Spawn : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        when {
            label == "spawn" && args.isEmpty() -> {
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
            val player = Bukkit.getOfflinePlayer(uuid)
            work(player)
        }

        private fun work(offlinePlayer: OfflinePlayer) {
            if (offlinePlayer.isOnline) {
                val player = offlinePlayer.player!!
                val uuid = player.uniqueId
                val lands = landManager.getPlayerLands(player.uniqueId, Rank.OWNER, Rank.DELEGATOR, Rank.MEMBER)
                if (lands != null) {
                    val data = lands.memberMap[uuid]!!
                    val spawn = when (data.rank) {
                        Rank.OWNER, Rank.DELEGATOR, Rank.MEMBER -> lands.memberSpawn
                        else -> lands.visitorSpawn
                    }
                    player.teleportAsync(spawn)
                    //TeleportCooltime.addPlayer(player,spawn, configuration.command.spawnCooldown,null,false)
                } else player.sendMessage("해당 명령어는 땅 소유자와 관리 대리인만 사용가능합니다.".errorFormat())
            } else {
                val uuid = offlinePlayer.uniqueId
                val lands = landManager.getPlayerLands(offlinePlayer.uniqueId, Rank.OWNER, Rank.DELEGATOR, Rank.MEMBER)
                if (lands != null) {
                    val data = lands.memberMap[uuid]!!
                    val spawn = when (data.rank) {
                        Rank.OWNER, Rank.DELEGATOR, Rank.MEMBER -> lands.memberSpawn
                        else -> lands.visitorSpawn
                    }

                    bungeeApi.connectOther(offlinePlayer.name, "")
                    Bukkit.getScheduler().runTaskLater(tunaLands, Runnable {
                        if (offlinePlayer.isOnline) {
                            offlinePlayer.player!!.teleportAsync(spawn)
                        }
                    }, 50)
                    //TeleportCooltime.addPlayer(player,spawn, configuration.command.spawnCooldown,null,false)
                } else bungeeApi.sendMessage(offlinePlayer.name, "해당 명령어는 땅 소유자와 관리 대리인만 사용가능합니다.".errorFormat())
            }
        }
    }
}