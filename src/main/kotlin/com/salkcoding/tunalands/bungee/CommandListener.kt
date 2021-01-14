package com.salkcoding.tunalands.bungee

import com.google.common.io.ByteStreams
import com.salkcoding.tunalands.bungeeApi
import com.salkcoding.tunalands.commands.sub.*
import com.salkcoding.tunalands.configuration
import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.Lands
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.tunaLands
import com.salkcoding.tunalands.util.consoleFormat
import com.salkcoding.tunalands.util.errorFormat
import com.salkcoding.tunalands.util.infoFormat
import io.github.leonardosnt.bungeechannelapi.BungeeChannelApi
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.IOException
import java.util.*
import kotlin.math.min

class CommandListener : BungeeChannelApi.ForwardConsumer {

    override fun accept(channel: String, receiver: Player, data: ByteArray) {
        //Sub channel
        val inMessage = ByteStreams.newDataInput(data)
        when (channel) {
            "tunalands-accept" -> {
                val uuid = UUID.fromString(inMessage.readUTF())
                Accept.work(uuid)
            }
            "tunalands-alba" -> {
                val uuid = UUID.fromString(inMessage.readUTF())
                Alba.work(uuid, inMessage.readUTF())
            }
            "tunalands-ban" -> {
                val uuid = UUID.fromString(inMessage.readUTF())
                Ban.work(uuid, inMessage.readUTF())
            }
            "tunalands-banlist" -> {
                Bukkit.getScheduler().runTaskAsynchronously(tunaLands, Runnable {
                    val uuid = UUID.fromString(inMessage.readUTF())
                    val name = inMessage.readUTF()
                    val lands = landManager.getPlayerLands(uuid, Rank.OWNER, Rank.DELEGATOR, Rank.MEMBER)
                    if (lands != null) {
                        val banMap = lands.banMap
                        val banList = banMap.keys.toList()
                        //Page is index, i is indices step 36
                        //Send list page by page
                        for (page in banList.indices step 36) {
                            val length = min(banList.size - page, 36)

                            val messageBytes = ByteArrayOutputStream()
                            val messageOut = DataOutputStream(messageBytes)
                            try {
                                messageOut.writeUTF(uuid.toString())
                                messageOut.writeInt(length)
                                for (i in page until (page + length)) {
                                    val banData = banMap[banList[i]]!!
                                    messageOut.writeUTF(banData.uuid.toString())
                                    messageOut.writeLong(banData.banned)
                                }
                            } catch (exception: IOException) {
                                exception.printStackTrace()
                            }

                            bungeeApi.forward("ALL", channel, messageBytes.toByteArray())
                        }
                    } else bungeeApi.sendMessage(name, "해당 명령어는 땅 소속만 사용가능합니다.".errorFormat())
                })
            }
            "tunalands-cancel" -> {
                val uuid = UUID.fromString(inMessage.readUTF())
                Cancel.work(uuid, inMessage.readUTF())
            }
            "tunalands-delete" -> {
                val uuid = UUID.fromString(inMessage.readUTF())
                Delete.work(uuid)
            }
            "tunalands-demote" -> {
                val uuid = UUID.fromString(inMessage.readUTF())
                Demote.work(uuid, inMessage.readUTF())
            }
            "tunalands-deny" -> {
                val uuid = UUID.fromString(inMessage.readUTF())
                Deny.work(uuid)
            }
            "tunalands-hego" -> {
                val uuid = UUID.fromString(inMessage.readUTF())
                Hego.work(uuid, inMessage.readUTF())
            }
            "tunalands-invite" -> {
                val uuid = UUID.fromString(inMessage.readUTF())
                Invite.work(uuid, inMessage.readUTF())
            }
            "tunalands-kick" -> {
                val uuid = UUID.fromString(inMessage.readUTF())
                Kick.work(uuid, inMessage.readUTF())
            }
            "tunalands-leave" -> {
                val uuid = UUID.fromString(inMessage.readUTF())
                Leave.work(uuid)
            }
            "tunalands-promote" -> {
                val uuid = UUID.fromString(inMessage.readUTF())
                Promote.work(uuid, inMessage.readUTF())
            }
            "tunalands-setleader" -> {
                val uuid = UUID.fromString(inMessage.readUTF())
                SetLeader.work(uuid, inMessage.readUTF())
            }
            "tunalands-spawn" -> {
                Bukkit.getScheduler().runTaskAsynchronously(tunaLands, Runnable {
                    val uuid = UUID.fromString(inMessage.readUTF())
                    val name = inMessage.readUTF()
                    val serverName = inMessage.readUTF()
                    val lands = landManager.getPlayerLands(uuid, Rank.OWNER, Rank.DELEGATOR, Rank.MEMBER)
                    if (lands != null) {
                        val messageBytes = ByteArrayOutputStream()
                        val messageOut = DataOutputStream(messageBytes)
                        try {
                            messageOut.writeUTF(uuid.toString())
                            messageOut.writeUTF(configuration.serverName)
                            messageOut.writeLong(configuration.command.spawnCooldown)
                        } catch (exception: IOException) {
                            exception.printStackTrace()
                        }

                        bungeeApi.forward(serverName, channel, messageBytes.toByteArray())
                    } else bungeeApi.sendMessage(name, "해당 명령어는 땅의 소유자, 관리 대리인, 멤버만 사용가능합니다.".errorFormat())
                })
            }
            "tunalands-spawn-teleport" -> {
                Bukkit.getScheduler().runTaskLater(tunaLands, Runnable {
                    val uuid = UUID.fromString(inMessage.readUTF())
                    val player = Bukkit.getPlayer(uuid)
                    if (player != null) {
                        val lands = landManager.getPlayerLands(uuid, Rank.OWNER, Rank.DELEGATOR, Rank.MEMBER)
                        if (lands != null) {
                            player.teleportAsync(lands.memberSpawn)
                        } else tunaLands.logger.warning("$uuid requested $channel but lands instance is null")
                    } else tunaLands.logger.warning("$uuid requested $channel but player instance is null")
                }, 15)
            }
            "tunalands-unban" -> {
                val uuid = UUID.fromString(inMessage.readUTF())
                Unban.work(uuid, inMessage.readUTF())
            }
            "tunalands-visit" -> {
                Bukkit.getScheduler().runTaskAsynchronously(tunaLands, Runnable {
                    val uuid = UUID.fromString(inMessage.readUTF())
                    val serverName = inMessage.readUTF()
                    val landMap = landManager.getPlayerLandMap()
                    val landList = landMap.keys.toList()
                    //Page is index, i is indices step 36
                    //Send list page by page
                    for (page in landList.indices step 36) {
                        val length = min(landList.size - page, 36)

                        val messageBytes = ByteArrayOutputStream()
                        val messageOut = DataOutputStream(messageBytes)
                        try {
                            messageOut.writeUTF(uuid.toString())
                            messageOut.writeInt(length)
                            for (i in page until (page + length)) {
                                val lands = landMap[landList[i]]!!
                                messageOut.writeUTF(lands.ownerUUID.toString())
                                messageOut.writeBoolean(lands.open)
                                messageOut.writeInt(lands.memberMap.size)
                                messageOut.writeLong(lands.landHistory.visitorCount)
                                messageOut.writeLong(lands.landHistory.createdMillisecond)
                                for (lore in lands.lore)
                                    messageOut.writeUTF(lore)
                            }
                        } catch (exception: IOException) {
                            exception.printStackTrace()
                        }

                        bungeeApi.forward(serverName, channel, messageBytes.toByteArray())
                    }
                })
            }
            "tunalands-visit-connect" -> {
                Bukkit.getScheduler().runTaskAsynchronously(tunaLands, Runnable {
                    val uuid = UUID.fromString(inMessage.readUTF())
                    val name = inMessage.readUTF()
                    val serverName = inMessage.readUTF()
                    val targetUUID = UUID.fromString(inMessage.readUTF())
                    val lands = landManager.getPlayerLands(targetUUID, Rank.OWNER)
                    if (lands != null) {
                        if (!lands.open) {
                            bungeeApi.sendMessage(name, "땅이 비공개 상태라 방문할 수 없습니다!".errorFormat())
                            return@Runnable
                        }

                        if (uuid in lands.memberMap) {
                            val rank = lands.memberMap[uuid]!!.rank
                            if (rank != Rank.PARTTIMEJOB) {
                                bungeeApi.sendMessage(name, "자신이 소속되어있는 땅에는 방문할 수 없습니다!".errorFormat())
                                return@Runnable
                            }
                        }

                        if (uuid in lands.memberMap) {
                            val rank = lands.memberMap[uuid]!!.rank
                            if (rank != Rank.PARTTIMEJOB) {
                                bungeeApi.sendMessage(name, "이미 방문 중입니다!".errorFormat())
                                return@Runnable
                            }
                        }

                        if (!lands.banMap.containsKey(uuid)) {
                            if (lands.open) {
                                val messageBytes = ByteArrayOutputStream()
                                val messageOut = DataOutputStream(messageBytes)
                                try {
                                    messageOut.writeUTF(uuid.toString())
                                    messageOut.writeUTF(targetUUID.toString())
                                    messageOut.writeUTF(configuration.serverName)
                                    messageOut.writeLong(configuration.command.visitCooldown)
                                } catch (exception: IOException) {
                                    exception.printStackTrace()
                                }
                                bungeeApi.forward(serverName, channel, messageBytes.toByteArray())
                            }
                        } else {
                            bungeeApi.sendMessage(name, "${lands.ownerName}의 땅에서 밴되었기 때문에 방문할 수 없습니다!".errorFormat())
                        }
                    } else bungeeApi.sendMessage(name, "해당 땅이 존재하지 않습니다.".errorFormat())
                })
            }
            "tunalands-visit-teleport" -> {
                Bukkit.getScheduler().runTaskLater(tunaLands, Runnable {
                    val uuid = UUID.fromString(inMessage.readUTF())
                    val player = Bukkit.getPlayer(uuid)
                    val targetUUID = UUID.fromString(inMessage.readUTF())
                    val lands = landManager.getPlayerLands(targetUUID, Rank.OWNER)
                    if (player != null) {
                        if (lands != null) {
                            val visitorSpawn = lands.visitorSpawn
                            player.teleportAsync(visitorSpawn)

                            lands.welcomeMessage.forEach {
                                player.sendMessage(it)
                            }

                            if (uuid in lands.memberMap) {
                                if (lands.memberMap[uuid]!!.rank == Rank.PARTTIMEJOB)
                                    return@Runnable
                            }

                            lands.landHistory.visitorCount++
                            val current = System.currentTimeMillis()
                            lands.memberMap[uuid] = Lands.MemberData(
                                uuid,
                                Rank.VISITOR,
                                current,
                                current
                            )

                            lands.memberMap.forEach { (uuid, _) ->
                                val member = Bukkit.getOfflinePlayer(uuid)
                                if (member.isOnline) {
                                    member.player!!.sendMessage("${player.name}님이 땅에 방문했습니다.".infoFormat())
                                } else {
                                    bungeeApi.sendMessage(
                                        member.name, "${player.name}님이 땅에 방문했습니다.".infoFormat()
                                    )
                                }
                            }
                        } else tunaLands.logger.warning("$uuid requested $channel but lands instance is null".consoleFormat())
                    } else tunaLands.logger.warning("$uuid requested $channel but player instance is null".consoleFormat())
                }, 15)
            }
        }
    }
}