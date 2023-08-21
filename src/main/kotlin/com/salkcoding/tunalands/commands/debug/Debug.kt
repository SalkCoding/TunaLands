package com.salkcoding.tunalands.commands.debug

import com.salkcoding.tunalands.*
import com.salkcoding.tunalands.lands.LandType
import com.salkcoding.tunalands.lands.Lands
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.util.errorFormat
import com.salkcoding.tunalands.util.infoFormat
import com.salkcoding.tunalands.util.times
import com.salkcoding.tunalands.util.warnFormat
import org.bukkit.Bukkit
import org.bukkit.ChatColor
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
                    when (val rank = args[3].lowercase()) {
                        "owner",
                        "delegator",
                        "member",
                        "parttimejob",
                        "visitor" -> {
                            lands.memberMap[uuid] = Lands.MemberData(
                                uuid,
                                Rank.valueOf(rank.uppercase()),
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

            args[0] == "setfuel" && args.size == 3 -> {
                val targetUUID = Bukkit.getPlayerUniqueId(args[1])
                if (targetUUID != null) {
                    val lands = landManager.getPlayerLands(targetUUID, Rank.OWNER)
                    if (lands != null) {
                        try {
                            val numOfFuel = args[2].toLong()
                            if (numOfFuel <= 0) {
                                lands.sendMessageToOnlineMembers(
                                    listOf(
                                        "관리자에의해 땅이 비활성화 상태로 전환됩니다!".warnFormat(),
                                        "코어에 연료를 넣어 활성화하지 않을 경우 모든 블럭과의 상호작용이 불가능합니다!".warnFormat()
                                    )
                                )
                                displayManager.pauseDisplay(lands)?.setMessage(
                                    "${ChatColor.RED}비활성화 ${ChatColor.WHITE}상태",
                                    "${ChatColor.GOLD}연료${ChatColor.WHITE}를 사용하여 ${ChatColor.GREEN}재활성화 ${ChatColor.WHITE}해야합니다!"
                                )
                                lands.enable = false
                            } else {
                                lands.fuelLeft = numOfFuel
                                if (!lands.enable) {
                                    lands.enable = true
                                    displayManager.resumeDisplay(lands)?.update()
                                    lands.sendMessageToOnlineMembers("땅이 다시 활성화되었습니다!".infoFormat())
                                }
                                lands.sendMessageToOnlineMembers("관리자에의해 땅 연료 갯수가 변경되었습니다.".infoFormat())
                            }
                        } catch (e: NumberFormatException) {
                            sender.sendMessage("${args[2]}은/는 숫자가 아닙니다.".errorFormat())
                        }
                    } else sender.sendMessage("소유한 땅이 없습니다.".infoFormat())
                } else sender.sendMessage("존재하지 않는 플레이어입니다.".errorFormat())
                return true
            }

            args[0] == "info" && args.size == 2 -> {
                val uuid = bukkitLinkedAPI.getPlayerInfo(args[1]).playerUUID
                val lands = landManager.getPlayerLands(uuid)
                if(lands == null){
                    sender.sendMessage("소속된 땅이 없습니다.".warnFormat())
                    return true
                }

                sender.sendMessage("땅 정보: $lands".infoFormat())
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

                    "rejoin" -> {
                        val target = Bukkit.getOfflinePlayer(args[2])
                        leftManager.resetMilliseconds(target.uniqueId)
                        sender.sendMessage("해당 유저의 재가입 쿨타임을 초기화하였습니다.".infoFormat())
                    }
                }
                return true
            }

            args[0] == "delete" && args.size == 2 -> {
                val target = Bukkit.getOfflinePlayer(args[1])
                val lands = landManager.getPlayerLands(target.uniqueId, Rank.OWNER)
                if (lands != null) {
                    landManager.deleteLands(lands, true)
                    sender.sendMessage("${target.name}의 땅을 삭제했습니다.".infoFormat())
                } else sender.sendMessage("소속된 곳이 없습니다".infoFormat())
                return true
            }

            args[0] == "buy" && args.size == 1 -> {
                val player = sender as? Player
                if (player != null) {
                    landManager.buyChunk(player, (Material.APPLE * 1), player.location.block)
                } else {
                    sender.sendMessage("콘솔에서는 사용 불가능한 명령어입니다.".errorFormat())
                }
                return true
            }

            args[0] == "forcebuy" && args.size == 2 -> {
                val player = sender as? Player
                val owner = Bukkit.getOfflinePlayerIfCached(args[1])

                if (owner == null) {
                    sender.sendMessage("존재하지 않는 플레이어입니다.".errorFormat())
                } else if (player == null) {
                    sender.sendMessage("콘솔에서는 사용 불가능한 명령어입니다.".errorFormat())
                } else {
                    landManager.buyLandByForceAsAdmin(player, owner, player.location.block)
                }
                return true
            }

            args[0] == "sell" && args.size == 1 -> {
                val player = sender as? Player
                if (player != null) {
                    landManager.sellChunk(player, (Material.APPLE * 1), player.location.block)
                } else {
                    sender.sendMessage("콘솔에서는 사용 불가능한 명령어입니다.".errorFormat())
                }
                return true
            }

            args[0] == "forcesell" && args.size == 2 -> {
                val player = sender as? Player
                val owner = Bukkit.getOfflinePlayerIfCached(args[1])

                if (owner == null) {
                    sender.sendMessage("존재하지 않는 플레이어입니다.".errorFormat())
                } else if (player == null) {
                    sender.sendMessage("콘솔에서는 사용 불가능한 명령어입니다.".errorFormat())
                } else {
                    landManager.sellLandByForceAsAdmin(player, owner, player.location.block)
                }
                return true
            }

            args[0] == "move" && args.size == 4 -> {
                val uuid = Bukkit.getPlayerUniqueId(args[1])
                val targetUUID = Bukkit.getPlayerUniqueId(args[2])
                if (uuid == null) {
                    sender.sendMessage("${args[1]}이라는 유저는 존재하지 않습니다!".errorFormat())
                    return true
                }
                if (targetUUID == null) {
                    sender.sendMessage("${args[2]}이라는 유저는 존재하지 않습니다!".errorFormat())
                    return true
                }

                val landList = landManager.getPlayerLandsList(uuid)
                if (landList.isNotEmpty()) {
                    landList.forEach { lands ->
                        val data = lands.memberMap[uuid]!!
                        if (data.rank != Rank.OWNER) lands.memberMap.remove(uuid)
                        else {
                            lands.memberMap.remove(uuid)
                            val list = lands.memberMap.values.toList()
                            list.sortedBy { it.rank }
                            landManager.changeChunksOwner(
                                Bukkit.getOfflinePlayer(uuid),
                                Bukkit.getOfflinePlayer(list.first().uuid)
                            )
                        }
                    }
                }
                val lands = landManager.getPlayerLands(targetUUID)!!
                val present = System.currentTimeMillis()
                lands.memberMap[uuid] = Lands.MemberData(uuid, Rank.valueOf(args[3].uppercase()), present, present)
                return true
            }

            args[0] == "check" && args[1] == "overflow" -> {
                landManager.getPlayerLandMap().filter { (_, lands) ->
                    lands.landMap.size >= configuration.protect.getMaxOccupied(lands).maxChunkAmount
                }.forEach { (_, lands) ->
                    sender.sendMessage(
                        "${lands.ownerName}의 땅, 현재 보유 청크: ${lands.landMap.size}/${
                            configuration.protect.getMaxOccupied(
                                lands
                            ).maxChunkAmount
                        }, 현재 보유 농작지: ${
                            lands.landMap.filter { (_, type) -> type == LandType.FARM }.size
                        }/${configuration.farm.limitOccupied}"
                    )
                }
                return true
            }

            else -> return false
        }
    }
}
