package com.salkcoding.tunalands.bungee

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.stream.MalformedJsonException
import com.salkcoding.tunalands.*
import com.salkcoding.tunalands.commands.sub.*
import com.salkcoding.tunalands.lands.Lands
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.util.errorFormat
import com.salkcoding.tunalands.util.infoFormat
import fish.evatuna.metamorphosis.redis.MetamorphosisReceiveEvent
import me.baiks.bukkitlinked.api.TeleportResult
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.util.*

class CommandListener : Listener {

    companion object {
        val commandList = listOf(
            "accept",
            "alba",
            "ban",
            "banlist",
            "cancel",
            "delete",
            "demote",
            "deny",
            "hego",
            "invite",
            "kick",
            "leave",
            "promote",
            "recommend",
            "recommend_lands",
            "setleader",
            "spawn",
            "pending_spawn_teleport",
            "unban",
            "visit",
            "visit_specific",
            "visit_connect",
            "pending_visit_teleport",
        )
    }

    @EventHandler
    fun onReceived(event: MetamorphosisReceiveEvent) {
        if (!event.key.startsWith("com.salkcoding.tunalands")) return

        val command = event.key.split(".").last()
        if (!commandList.contains(command)) return

        lateinit var json: JsonObject
        try {
            val element = JsonParser.parseString(event.value)
            if (!element.isJsonObject) return

            json = JsonParser.parseString(event.value).asJsonObject
        } catch (e: MalformedJsonException) {
            tunaLands.logger.warning("${event.key} sent an object without transform to JSON object!")
        }

        val uuid = UUID.fromString(json["uuid"].asString)
        //Split a last sub key
        when (command) {
            "accept" -> {
                Accept.work(uuid)
            }

            "alba" -> {
                Alba.work(uuid, json["targetName"].asString)
            }

            "ban" -> {
                Ban.work(uuid, json["targetName"].asString)
            }

            "banlist" -> {
                Bukkit.getScheduler().runTaskAsynchronously(tunaLands, Runnable {
                    val name = json["name"].asString
                    val lands = landManager.getPlayerLands(uuid, Rank.OWNER, Rank.DELEGATOR, Rank.MEMBER)
                    if (lands != null) {
                        val sendJson = JsonObject().apply {
                            addProperty("uuid", uuid.toString())
                            addProperty("targetServerName", json["serverName"].asString)
                        }

                        val banArray = JsonArray()
                        lands.banMap.forEach { (targetUUID, banData) ->
                            val banJson = JsonObject().apply {
                                addProperty("targetUUID", targetUUID.toString())
                                addProperty("banned", banData.banned)
                            }
                            banArray.add(banJson)
                        }

                        sendJson.add("banArray", banArray)
                        metamorphosis.send("com.salkcoding.tunalands.response_banlist", sendJson.toString())
                    } else bukkitLinkedAPI.sendMessageAcrossServer(name, "해당 명령어는 땅 소속만 사용가능합니다.".errorFormat())
                })
            }

            "cancel" -> {
                Cancel.work(uuid, json["targetName"].asString)
            }

            "delete" -> {
                Delete.work(uuid)
            }

            "demote" -> {
                Demote.work(uuid, json["targetName"].asString)
            }

            "deny" -> {
                Deny.work(uuid)
            }

            "hego" -> {
                Hego.work(uuid, json["targetName"].asString)
            }

            "invite" -> {
                Invite.work(uuid, json["targetName"].asString)
            }

            "kick" -> {
                Kick.work(uuid, json["targetName"].asString)
            }

            "leave" -> {
                Leave.work(uuid)
            }

            "promote" -> {
                Promote.work(uuid, json["targetName"].asString)
            }

            "recommend" -> {
                Bukkit.getScheduler().runTaskAsynchronously(tunaLands, Runnable {
                    val sendJson = JsonObject().apply {
                        addProperty("uuid", uuid.toString())
                        addProperty("targetServerName", json["serverName"].asString)
                    }
                    val visitArray = JsonArray()
                    val landMap = landManager.getPlayerLandMap()
                    landMap.forEach { (_, lands) ->
                        val visitJson = JsonObject().apply {
                            addProperty("ownerUUID", lands.ownerUUID.toString())
                            addProperty("open", lands.open)
                            addProperty("memberSize", lands.memberMap.size)
                            addProperty("visitorCount", lands.landHistory.visitorCount)
                            addProperty("createdMillisecond", lands.landHistory.createdMillisecond)
                            addProperty("recommend", lands.recommend)
                            addProperty("landsName", lands.landsName)
                            add("lore", JsonArray().apply {
                                lands.lore.forEach {
                                    this.add(it)
                                }
                            })
                        }
                        visitArray.add(visitJson)
                    }
                    sendJson.add("recommendArray", visitArray)
                    metamorphosis.send("com.salkcoding.tunalands.response_recommend", sendJson.toString())
                })
            }

            "recommend_lands" -> {
                val name = json["name"].asString
                val ownerUUID = UUID.fromString(json["ownerUUID"].asString)

                val lands = landManager.getPlayerLands(ownerUUID, Rank.OWNER)
                if (lands != null) {
                    if (recommendManager.canRecommend(uuid)) {
                        recommendManager.recommend(uuid, lands)
                        bukkitLinkedAPI.sendMessageAcrossServer(name, "${lands.landsName}을/를 추천했습니다!".infoFormat())
                    } else {
                        val remain = recommendManager.remainMilliseconds(uuid)
                        val hours = (remain / 3600000) % 24
                        val minutes = (remain / 60000) % 60
                        val seconds = (remain / 1000) % 60
                        bukkitLinkedAPI.sendMessageAcrossServer(
                            name,
                            when {
                                hours > 0 -> "${hours}시간 ${minutes}분 ${seconds}초 후 다시 추천할 수 있습니다.".errorFormat()
                                minutes > 0 -> "${minutes}분 ${seconds}초 후 다시 추천할 수 있습니다.".errorFormat()
                                seconds > 0 -> "${seconds}초 후 다시 추천할 수 있습니다.".errorFormat()
                                else -> ""
                            }
                        )
                    }
                } else bukkitLinkedAPI.sendMessageAcrossServer(name, "해당 땅이 존재하지 않습니다.".errorFormat())
            }

            "setleader" -> {
                SetLeader.work(uuid, json["targetName"].asString)
            }

            "spawn" -> {
                Bukkit.getScheduler().runTaskAsynchronously(tunaLands, Runnable {
                    val name = json["name"].asString
                    val lands = landManager.getPlayerLands(uuid, Rank.OWNER, Rank.DELEGATOR, Rank.MEMBER)
                    if (lands != null) {
                        val sendJson = JsonObject().apply {
                            addProperty("uuid", uuid.toString())
                            addProperty("spawnCooldown", configuration.commandCooldown.spawnCooldown)
                            addProperty("targetServerName", json["serverName"].asString)
                            addProperty("spawnServerName", currentServerName)
                        }
                        metamorphosis.send("com.salkcoding.tunalands.response_spawn", sendJson.toString())
                    } else bukkitLinkedAPI.sendMessageAcrossServer(
                        name,
                        "해당 명령어는 땅의 소유자, 관리 대리인, 멤버만 사용가능합니다.".errorFormat()
                    )
                })
            }

            "pending_spawn_teleport" -> {
                val lands = landManager.getPlayerLands(uuid, Rank.OWNER, Rank.DELEGATOR, Rank.MEMBER)
                if (lands != null) {
                    //Remove visitor data
                    val previousLands = landManager.getPlayerLands(uuid, Rank.VISITOR)
                    if (previousLands != null) {
                        previousLands.memberMap.remove(uuid)
                        bukkitLinkedAPI.sendMessageAcrossServer(
                            uuid,
                            "${previousLands.landsName}을/를 떠났습니다.".infoFormat()
                        )
                    }
                    val name = json["name"].asString
                    val memberSpawn = lands.memberSpawn
                    Bukkit.getScheduler().runTaskAsynchronously(tunaLands, Runnable {
                        val result = bukkitLinkedAPI.teleport(
                            json["name"].asString,
                            currentServerName,
                            memberSpawn.world.name,
                            memberSpawn.x.toInt(),
                            memberSpawn.y.toInt(),
                            memberSpawn.z.toInt()
                        )
                        if (result != TeleportResult.TELEPORT_STARTED) {
                            tunaLands.logger.warning("$name teleport to lands spawn fail!: $result")
                        }
                    })
                } else tunaLands.logger.warning("$uuid requested ${event.uniqueId}:${event.key}:${event.value} but lands instance is null")
            }

            "unban" -> {
                Unban.work(uuid, json["targetName"].asString)
            }

            "visit" -> {
                Bukkit.getScheduler().runTaskAsynchronously(tunaLands, Runnable {
                    val sendJson = JsonObject().apply {
                        addProperty("uuid", uuid.toString())
                        addProperty("targetServerName", json["serverName"].asString)
                    }
                    val visitArray = JsonArray()
                    val landMap = landManager.getPlayerLandMap()
                    landMap.forEach { (_, lands) ->
                        val visitJson = JsonObject().apply {
                            addProperty("ownerUUID", lands.ownerUUID.toString())
                            addProperty("open", lands.open)
                            addProperty("memberSize", lands.memberMap.size)
                            addProperty("visitorCount", lands.landHistory.visitorCount)
                            addProperty("createdMillisecond", lands.landHistory.createdMillisecond)
                            addProperty("recommend", lands.recommend)
                            addProperty("landsName", lands.landsName)
                            add("lore", JsonArray().apply {
                                lands.lore.forEach {
                                    this.add(it)
                                }
                            })
                        }
                        visitArray.add(visitJson)
                    }
                    sendJson.add("visitArray", visitArray)
                    metamorphosis.send("com.salkcoding.tunalands.response_visit", sendJson.toString())
                })
            }

            "visit_connect" -> {
                Bukkit.getScheduler().runTaskAsynchronously(tunaLands, Runnable {
                    val name = json["name"].asString
                    val isOp = json["isOp"].asBoolean
                    val ownerUUID = UUID.fromString(json["ownerUUID"].asString)
                    val lands = landManager.getPlayerLands(ownerUUID, Rank.OWNER)
                    if (lands != null) {
                        if (!lands.open && !isOp) {
                            bukkitLinkedAPI.sendMessageAcrossServer(name, "땅이 비공개 상태라 방문할 수 없습니다!".errorFormat())
                            return@Runnable
                        }

                        if (uuid in lands.memberMap) {
                            val rank = lands.memberMap[uuid]!!.rank
                            if (rank != Rank.PARTTIMEJOB) {
                                bukkitLinkedAPI.sendMessageAcrossServer(
                                    name,
                                    "자신이 소속되어있는 땅에는 방문할 수 없습니다!".errorFormat()
                                )
                                return@Runnable
                            }
                        }

                        if (uuid in lands.memberMap) {
                            val rank = lands.memberMap[uuid]!!.rank
                            if (rank != Rank.PARTTIMEJOB) {
                                bukkitLinkedAPI.sendMessageAcrossServer(name, "이미 방문 중입니다!".errorFormat())
                                return@Runnable
                            }
                        }

                        if (!lands.banMap.containsKey(uuid)) {
                            val sendJson = JsonObject().apply {
                                addProperty("uuid", uuid.toString())
                                addProperty("ownerUUID", ownerUUID.toString())
                                addProperty("targetServerName", json["serverName"].asString)
                                addProperty("visitServerName", currentServerName)
                                addProperty("visitCooldown", configuration.commandCooldown.visitCooldown)
                            }

                            metamorphosis.send("com.salkcoding.tunalands.response_visit_connect", sendJson.toString())
                        } else {
                            bukkitLinkedAPI.sendMessageAcrossServer(
                                name,
                                "${lands.ownerName}의 땅에서 밴되었기 때문에 방문할 수 없습니다!".errorFormat()
                            )
                        }
                    } else bukkitLinkedAPI.sendMessageAcrossServer(name, "해당 땅이 존재하지 않습니다.".errorFormat())
                })
            }

            "pending_visit_teleport" -> {
                val name = json["name"].asString
                val targetUUID = UUID.fromString(json["ownerUUID"].asString)
                val lands = landManager.getPlayerLands(targetUUID, Rank.OWNER)
                if (lands != null) {
                    //Remove visitor data
                    val previousLands = landManager.getPlayerLands(uuid, Rank.VISITOR)
                    if (previousLands != null) {
                        previousLands.memberMap.remove(uuid)
                        bukkitLinkedAPI.sendMessageAcrossServer(
                            name,
                            "${previousLands.landsName}의 땅을 떠났습니다.".infoFormat()
                        )
                    }

                    lands.welcomeMessage.forEach {
                        bukkitLinkedAPI.sendMessageAcrossServer(name, it)
                    }

                    if (uuid in lands.memberMap) {
                        if (lands.memberMap[uuid]!!.rank == Rank.PARTTIMEJOB)
                            return
                    }

                    lands.landHistory.visitorCount += 1
                    val current = System.currentTimeMillis()
                    lands.memberMap[uuid] = Lands.MemberData(
                        uuid,
                        Rank.VISITOR,
                        current,
                        current
                    )

                    lands.memberMap.keys.forEach { memberUUID ->
                        val member = Bukkit.getOfflinePlayer(memberUUID)
                        if (member.isOnline) {
                            member.player!!.sendMessage("${name}님이 땅에 방문했습니다.".infoFormat())
                        } else {
                            bukkitLinkedAPI.sendMessageAcrossServer(
                                member.name, "${name}님이 땅에 방문했습니다.".infoFormat()
                            )
                        }
                    }

                    val spawn = lands.visitorSpawn
                    bukkitLinkedAPI.teleport(
                        name,
                        currentServerName,
                        spawn.world.name,
                        spawn.x.toInt(),
                        spawn.y.toInt(),
                        spawn.z.toInt()
                    )
                } else tunaLands.logger.warning("$uuid requested ${event.uniqueId}:${event.key}:${event.value} but lands instance is null")
            }

            "visit_specific" -> {
                val ownerName = json["ownerName"].asString
                val ownerUUID = Bukkit.getPlayerUniqueId(ownerName)
                if (ownerUUID == null) {
                    bukkitLinkedAPI.sendMessageAcrossServer(uuid, "${ownerName}이라는 유저가 존재하지 않습니다.".errorFormat())
                    return
                }

                val lands = landManager.getPlayerLands(ownerUUID, Rank.OWNER)
                if (lands == null) {
                    bukkitLinkedAPI.sendMessageAcrossServer(uuid, "${ownerName}이라는 유저는 땅을 소유하고 있지 않습니다.".errorFormat())
                    return
                }

                if (!lands.enable) {
                    bukkitLinkedAPI.sendMessageAcrossServer(uuid, "비활성화된 땅은 방문할 수 없습니다.".errorFormat())
                    return
                }

                if (!lands.open) {
                    bukkitLinkedAPI.sendMessageAcrossServer(uuid, "비공개 설정된 땅은 방문할 수 없습니다.".errorFormat())
                    return
                }

                if (uuid in lands.memberMap) {
                    val rank = lands.memberMap[uuid]!!.rank
                    if (rank != Rank.PARTTIMEJOB && rank != Rank.VISITOR) {
                        bukkitLinkedAPI.sendMessageAcrossServer(uuid, "자신이 소속되어있는 땅에는 방문할 수 없습니다!".errorFormat())
                        return
                    }
                }

                if (uuid in lands.banMap) {
                    bukkitLinkedAPI.sendMessageAcrossServer(
                        uuid,
                        "${ChatColor.GREEN}${lands.ownerName}${ChatColor.WHITE}의 땅에서 밴되었기 때문에 방문할 수 없습니다!".errorFormat()
                    )
                    return
                }

                val sendJson = JsonObject().apply {
                    addProperty("uuid", uuid.toString())
                    addProperty("ownerUUID", lands.ownerUUID.toString())
                    addProperty("targetServerName", json["serverName"].asString)
                    addProperty("visitCooldown", configuration.commandCooldown.visitCooldown)
                }
                metamorphosis.send("com.salkcoding.tunalands.response_visit_specific", sendJson.toString())
            }
        }
    }
}