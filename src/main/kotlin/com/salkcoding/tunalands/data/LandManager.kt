package com.salkcoding.tunalands.data

import com.salkcoding.tunalands.data.lands.Lands
import com.salkcoding.tunalands.data.lands.Rank
import com.salkcoding.tunalands.database
import com.salkcoding.tunalands.displayManager
import com.salkcoding.tunalands.io.JsonReader
import com.salkcoding.tunalands.io.JsonWriter
import com.salkcoding.tunalands.tunaLands
import com.salkcoding.tunalands.util.*
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.OfflinePlayer
import org.bukkit.Particle
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class LandManager {

    private val landMap = ConcurrentHashMap<String, Lands.ChunkInfo>()
    private val playerLandMap = JsonReader.loadPlayerLandMap()
    private val task = Bukkit.getScheduler().runTaskTimerAsynchronously(tunaLands, Runnable {
        playerLandMap.forEach { (_, lands) ->
            val expired = lands.expiredMillisecond
            val present = System.currentTimeMillis()
            if (present > expired)
                deleteLands(lands.ownerUUID, lands.ownerName)
        }
    }, 100, 100)

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
        }
    }

    fun close() {
        task.cancel()

        JsonWriter.savePlayerLandMap()
    }

    fun deleteLands(owner: OfflinePlayer) {
        deleteLands(owner.uniqueId, owner.name!!)
    }

    fun deleteLands(ownerUUID: UUID, ownerName: String) {
        val lands = playerLandMap[ownerUUID]!!
        displayManager.removeDisplayInChunk(lands.upCoreLocation.chunk)
        lands.landList.forEach { query ->
            landMap.remove(query)
        }
        playerLandMap.remove(ownerUUID)
        database.deleteAll(ownerUUID, ownerName)

        Bukkit.getScheduler().runTaskAsynchronously(tunaLands, Runnable {
            val folder = File(tunaLands.dataFolder, "userdata")
            if (folder.exists()) {
                val file = File(folder, "$ownerUUID.json")
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

        database.replaceAll(oldOwner.uniqueId, oldOwner.name!!, newOwner.uniqueId, newOwner.name!!)

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

    //Be careful to Rank.PartiTimeJob and Rank.Visitor as not be conflicted with Rank.Owner, Rank.Delegator, Rank.Member
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
                val expired = Calendar.getInstance()
                expired.add(Calendar.DATE, 1)//1 Day
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
                    expired.timeInMillis
                ).apply {
                    this.memberMap[uuid] = Lands.MemberData(uuid, Rank.OWNER, now, now)
                }
                playerLandMap[uuid] = lands
                val chunkInfo = Lands.ChunkInfo(player.name, uuid, chunk.world.name, chunk.x, chunk.z)
                landMap[query] = chunkInfo
                database.insert(chunkInfo)
                displayManager.createDisplay(lands)
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

            if (chunk.isMeetOtherChunk(lands.landList)) {
                lands.landList.add(query)
                val chunkInfo = Lands.ChunkInfo(lands.ownerName, lands.ownerUUID, chunk.world.name, chunk.x, chunk.z)
                landMap[query] = chunkInfo
                Bukkit.getScheduler().runTaskAsynchronously(tunaLands, Runnable {
                    val floodFill = lands.checkFloodFill()
                    Bukkit.getScheduler().runTask(tunaLands, Runnable {
                        if (floodFill) {
                            flag.amount -= 1
                            database.insert(chunkInfo)
                            player.sendMessage("해당 위치의 땅을 구매했습니다.".infoFormat())
                            player.world.playBuyChunkEffect(player, chunk)
                        } else {
                            lands.landList.remove(query)
                            landMap.remove(query)
                            player.sendMessage("땅따먹기 방지에 의해 구매가 취소되었습니다!".errorFormat())
                        }
                    })
                })
            } else {
                player.sendMessage("바로 옆에 자신의 땅이 맞닿아있어야합니다.".errorFormat())
            }
        }
    }

    fun sellLand(player: Player, flag: ItemStack, block: Block) {
        val chunk = block.chunk
        val query = chunk.toQuery()
        if (landMap.containsKey(query)) {
            val chunkInfo = landMap[query]!!
            val lands = this.getPlayerLands(chunkInfo.ownerUUID, Rank.OWNER, Rank.DELEGATOR) ?: return

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
                            val floodFill = lands.checkFloodFill()
                            Bukkit.getScheduler().runTask(tunaLands, Runnable {
                                if (floodFill) {
                                    if (lands.landList.isEmpty()) playerLandMap.remove(player.uniqueId)
                                    flag.amount -= 1
                                    database.delete(removedInfo)
                                    player.sendMessage("제거되었습니다.".infoFormat())
                                    player.world.playSellChunkEffect(player, chunk)
                                } else {
                                    landMap[query] = removedInfo
                                    lands.landList.add(query)
                                    player.sendMessage("땅따먹기 방지에 의해 제거가 취소되었습니다!".errorFormat())
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

    fun debug() {
        landMap.entries.forEach { (_, land) ->
            val c = land.chunk
            val blockX: Int = c.x shl 4
            val blockZ: Int = c.z shl 4

            var location = c.world.getHighestBlockAt(blockX, blockZ).location.add(0.0, 3.0, 0.0)
            location.world.spawnParticle(Particle.FIREWORKS_SPARK, location, 0, .0, .0, .0, .0, null, true)

            location = c.world.getHighestBlockAt(blockX, blockZ + 16).location.add(0.0, 3.0, 0.0)
            location.world.spawnParticle(Particle.FIREWORKS_SPARK, location, 0, .0, .0, .0, .0, null, true)

            location = c.world.getHighestBlockAt(blockX + 16, blockZ).location.add(0.0, 3.0, 0.0)
            location.world.spawnParticle(Particle.FIREWORKS_SPARK, location, 0, .0, .0, .0, .0, null, true)

            location = c.world.getHighestBlockAt(blockX + 16, blockZ + 16).location.add(0.0, 3.0, 0.0)
            location.world.spawnParticle(Particle.FIREWORKS_SPARK, location, 0, .0, .0, .0, .0, null, true)
        }
    }
}