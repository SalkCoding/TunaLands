package com.salkcoding.tunalands.commands.debug

import com.salkcoding.tunalands.displayManager
import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.Lands
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.recommendManager
import com.salkcoding.tunalands.util.errorFormat
import com.salkcoding.tunalands.util.infoFormat
import com.salkcoding.tunalands.util.times
import com.salkcoding.tunalands.util.warnFormat
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
                        "visitor" -> {
                            lands.memberMap[uuid] = Lands.MemberData(
                                uuid,
                                Rank.valueOf(rank),
                                System.currentTimeMillis(),
                                System.currentTimeMillis()
                            )
                            sender.sendMessage("${name}을/를 ${rank}로/으로 설정했습니다.".infoFormat())
                        }
                        "null" -> {
                            lands.memberMap.remove(uuid)
                            sender.sendMessage("${name}을/를 멤버 목록에서 제거했습니다.".infoFormat())
                        }
                        else -> return false
                    }
                } else sender.sendMessage("대상이 존재하지 않습니다.".infoFormat())
                return true
            }
            args[0] == "timeset" && args.size == 3 -> {
                val targetUUID = Bukkit.getPlayerUniqueId(args[1])
                if (targetUUID != null) {
                    val lands = landManager.getPlayerLands(targetUUID, Rank.OWNER)
                    if (lands != null) {
                        try {
                            val milliSeconds = args[2].toLong()
                            if (milliSeconds <= 0) {
                                lands.sendMessageToOnlineMembers(
                                    listOf(
                                        "관리자에의해 땅이 비활성화 상태로 전환됩니다!".warnFormat(),
                                        "코어에 연료를 넣어 활성화하지 않을 경우 모든 블럭과의 상호작용이 불가능합니다!".warnFormat()
                                    )
                                )
                                displayManager.pauseDisplay(lands)
                                lands.enable = false
                            } else {
                                if (lands.enable) lands.expiredMillisecond += milliSeconds
                                else {
                                    lands.expiredMillisecond = System.currentTimeMillis() + milliSeconds
                                    lands.enable = true
                                    displayManager.resumeDisplay(lands)
                                    lands.sendMessageToOnlineMembers("땅이 다시 활성화되었습니다!".infoFormat())
                                }
                                lands.sendMessageToOnlineMembers("관리자에의해 땅 보호 시간이 변경되었습니다.".infoFormat())
                            }
                        } catch (e: NumberFormatException) {
                            sender.sendMessage("${args[2]}은/는 숫자가 아닙니다.".errorFormat())
                        }
                    } else sender.sendMessage("소유한 땅이 없습니다.".infoFormat())
                } else sender.sendMessage("존재하지 않는 플레이어입니다.".errorFormat())
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
            args[0] == "reset" && args.size == 4 -> {
                when (args[1]) {
                    "recommend" -> {
                        val target = Bukkit.getOfflinePlayer(args[2])
                        val lands = landManager.getPlayerLands(target.uniqueId, Rank.OWNER)
                        if (lands != null) {
                            lands.recommend = args[3].toInt()
                            sender.sendMessage("${lands.landsName}의 추천 수를 초기화하였습니다.".infoFormat())
                        } else sender.sendMessage("소속된 곳이 없습니다".infoFormat())
                    }
                }
                return true
            }
            args[0] == "reset" && args.size == 3 -> {
                when (args[1]) {
                    "cooldown" -> {
                        val target = Bukkit.getOfflinePlayer(args[2])
                        recommendManager.resetMilliseconds(target.uniqueId)
                        sender.sendMessage("해당 유저의 추천 쿨타임을 초기화하였습니다.".infoFormat())
                    }
                }
                return true
            }
            args[0] == "delete" && args.size == 2 -> {
                val target = Bukkit.getOfflinePlayer(args[1])
                val lands = landManager.getPlayerLands(target.uniqueId, Rank.OWNER)
                if (lands != null) {
                    landManager.deleteLands(target)
                    sender.sendMessage("${target.name}의 땅을 삭제했습니다.".infoFormat())
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