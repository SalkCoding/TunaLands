package com.salkcoding.tunalands.lands

import com.salkcoding.tunalands.tunaLands
import com.salkcoding.tunalands.util.*
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.entity.Player
import java.util.*
import kotlin.collections.HashMap

class LandManager {

    private val landMap = HashMap<String, Lands.ChunkInfo>()
    private val playerLandMap = HashMap<UUID, Lands>()

    fun isProtectedLand(chunk: Chunk): Boolean {
        return chunk.toQuery() in landMap
    }

    fun isPlayerLand(player: Player, chunk: Chunk): Boolean {
        val query = chunk.toQuery()
        if (!landMap.containsKey(query)) return false
        return landMap[chunk.toQuery()]!!.ownerUUID == player.uniqueId
    }

    fun hasRank(playerUUID: UUID): Boolean {
        var has: Boolean? = null
        playerLandMap.forEach { (_, lands) ->
            if (lands.getRank(playerUUID) != Rank.VISITOR) {
                has = true
                return@forEach
            }
        }
        return has ?: false
    }

    fun getLandOwnerName(chunk: Chunk): String? {
        val query = chunk.toQuery()
        return if (query in landMap)
            landMap[query]!!.owner
        else
            null
    }

    fun getLandOwnerUUID(chunk: Chunk): UUID? {
        val query = chunk.toQuery()
        return if (query in landMap)
            landMap[query]!!.ownerUUID
        else
            null
    }

    fun getChunkInfo(chunk: Chunk): Lands.ChunkInfo? {
        val query = chunk.toQuery()
        return if (query in landMap)
            landMap[query]!!
        else
            null
    }

    fun getPlayerLands(playerUUID: UUID): Lands? {
        var value: Lands? = null
        playerLandMap.forEach { (_, lands) ->
            if (lands.getRank(playerUUID) != Rank.VISITOR && lands.getRank(playerUUID) != Rank.PARTTIMEJOB) {
                value = lands
                return@forEach
            }
        }
        return value
    }

    fun getPlayerLandList(playerUUID: UUID): List<String>? {
        var list: List<String>? = null
        playerLandMap.forEach { (_, lands) ->
            if (lands.getRank(playerUUID) != Rank.VISITOR && lands.getRank(playerUUID) != Rank.PARTTIMEJOB) {
                list = lands.landList
                return@forEach
            }
        }
        return list
    }

    fun getPlayerLandHistory(playerUUID: UUID): Lands.LandHistory? {
        var history: Lands.LandHistory? = null
        playerLandMap.forEach { (_, lands) ->
            if (lands.getRank(playerUUID) != Rank.VISITOR && lands.getRank(playerUUID) != Rank.PARTTIMEJOB) {
                history = lands.landHistory
                return@forEach
            }
        }
        return history
    }

    fun getLandVisitorSetting(playerUUID: UUID): LandSetting? {
        var setting: LandSetting? = null
        playerLandMap.forEach { (_, lands) ->
            if (lands.getRank(playerUUID) != Rank.VISITOR && lands.getRank(playerUUID) != Rank.PARTTIMEJOB) {
                setting = lands.visitorSetting
                return@forEach
            }
        }
        return setting
    }

    fun getLandMemberSetting(playerUUID: UUID): LandSetting? {
        var setting: LandSetting? = null
        playerLandMap.forEach { (_, lands) ->
            if (lands.getRank(playerUUID) != Rank.VISITOR && lands.getRank(playerUUID) != Rank.PARTTIMEJOB) {
                setting = lands.memberSetting
                return@forEach
            }
        }
        return setting
    }

    fun getLandDelegatorSetting(playerUUID: UUID): DelegatorSetting? {
        var setting: DelegatorSetting? = null
        playerLandMap.forEach { (_, lands) ->
            if (lands.getRank(playerUUID) != Rank.VISITOR && lands.getRank(playerUUID) != Rank.PARTTIMEJOB) {
                setting = lands.delegatorSetting
                return@forEach
            }
        }
        return setting
    }

    fun getLandPartTimeJobSetting(playerUUID: UUID): LandSetting? {
        var setting: LandSetting? = null
        playerLandMap.forEach { (_, lands) ->
            when (lands.getRank(playerUUID)) {
                Rank.OWNER, Rank.DELEGATOR -> {
                    setting = lands.partTimeJobSetting
                    return@forEach
                }
                else -> return@forEach
            }
        }
        return setting
    }

    fun getLandsWithChunk(chunk: Chunk): Lands? {
        val query = chunk.toQuery()
        return if (query in landMap) {
            var value: Lands? = null
            playerLandMap.forEach { (_, lands) ->
                if (lands.landList.contains(query)) {
                    value = lands
                    return@forEach
                }
            }
            value
        } else {
            null
        }
    }

