package com.salkcoding.tunalands.lands

import com.salkcoding.tunalands.*
import com.salkcoding.tunalands.fuel.FuelConsumeRunnable
import com.salkcoding.tunalands.io.JsonReader
import com.salkcoding.tunalands.io.JsonWriter
import com.salkcoding.tunalands.util.*
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.HashMap
import kotlin.math.roundToLong

class LandManager {

    private val landMap = ConcurrentHashMap<String, Lands.ChunkInfo>()
    private val playerLandMap = JsonReader.loadPlayerLandMap()
    private val task = Bukkit.getScheduler().runTaskTimer(tunaLands, FuelConsumeRunnable(playerLandMap), 100, 20)

    init {
        playerLandMap.forEach { (_, lands) ->
            lands.landMap.forEach { (query, type) ->
                val split = query.splitQuery()
                landMap[query] = Lands.ChunkInfo(
                    lands.ownerName,
                    lands.ownerUUID,
                    lands.upCoreLocation.world.name,//Core world and chunk world are matched
                    split.first,
                    split.second,
                    type
                )
            }

            if (lands.fuelLeft > 0) {
                displayManager.createDisplay(lands)
                alarmManager.registerAlarm(lands)
            }
        }
    }

    fun deleteLands(lands: Lands, forced: Boolean = false) {
        val uuid = lands.ownerUUID
        val upCoreLocation = lands.upCoreLocation
        val downCoreLocation = lands.downCoreLocation

        //Destroy core naturally
        upCoreLocation.block.type = Material.AIR
        if (forced)
            downCoreLocation.block.type = Material.AIR
        else
            downCoreLocation.block.breakNaturally()



        displayManager.removeDisplay(lands)
        lands.landMap.forEach { (query, _) ->
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
        lands.landMap.forEach { (query, _) ->
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
    ): HashMap<String, LandType>? {
        playerLandMap.forEach { (_, lands) ->
            if (playerUUID in lands.memberMap)
                if (lands.memberMap[playerUUID]!!.rank in filter)
                    return lands.landMap
        }
        return null
    }

    fun getLandsWithChunk(chunk: Chunk): Lands? {
        val query = chunk.toQuery()
        return if (landMap.containsKey(query)) {
            var value: Lands? = null
            for ((_, lands) in playerLandMap) {
                if (lands.landMap.contains(query)) {
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
            if (lands.landMap.contains(query)) {
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

    fun setLandType(player: Player, flag: ItemStack, block: Block, type: LandType) {
        val chunk = block.chunk
        val query = chunk.toQuery()
        if (!landMap.containsKey(query)) {
            player.sendMessage("활성화된 땅에만 할 수 있습니다.".errorFormat())
            return
        }

        val lands = this.getPlayerLands(player.uniqueId, Rank.OWNER, Rank.DELEGATOR)
        if (lands == null) {
            player.sendMessage("땅 소유주와 관리 대리인만이 땅을 확장할 수 있습니다.".errorFormat())
            return
        }

        if (!lands.enable) {
            player.sendMessage("땅을 다시 활성화 해야합니다!".errorFormat())
            return
        }

        if (lands.landMap[query] == type) {
            player.sendMessage("해당 땅은 이미 ${type}입니다.".errorFormat())
            return
        }

        if (lands.landMap.filter { (_, type) ->
                type == LandType.FARM
            }.size > configuration.flag.limitFarmOccupied) {
            player.sendMessage("농지는 ${configuration.flag.limitFarmOccupied}개 이상 소유하실 수 없습니다.".errorFormat())
            return
        }

        flag.amount -= 1
        player.sendMessage("땅의 용도가 ${type}으로 전환되었습니다!".infoFormat())
    }

    fun buyLand(player: Player, upCore: Block, downCore: Block): Lands {
        //이미 caller 쪽에서 땅 소유하고 있는지 확인해서 추가 검사할 필요 없습니다.
        val chunk = upCore.chunk
        val query = chunk.toQuery()
        //First buy
        val uuid = player.uniqueId
        val now = System.currentTimeMillis()
        // Give fuel that should last for 24 hours
        val defaultFuelRequirement = configuration.fuel.fuelRequirements.minOf { it }
        val defaultFuelAmount = configuration.fuel.defaultFuel
        val msPerFuel = defaultFuelRequirement.secondsPerFuel

        val lands = Lands(
            player.name,
            uuid,
            HashMap(),
            Lands.LandHistory(
                0,
                now
            ),
            upCore.location,
            downCore.location,
            defaultFuelAmount,
            msPerFuel
        ).apply {
            this.memberMap[uuid] = Lands.MemberData(uuid, Rank.OWNER, now, now)
            this.landMap[query] = LandType.NORMAL
        }
        playerLandMap[uuid] = lands
        val chunkInfo = Lands.ChunkInfo(player.name, uuid, chunk.world.name, chunk.x, chunk.z, LandType.NORMAL)
        landMap[query] = chunkInfo

        displayManager.createDisplay(lands)

        player.sendMessage("해당 위치의 땅을 구매했습니다.".infoFormat())
        player.world.playBuyChunkEffect(player, chunk)
        return lands
    }

    fun buyLand(player: Player, flag: ItemStack, block: Block) {
        val chunk = block.chunk
        val query = chunk.toQuery()
        if (landMap.containsKey(query)) {
            player.sendMessage("${landMap[query]!!.ownerName}가 이미 구매한 땅입니다.".errorFormat())
            return
        }
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

        if (!chunk.isMeetOtherChunk(lands.landMap)) {
            player.sendMessage("바로 옆에 자신의 땅이 맞닿아있어야합니다.".errorFormat())
            return
        }
        lands.landMap[query] = LandType.NORMAL
        val chunkInfo = Lands.ChunkInfo(
            lands.ownerName,
            lands.ownerUUID,
            chunk.world.name,
            chunk.x,
            chunk.z,
            LandType.NORMAL
        )

        landMap[query] = chunkInfo
        Bukkit.getScheduler().runTaskAsynchronously(tunaLands, Runnable {
            val floodFill = lands.checkFloodFill()
            Bukkit.getScheduler().runTask(tunaLands, Runnable {
                if (floodFill) {
                    flag.amount -= 1

                    player.sendMessage("해당 위치의 땅을 구매했습니다.".infoFormat())
                    player.world.playBuyChunkEffect(player, chunk)
                } else {
                    lands.landMap.remove(query)
                    landMap.remove(query)
                    player.sendMessage("땅따먹기 방지에 의해 구매가 취소되었습니다!".errorFormat())

                }
            })
        })
    }

    fun sellLand(player: Player, flag: ItemStack, block: Block) {
        val chunk = block.chunk
        val query = chunk.toQuery()
        if (!landMap.containsKey(query)) {
            player.sendMessage("해당 땅은 소유된 땅이 아닙니다.".warnFormat())
            return
        }
        val chunkInfo = landMap[query]!!
        val lands = this.getPlayerLands(chunkInfo.ownerUUID, Rank.OWNER) ?: return

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

        when (lands.memberMap[uuid]!!.rank) {
            Rank.OWNER, Rank.DELEGATOR -> {
                val removedInfo = landMap.remove(query)!!

                lands.landMap.remove(query)
                Bukkit.getScheduler().runTaskAsynchronously(tunaLands, Runnable {
                    val floodFill = lands.checkFloodFill()
                    Bukkit.getScheduler().runTask(tunaLands, Runnable {
                        if (floodFill) {
                            if (lands.landMap.isEmpty()) playerLandMap.remove(player.uniqueId)
                            flag.amount -= 1

                            player.sendMessage("제거되었습니다.".infoFormat())
                            player.world.playSellChunkEffect(player, chunk)
                        } else {
                            landMap[query] = removedInfo
                            lands.landMap[query] = removedInfo.landType
                            player.sendMessage("땅따먹기 방지에 의해 제거가 취소되었습니다!".errorFormat())
                        }
                    })
                })
            }

            else -> player.sendMessage("${landMap[query]!!.ownerName}의 땅 소유자와 관리 대리인만 해당 땅을 제거할 수 있습니다.".warnFormat())
        }
    }

    fun buyLandByForceAsAdmin(player: Player, owner: OfflinePlayer, block: Block) {
        val chunk = block.chunk
        val query = chunk.toQuery()
        if (landMap.containsKey(query)) {
            player.sendMessage("${landMap[query]!!.ownerName}가 이미 구매한 땅입니다.".errorFormat())
            return
        }

        //Additional buying
        val lands = this.getPlayerLands(owner.uniqueId, Rank.OWNER)
        if (lands == null) {
            player.sendMessage("해당 플레이어는 땅 소유주가 아닙니다.".errorFormat())
            return
        }
        lands.landMap[query] = LandType.NORMAL
        val chunkInfo =
            Lands.ChunkInfo(lands.ownerName, lands.ownerUUID, chunk.world.name, chunk.x, chunk.z, LandType.NORMAL)
        landMap[query] = chunkInfo

        player.sendMessage("해당 위치의 땅을 강제 구매했습니다.".infoFormat())
        player.world.playBuyChunkEffect(player, chunk)
    }

    fun sellLandByForceAsAdmin(player: Player, owner: OfflinePlayer, block: Block) {
        val chunk = block.chunk
        val query = chunk.toQuery()
        if (!landMap.containsKey(query)) {
            player.sendMessage("해당 땅은 소유된 땅이 아닙니다.".warnFormat())
            return
        }
        val lands = this.getPlayerLands(owner.uniqueId, Rank.OWNER) ?: return

        if (!lands.landMap.contains(query)) {
            player.sendMessage("해당 플레이어가 소유중인 땅이 아닙니다.".errorFormat())
            return
        }

        val coreLocation = lands.upCoreLocation
        if (coreLocation.chunk.isSameChunk(chunk)) {
            player.sendMessage("코어가 위치한 땅은 제거할 수 없습니다.".errorFormat())
            return
        }

        lands.landMap.remove(query)
        if (lands.landMap.isEmpty()) {
            playerLandMap.remove(player.uniqueId)
        }

        player.sendMessage("제거되었습니다.".infoFormat())
        player.world.playSellChunkEffect(player, chunk)
    }

    fun dispose() {
        task.cancel()

        JsonWriter.savePlayerLandMap()
    }
}