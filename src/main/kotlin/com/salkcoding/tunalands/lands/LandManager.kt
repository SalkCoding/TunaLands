package com.salkcoding.tunalands.lands

import com.salkcoding.tunalands.*
import com.salkcoding.tunalands.file.ImposeTimeWriter
import com.salkcoding.tunalands.fuel.FuelConsumeRunnable
import com.salkcoding.tunalands.file.PlayerLandMapReader
import com.salkcoding.tunalands.file.PlayerLandMapWriter
import com.salkcoding.tunalands.util.*
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.HashMap

class LandManager {

    private val landMap = ConcurrentHashMap<String, Lands.ChunkInfo>()
    private val playerLandMap = PlayerLandMapReader.loadPlayerLandMap()
    private val fuelConsumeRunnable = FuelConsumeRunnable(playerLandMap)
    private val task = Bukkit.getScheduler().runTaskTimer(tunaLands, fuelConsumeRunnable, 20, 1200)

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
        val listOfQueries = lands.landMap.keys.toList()
        lands.landMap.forEach { (query, _) ->
            landMap.remove(query)
        }
        onChunkInfoChange(listOfQueries)
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

    fun getFuelConsumeRunner(): FuelConsumeRunnable {
        return fuelConsumeRunnable
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
                if (lands.landMap.contains(query)
                    && lands.upCoreLocation.world.name == chunk.world.name
                ) {
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

        if (type == LandType.FARM && lands.landMap.filter { (_, type) ->
                type == LandType.FARM
            }.size >= configuration.farm.limitOccupied) {
            player.sendMessage("농작지는 ${configuration.farm.limitOccupied}개 이상 소유하실 수 없습니다.".errorFormat())
            return
        }

        if (landMap.contains(query)){
            landMap[query]!!.landType = type
        } else {
            landMap[query] = Lands.ChunkInfo(lands.ownerName, lands.ownerUUID, chunk.world.name, chunk.x, chunk.z, type)
        }
        lands.landMap[query] = type
        onChunkInfoChange(listOf(query))
        flag.amount -= 1
        player.sendMessage("땅의 용도가 ${type}으로 전환되었습니다!".infoFormat())
        chunk.world.playSetChunkEffect(player, chunk, Material.BROWN_TERRACOTTA)
    }

    fun createLand(player: Player, upCore: Block, downCore: Block): Lands {
        //이미 caller 쪽에서 땅 소유하고 있는지 확인해서 추가 검사할 필요 없습니다.
        val chunk = upCore.chunk
        val query = chunk.toQuery()
        //First buy
        val uuid = player.uniqueId
        val now = System.currentTimeMillis()
        // Give fuel that should last for 24 hours
        val defaultFuelRequirement = configuration.fuel.fuelRequirements.minOf { it }
        val defaultFuelAmount = configuration.fuel.defaultFuel
        val defaultDayPerFuel = defaultFuelRequirement.dayPerFuel

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
            defaultDayPerFuel
        ).apply {
            this.memberMap[uuid] = Lands.MemberData(uuid, Rank.OWNER, now, now)
            this.landMap[query] = LandType.NORMAL
        }
        playerLandMap[uuid] = lands
        val chunkInfo = Lands.ChunkInfo(player.name, uuid, chunk.world.name, chunk.x, chunk.z, LandType.NORMAL)
        landMap[query] = chunkInfo
        onChunkInfoChange(listOf(query))

        displayManager.createDisplay(lands)

        player.sendMessage("해당 위치의 땅을 구매했습니다.".infoFormat())
        player.world.playBuyChunkEffect(player, chunk)
        return lands
    }

    fun buyChunk(player: Player, flag: ItemStack, block: Block) {
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
        val limitOccupied = configuration.protect.getMaxOccupied(lands)
        if (limitOccupied.maxChunkAmount <= lands.landMap.size) {
            player.sendMessage("더 이상 땅을 구입할 수 없습니다.".errorFormat())
            return
        }

        val price = configuration.flag.getActivePrice(lands).price.toDouble()
        if (player.hasNotEnoughMoney(price)) {
            val delta = price - economy.getBalance(player)
            player.sendMessage("${"%.2f".format(delta)}캔이 부족합니다.".errorFormat())
            return
        }
        economy.withdrawPlayer(player, price)

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
        onChunkInfoChange(listOf(query))
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

    fun sellChunk(player: Player, flag: ItemStack, block: Block) {
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
                            onChunkInfoChange(listOf(query))
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
        onChunkInfoChange(listOf(query))

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
        landMap.remove(query)
        onChunkInfoChange(listOf(query))

        player.sendMessage("제거되었습니다.".infoFormat())
        player.world.playSellChunkEffect(player, chunk)
    }

    fun dispose() {
        task.cancel()

        PlayerLandMapWriter.savePlayerLandMap()
        ImposeTimeWriter.saveImposeTime()
    }

    fun onChunkInfoChange(changedChunkQueries: List<String>) {
        val updates = changedChunkQueries.associateWith {
            val chunkInfo = landMap[it]
            if (chunkInfo == null) {
                null
            } else {
                Pair(chunkInfo.worldName, chunkInfo.landType)
            }
        }
        tunaLands.nonMainServerSyncSender.sendChunkInfo(updates)
    }


    fun getLandMap(): ConcurrentHashMap<String, Lands.ChunkInfo> {
        return landMap
    }
}