    fun buyLand(player: Player, flag: Block) {
        val chunk = flag.chunk
        val query = chunk.toQuery()
        if (query in landMap) {
            player.sendMessage("Already bought! Owned by ${landMap[query]!!.owner}".errorFormat())
        } else {
            //Additional buying
            val uuid = player.uniqueId
            val lands = this.getPlayerLands(uuid)
            if (lands == null) {
                player.sendMessage("Make a core first!".errorFormat())
                return
            }

            when (lands.getRank(uuid)) {
                Rank.OWNER, Rank.DELEGATOR -> {
                    if (chunk.isMeetOtherChunk(playerLandMap[uuid]!!.landList)) {
                        playerLandMap[uuid]!!.landList.add(query)
                        landMap[query] = Lands.ChunkInfo(player.name, uuid, chunk.world.name, chunk.x, chunk.z)
                        Bukkit.getScheduler().runTaskAsynchronously(tunaLands, Runnable {
                            val floodFill = !playerLandMap[uuid]!!.checkFloodFill()
                            Bukkit.getScheduler().runTask(tunaLands, Runnable {
                                if (!floodFill) {
                                    playerLandMap[uuid]!!.landList.remove(query)
                                    landMap.remove(query)
                                    player.sendMessage("Flood fill false! Your bought will be cancelled!".errorFormat())
                                } else {
                                    player.sendMessage("You bought a chunk you stand!".infoFormat())
                                }
                            })
                        })
                    } else {
                        player.sendMessage("You can only buy a chunk that meet other chunk that you owned".errorFormat())
                    }
                }
                else -> {
                    player.sendMessage("You don't have a permission to buy".errorFormat())
                }
            }
        }
    }

    fun buyLand(player: Player, upCore: Block, downCore: Block) {
        val chunk = upCore.chunk
        val query = chunk.toQuery()
        if (query in landMap) {
            player.sendMessage("Already bought! Owned by ${landMap[query]!!.owner}".errorFormat())
        } else {
            //First buy
            val uuid = player.uniqueId
            if (uuid !in playerLandMap) {
                val now = System.currentTimeMillis()
                val expired = Calendar.getInstance()
                expired.add(Calendar.DATE, 1)//Next day(Temp value)
                playerLandMap[uuid] =
                    Lands(
                        uuid,
                        mutableListOf(query),
                        Lands.LandHistory(
                            player.name,
                            uuid,
                            now
                        ),
                        Lands.Core(
                            upCore.world.name,
                            upCore.location.blockX,
                            upCore.location.blockY,
                            upCore.location.blockZ
                        ),
                        Lands.Core(
                            downCore.world.name,
                            downCore.location.blockX,
                            downCore.location.blockY,
                            downCore.location.blockZ
                        ),
                        expired.timeInMillis
                    )
                landMap[query] = Lands.ChunkInfo(player.name, uuid, chunk.world.name, chunk.x, chunk.z)
                player.sendMessage("You bought a chunk you stand!".infoFormat())
            }
        }
    }

    fun sellLand(player: Player, chunk: Chunk) {
        val query = chunk.toQuery()
        if (query in landMap) {
            val uuid = player.uniqueId
            val lands = this.getPlayerLands(uuid)
            if (lands == null) {
                player.sendMessage("Make a core first!".errorFormat())
                return
            }

            when (lands.getRank(uuid)) {
                Rank.OWNER, Rank.DELEGATOR -> {
                    val removedInfo = landMap.remove(query)!!
                    lands.landList.remove(query)
                    Bukkit.getScheduler().runTaskAsynchronously(tunaLands, Runnable {
                        val floodFill = playerLandMap[uuid]!!.checkFloodFill()
                        Bukkit.getScheduler().runTask(tunaLands, Runnable {
                            if (!floodFill) {
                                landMap[query] = removedInfo
                                playerLandMap[uuid]!!.landList.add(query)
                                player.sendMessage("Flood fill false! Your sold will be cancelled!".errorFormat())
                            } else {
                                if (lands.landList.isEmpty()) {
                                    val upCore = lands.upCore
                                    val downCore = lands.downCore
                                    chunk.world.getBlockAt(upCore.x, upCore.y, upCore.z).breakNaturally()//Chest delete
                                    chunk.world.getBlockAt(downCore.x, downCore.y, downCore.z)
                                        .breakNaturally()//Core destroy naturally

                                    playerLandMap.remove(player.uniqueId)
                                }
                                player.sendMessage("Successfully removed".infoFormat())
                            }
                        })
                    })
                }
                else -> {
                    player.sendMessage("You are not owner or delegator of this chunk. This chunk owned by ${landMap[query]!!.owner}".warnFormat())
                }
            }
        } else {
            player.sendMessage("This chunk is not owned by anyone.".warnFormat())
        }
    }

    fun debug() {
        landMap.entries.forEach { (_, land) ->
            val c = land.chunk
            val blockX: Int = c.x shl 4
            val blockZ: Int = c.z shl 4

            var location = c.world.getHighestBlockAt(blockX, blockZ).location.add(0.0, 2.0, 0.0)
            location.world.spawnParticle(Particle.REDSTONE, location, 10, Particle.DustOptions(Color.RED, 5f))

            location = c.world.getHighestBlockAt(blockX, blockZ + 15).location.add(0.0, 2.0, 0.0)
            location.world.spawnParticle(Particle.REDSTONE, location, 10, Particle.DustOptions(Color.RED, 5f))

            location = c.world.getHighestBlockAt(blockX + 15, blockZ).location.add(0.0, 2.0, 0.0)
            location.world.spawnParticle(Particle.REDSTONE, location, 10, Particle.DustOptions(Color.RED, 5f))

            location = c.world.getHighestBlockAt(blockX + 15, blockZ + 15).location.add(0.0, 2.0, 0.0)
            location.world.spawnParticle(Particle.REDSTONE, location, 10, Particle.DustOptions(Color.RED, 5f))
        }
    }

}