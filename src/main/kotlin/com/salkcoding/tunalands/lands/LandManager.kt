package com.salkcoding.tunalands.lands

import com.salkcoding.tunalands.*
import com.salkcoding.tunalands.io.JsonReader
import com.salkcoding.tunalands.io.JsonWriter
import com.salkcoding.tunalands.util.*
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.io.File
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.roundToLong

class LandManager {

    private class FuelConsumeRunnable(val playerLandMap: ConcurrentHashMap<UUID, Lands>) : Runnable {
        override fun run() {
            playerLandMap.forEach { (_, lands) ->
                val timeToConsumeFuel = lands.nextTimeFuelNeedsToBeConsumed
                val present = LocalDateTime.now()

                if (lands.enable && present.isAfter(timeToConsumeFuel)) {
                    // 새롭게 연료를 소비해야되는 시간이 됨

                    if (lands.fuelLeft > 0) {
                        // minutesPerFuel 이 소숫점일 수도 있어서 밀리초로 변환 후 적용합니다.
                        // 예: minutesPerFuel 이 0.01 일 경우, 연료 하나당 0.01분을 커버해줍니다.
                        // => 0.01분 = 0.6초 = 600밀리초
                        val secondsPerFuel = configuration.fuel.getFuelRequirement(lands).secondsPerFuel
                        val msPerFuel = (secondsPerFuel * 1000).roundToLong()

                        lands.nextTimeFuelNeedsToBeConsumed = present.plus(msPerFuel, ChronoUnit.MILLIS)
                        lands.fuelLeft--
                    } else {
                        lands.sendMessageToOnlineMembers(
                            listOf(
                                "땅 보호 기간이 만료되어 비활성화 상태로 전환됩니다!".warnFormat(),
                                "코어에 연료를 넣어 활성화하지 않을 경우 모든 블럭과의 상호작용이 불가능합니다!".warnFormat()
                            )
                        )
                        displayManager.pauseDisplay(lands)
                        lands.enable = false
                    }
                } else if (!lands.enable) {
                    displayManager.pauseDisplayIfNotPaused(lands)
                }
            }
        }
    }

    private val landMap = ConcurrentHashMap<String, Lands.ChunkInfo>()
    private val playerLandMap = JsonReader.loadPlayerLandMap()
    private val task = Bukkit.getScheduler().runTaskTimer(tunaLands, FuelConsumeRunnable(playerLandMap), 100, 10)

    init {
        playerLandMap.forEach { (_, lands) ->
            lands.landList.forEach { query ->
                val result = query.splitQuery()
                landMap[query] = Lands.ChunkInfo(
                    lands.ownerName,
                    lands.ownerUUID,
                    lands.upCoreLocation.world.name,//Core world and chunk world are matched
                    result.first,
                    result.second
                )
            }

            if (lands.fuelLeft > 0) {
                displayManager.createDisplay(lands)
            }
        }
    }

    fun deleteLands(lands: Lands, forced: Boolean = false) {
        val uuid = lands.ownerUUID
        val name = lands.ownerName
        val upCoreLocation = lands.upCoreLocation
        val downCoreLocation = lands.downCoreLocation

        //Destroy core naturally
        if (forced) {
            upCoreLocation.block.type = Material.AIR
            downCoreLocation.block.type = Material.AIR
        } else {
            upCoreLocation.block.breakNaturally()
            downCoreLocation.block.breakNaturally()
        }

        alarmManager.unregisterAlarm(lands)
        displayManager.removeDisplay(lands)
        lands.landList.forEach { query ->
            landMap.remove(query)
        }
        playerLandMap.remove(uuid)

        Bukkit.getScheduler().runTaskAsynchronously(tunaLands, Runnable {
            val folder = File(tunaLands.dataFolder, "userdata")
            if (folder.exists()) {
                val file = File(folder, "$uuid.json")
                if (file.exists())
                    file.delete()
            }
        })
    }

    fun changeChunksOwner(oldOwner: OfflinePlayer, newOwner: OfflinePlayer) {
        val lands = playerLandMap.remove(oldOwner.uniqueId)!!
        lands.ownerName = newOwner.name!!
        lands.ownerUUID = newOwner.uniqueId
        lands.landList.forEach { query ->
            val info = landMap[query]!!
            info.ownerName = lands.ownerName
            info.ownerUUID = lands.ownerUUID
        }
        playerLandMap[lands.ownerUUID] = lands

        Bukkit.getScheduler().runTaskAsynchronously(tunaLands, Runnable {
            val folder = File(tunaLands.dataFolder, "userdata")
            if (folder.exists()) {
                val file = File(folder, "${oldOwner.uniqueId}.json")
                if (file.exists()) {
                    file.renameTo(File(folder, "${newOwner.uniqueId}.json"))
                }
            }
        })
    }

    fun getPlayerLandMap(): ConcurrentHashMap<UUID, Lands> {
        return playerLandMap
    }

    fun isProtectedLand(chunk: Chunk): Boolean {
        return landMap.containsKey(chunk.toQuery())
    }

