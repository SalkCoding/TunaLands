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
        playerLandMap.forEach { (_, lands) ->
            if (playerUUID in lands.memberMap) {
                return when (lands.memberMap[playerUUID]!!.rank) {
                    Rank.VISITOR -> false
                    else -> true
                }
            }
        }
        return false
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
        playerLandMap.forEach { (_, lands) ->
            if (playerUUID in lands.memberMap) {
                when (lands.memberMap[playerUUID]!!.rank) {
                    Rank.VISITOR -> return@forEach
                    else -> return lands
                }
            }
        }
        return null
    }

    fun getPlayerLandList(playerUUID: UUID): List<String>? {
        playerLandMap.forEach { (_, lands) ->
            if (playerUUID in lands.memberMap) {
                when (lands.memberMap[playerUUID]!!.rank) {
                    Rank.VISITOR -> return@forEach
                    else -> return lands.landList
                }
            }
        }
        return null
    }

    fun getPlayerLandHistory(playerUUID: UUID): Lands.LandHistory? {
        playerLandMap.forEach { (_, lands) ->
            if (playerUUID in lands.memberMap) {
                when (lands.memberMap[playerUUID]!!.rank) {
                    Rank.VISITOR -> return@forEach
                    else -> return lands.landHistory
                }
            }
        }
        return null
    }

    fun getLandVisitorSetting(playerUUID: UUID): LandSetting? {
        playerLandMap.forEach { (_, lands) ->
            if (playerUUID in lands.memberMap) {
                when (lands.memberMap[playerUUID]!!.rank) {
                    Rank.VISITOR -> return@forEach
                    else -> return lands.visitorSetting
                }
            }
        }
        return null
    }

    fun getLandMemberSetting(playerUUID: UUID): LandSetting? {
        playerLandMap.forEach { (_, lands) ->
            if (playerUUID in lands.memberMap) {
                when (lands.memberMap[playerUUID]!!.rank) {
                    Rank.VISITOR -> return@forEach
                    else -> return lands.memberSetting
                }
            }
        }
        return null
    }

    fun getLandDelegatorSetting(playerUUID: UUID): DelegatorSetting? {
        playerLandMap.forEach { (_, lands) ->
            if (playerUUID in lands.memberMap) {
                when (lands.memberMap[playerUUID]!!.rank) {
                    Rank.VISITOR -> return@forEach
                    else -> return lands.delegatorSetting
                }
            }
        }
        return null
    }

    fun getLandPartTimeJobSetting(playerUUID: UUID): LandSetting? {
        playerLandMap.forEach { (_, lands) ->
            if (playerUUID in lands.memberMap) {
                when (lands.memberMap[playerUUID]!!.rank) {
                    Rank.VISITOR -> return@forEach
                    else -> return lands.partTimeJobSetting
                }
            }
        }
        return null
    }

    fun getLandsWithChunk(chunk: Chunk): Lands? {
        val query = chunk.toQuery()
        return if (query in landMap) {
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

            when (lands.memberMap[uuid]!!.rank) {
                Rank.OWNER, Rank.DELEGATOR -> {
                    if (chunk.isMeetOtherChunk(playerLandMap[uuid]!!.landList)) {
                        playerLandMap[uuid]!!.landList.add(query)
                        landMap[query] = Lands.ChunkInfo(player.name, uuid, chunk.world.name, chunk.x, chunk.z)
                        Bukkit.getScheduler().runTaskAsynchronously(tunaLands, Runnable {
                            val floodFill = playerLandMap[uuid]!!.checkFloodFill()
                            Bukkit.getScheduler().runTask(tunaLands, Runnable {
                                if (floodFill) {
                                    player.sendMessage("You bought a chunk you stand!".infoFormat())
                                } else {
                                    playerLandMap[uuid]!!.landList.remove(query)
                                    landMap.remove(query)
                                    player.sendMessage("Flood fill false! Your bought will be cancelled!".errorFormat())
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
                        mutableListOf(query),
                        Lands.LandHistory(
                            player.name,
                            uuid,
                            0,
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
                    ).apply {
                        this.memberMap[uuid] = Lands.MemberData(uuid, Rank.OWNER, now, now)
                    }
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

            val info = lands.upCore
            if (info.world == chunk.world.name) {
                val coreChunk = chunk.world.getBlockAt(info.x, info.y, info.z).chunk
                if (coreChunk.x == chunk.x && coreChunk.z == chunk.z && lands.landList.size > 1) {
                    player.sendMessage("Core chunk can be sold after all of chunks are sold.".errorFormat())
                    return
                }
            }

            when (lands.memberMap[uuid]!!.rank) {
                Rank.OWNER, Rank.DELEGATOR -> {
                    val removedInfo = landMap.remove(query)!!
                    lands.landList.remove(query)
                    Bukkit.getScheduler().runTaskAsynchronously(tunaLands, Runnable {
                        val floodFill = playerLandMap[uuid]!!.checkFloodFill()
                        Bukkit.getScheduler().runTask(tunaLands, Runnable {
                            if (floodFill) {
                                if (lands.landList.isEmpty()) {
                                    val upCore = lands.upCore
                                    val downCore = lands.downCore
                                    chunk.world.getBlockAt(upCore.x, upCore.y, upCore.z).breakNaturally()//Chest delete
                                    chunk.world.getBlockAt(downCore.x, downCore.y, downCore.z)
                                        .breakNaturally()//Core destroy naturally

                                    playerLandMap.remove(player.uniqueId)
                                }
                                player.sendMessage("Successfully removed".infoFormat())
                            } else {
                                landMap[query] = removedInfo
                                playerLandMap[uuid]!!.landList.add(query)
                                player.sendMessage("Flood fill false! Your sold will be cancelled!".errorFormat())
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