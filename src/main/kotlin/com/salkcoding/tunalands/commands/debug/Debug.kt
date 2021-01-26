package com.salkcoding.tunalands.commands.debug

import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.data.lands.Lands
import com.salkcoding.tunalands.data.lands.Rank
import com.salkcoding.tunalands.util.errorFormat
import com.salkcoding.tunalands.util.infoFormat
import com.salkcoding.tunalands.util.times
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Debug : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!sender.isOp || args.isEmpty())
            return false

        when {
            args[0] == "set" && args.size == 4 -> {
                val target = args[1]
                val targetUUID = Bukkit.getOfflinePlayer(target).uniqueId
                val name = args[2]
                val uuid = Bukkit.getOfflinePlayer(name).uniqueId
                val lands = landManager.getPlayerLands(targetUUID, Rank.OWNER)
                if (lands != null) {
                    when (val rank = args[3]) {
                        "owner",
                        "delegator",
                        "member",
                        "parttimejob",
                        "visitor" -> lands.memberMap[uuid] = Lands.MemberData(
                            uuid,
                            Rank.valueOf(rank),
                            System.currentTimeMillis(),
                            System.currentTimeMillis()
                        )
                        "null" -> lands.memberMap.remove(uuid)
                        else -> return false
                    }
                } else sender.sendMessage("대상이 존재하지 않습니다.".infoFormat())
                return true
            }
            args[0] == "info" && args.size == 2 -> {
                val name = args[1]
                val list = landManager.getPlayerLandList(Bukkit.getOfflinePlayer(name).uniqueId)
                if (list != null) sender.sendMessage("$name 소유의 땅 목록: ${list.joinToString(separator = ", ")}".infoFormat())
                else sender.sendMessage("소유한 땅이 없습니다.".infoFormat())
                return true
            }
            args[0] == "player" && args.size == 2 -> {
                val name = args[1]
                val uuid = Bukkit.getOfflinePlayer(name).uniqueId
                val list = landManager.getPlayerLandsList(uuid)
                if (list.isNotEmpty()) {
                    sender.sendMessage("${name}의 정보".infoFormat())
                    list.forEach {
                        sender.sendMessage("${it.ownerName}의 땅 ${it.memberMap[uuid]!!.rank}".infoFormat())
                    }
                } else sender.sendMessage("소속된 곳이 없습니다".infoFormat())
                return true
            }
            args[0] == "visit" && args.size == 2 -> {
                if (sender !is Player) {
                    sender.sendMessage("콘솔에서는 사용할 수 없습니다.".errorFormat())
                    return true
                }
                val name = args[1]
                val uuid = Bukkit.getOfflinePlayer(name).uniqueId
                val lands = landManager.getPlayerLands(uuid, Rank.OWNER)
                if (lands != null) {
                    sender.teleportAsync(lands.memberSpawn)
                    sender.sendMessage("${name}의 땅으로 이동했습니다.".infoFormat())
                } else sender.sendMessage("소속된 곳이 없습니다".infoFormat())
                return true
            }
            args[0] == "buy" && args.size == 1 -> {
                val player = sender as? Player
                if (player != null) {
                    landManager.buyLand(player, (Material.APPLE * 1), player.location.block)
                } else {
                    sender.sendMessage("콘솔에서는 사용 불가능한 명령어입니다.".errorFormat())
                }
                return true
            }
            args[0] == "sell" && args.size == 1 -> {
                val player = sender as? Player
                if (player != null) {
                    landManager.sellLand(player, (Material.APPLE * 1), player.location.block)
                } else {
                    sender.sendMessage("콘솔에서는 사용 불가능한 명령어입니다.".errorFormat())
                }
                return true
            }
            else -> return false
        }
    }
}