    fun getPlayerLands(
        playerUUID: UUID,
        vararg filter: Rank = arrayOf(*Rank.values())
    ): Lands? {
        playerLandMap.forEach { (_, lands) ->
            if (playerUUID in lands.memberMap)
                if (lands.memberMap[playerUUID]!!.rank in filter)
                    return lands
        }
        return null
    }

    fun getPlayerLandsList(
        playerUUID: UUID,
        vararg filter: Rank = arrayOf(*Rank.values())
    ): List<Lands> {
        val landsList = mutableListOf<Lands>()
        playerLandMap.forEach { (_, lands) ->
            if (playerUUID in lands.memberMap)
                if (lands.memberMap[playerUUID]!!.rank in filter)
                    landsList.add(lands)
        }
        return landsList
    }

    fun getPlayerLandList(
        playerUUID: UUID,
        vararg filter: Rank = arrayOf(*Rank.values())
    ): List<String>? {
        playerLandMap.forEach { (_, lands) ->
            if (playerUUID in lands.memberMap)
                if (lands.memberMap[playerUUID]!!.rank in filter)
                    return lands.landList
        }
        return null
    }

    fun getLandsWithChunk(chunk: Chunk): Lands? {
        val query = chunk.toQuery()
        return if (landMap.containsKey(query)) {
            var value: Lands? = null
            for ((_, lands) in playerLandMap) {
                if (lands.landList.contains(query)) {
                    value = lands
                    break
                }
            }
            value
        } else {
            null
        }
    }

    fun getLandsWithChunkQuery(worldName: String, query: String): Lands? {
        val chunkInfo = landMap[query] ?: return null
        if (chunkInfo.chunk.world.name != worldName) return null

        for (lands in playerLandMap.values) {
            if (lands.landList.contains(query)) {
                return lands
            }
        }
        return null
    }

    fun isSameLandsNameExist(name: String): Boolean {
        playerLandMap.forEach { (_, lands) ->
            if (lands.landsName == name)
                return true
        }
        return false
    }

    fun buyLand(player: Player, upCore: Block, downCore: Block) {
        val chunk = upCore.chunk
        val query = chunk.toQuery()
        if (landMap.containsKey(query)) {
            player.sendMessage("${landMap[query]!!.ownerName}가 이미 구매한 땅입니다.".errorFormat())
        } else {
            //First buy
            val uuid = player.uniqueId
            if (!playerLandMap.containsKey(uuid)) {
                val now = System.currentTimeMillis()
                // Give fuel that should last for 24 hours
                val defaultFuelRequirement = configuration.fuel.fuelRequirements.maxOf { it }
                val defaultFuelAmount = (86400 / defaultFuelRequirement.secondsPerFuel).roundToLong()
                val msPerFuel = (defaultFuelRequirement.secondsPerFuel * 1000).roundToLong()
                val nextTimeToConsumeFuel = LocalDateTime.now().plus(msPerFuel, ChronoUnit.MILLIS)

                val lands = Lands(
                    player.name,
                    uuid,
                    mutableListOf(query),
                    Lands.LandHistory(
                        0,
                        now
                    ),
                    upCore.location,
                    downCore.location,
                    defaultFuelAmount,
                    nextTimeToConsumeFuel
                ).apply {
                    this.memberMap[uuid] = Lands.MemberData(uuid, Rank.OWNER, now, now)
                }
                playerLandMap[uuid] = lands
                val chunkInfo = Lands.ChunkInfo(player.name, uuid, chunk.world.name, chunk.x, chunk.z)
                landMap[query] = chunkInfo

                displayManager.createDisplay(lands)
                alarmManager.registerAlarm(lands)
                player.sendMessage("해당 위치의 땅을 구매했습니다.".infoFormat())
                player.world.playBuyChunkEffect(player, chunk)
            }
        }
    }

    fun buyLand(player: Player, flag: ItemStack, block: Block) {
        val chunk = block.chunk
        val query = chunk.toQuery()
        if (landMap.containsKey(query)) {
            player.sendMessage("${landMap[query]!!.ownerName}가 이미 구매한 땅입니다.".errorFormat())
        } else {
            //Additional buying
            val lands = this.getPlayerLands(player.uniqueId, Rank.OWNER, Rank.DELEGATOR)
            if (lands == null) {
                player.sendMessage("땅 소유주와 관리 대리인만이 땅을 확장할 수 있습니다.".errorFormat())
                return
            }

            if (!lands.enable) {
                player.sendMessage("땅을 다시 활성화 해야합니다!".errorFormat())
                return
            }

            if (chunk.isMeetOtherChunk(lands.landList)) {
                lands.landList.add(query)
                val chunkInfo = Lands.ChunkInfo(lands.ownerName, lands.ownerUUID, chunk.world.name, chunk.x, chunk.z)
                landMap[query] = chunkInfo
                Bukkit.getScheduler().runTaskAsynchronously(tunaLands, Runnable {
                    val connected = lands.hasConnectedComponent()
                    Bukkit.getScheduler().runTask(tunaLands, Runnable {
                        if (connected) {
                            lands.landList.remove(query)
                            landMap.remove(query)
                            player.sendMessage("땅따먹기 방지에 의해 구매가 취소되었습니다!".errorFormat())
                        } else {
                            flag.amount -= 1

                            player.sendMessage("해당 위치의 땅을 구매했습니다.".infoFormat())
                            player.world.playBuyChunkEffect(player, chunk)
                        }
                    })
                })
            } else {
                player.sendMessage("바로 옆에 자신의 땅이 맞닿아있어야합니다.".errorFormat())
            }
        }
    }

    fun buyLandByForceAsAdmin(player: Player, owner: OfflinePlayer, block: Block) {
        val chunk = block.chunk
        val query = chunk.toQuery()
        if (landMap.containsKey(query)) {
            player.sendMessage("${landMap[query]!!.ownerName}가 이미 구매한 땅입니다.".errorFormat())
        } else {
            //Additional buying
            val lands = this.getPlayerLands(owner.uniqueId, Rank.OWNER)
            if (lands == null) {
                player.sendMessage("해당 플레이어는 땅 소유주가 아닙니다.".errorFormat())
                return
            }
            lands.landList.add(query)
            val chunkInfo = Lands.ChunkInfo(lands.ownerName, lands.ownerUUID, chunk.world.name, chunk.x, chunk.z)
            landMap[query] = chunkInfo

            player.sendMessage("해당 위치의 땅을 강제 구매했습니다.".infoFormat())
            player.world.playBuyChunkEffect(player, chunk)
        }
    }


    fun sellLand(player: Player, flag: ItemStack, block: Block) {
        val chunk = block.chunk
        val query = chunk.toQuery()
        if (landMap.containsKey(query)) {
            val chunkInfo = landMap[query]!!
            val lands = this.getPlayerLands(chunkInfo.ownerUUID, Rank.OWNER, Rank.DELEGATOR) ?: return

            if (!lands.enable) {
                player.sendMessage("땅을 다시 활성화 해야합니다!".errorFormat())
                return
            }

            val uuid = player.uniqueId
            val coreLocation = lands.upCoreLocation
            if (coreLocation.chunk.isSameChunk(chunk)) {
                player.sendMessage("코어가 위치한 땅은 제거할 수 없습니다.".errorFormat())
                return
            }

            if (uuid in lands.memberMap) {
                when (lands.memberMap[uuid]!!.rank) {
                    Rank.OWNER, Rank.DELEGATOR -> {
                        val removedInfo = landMap.remove(query)!!
                        lands.landList.remove(query)
                        Bukkit.getScheduler().runTaskAsynchronously(tunaLands, Runnable {
                            val connected = lands.hasConnectedComponent()
                            Bukkit.getScheduler().runTask(tunaLands, Runnable {
                                if (connected) {
                                    landMap[query] = removedInfo
                                    lands.landList.add(query)
                                    player.sendMessage("땅따먹기 방지에 의해 제거가 취소되었습니다!".errorFormat())
                                } else {
                                    if (lands.landList.isEmpty()) playerLandMap.remove(player.uniqueId)
                                    flag.amount -= 1

                                    player.sendMessage("제거되었습니다.".infoFormat())
                                    player.world.playSellChunkEffect(player, chunk)
                                }
                            })
                        })
                    }

                    else -> {
                        player.sendMessage("${landMap[query]!!.ownerName}의 땅 소유자와 관리 대리인만 해당 땅을 제거할 수 있습니다.".warnFormat())
                    }
                }
            } else player.sendMessage("${chunkInfo.ownerName}의 땅입니다!".errorFormat())
        } else {
            player.sendMessage("해당 땅은 소유된 땅이 아닙니다.".warnFormat())
        }
    }


    fun sellLandByForceAsAdmin(player: Player, owner: OfflinePlayer, block: Block) {
        val chunk = block.chunk
        val query = chunk.toQuery()
        if (landMap.containsKey(query)) {
            val lands = this.getPlayerLands(owner.uniqueId, Rank.OWNER) ?: return

            if (!lands.landList.contains(query)) {
                player.sendMessage("해당 플레이어가 소유중인 땅이 아닙니다.".errorFormat())
                return
            }

            val coreLocation = lands.upCoreLocation
            if (coreLocation.chunk.isSameChunk(chunk)) {
                player.sendMessage("코어가 위치한 땅은 제거할 수 없습니다.".errorFormat())
                return
            }

            lands.landList.remove(query)
            if (lands.landList.isEmpty()) {
                playerLandMap.remove(player.uniqueId)
            }

            player.sendMessage("제거되었습니다.".infoFormat())
            player.world.playSellChunkEffect(player, chunk)
        } else {
            player.sendMessage("해당 땅은 소유된 땅이 아닙니다.".warnFormat())
        }
    }

    fun dispose() {
        task.cancel()

        JsonWriter.savePlayerLandMap()
    }
}