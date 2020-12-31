package com.salkcoding.tunalands.lands

import com.salkcoding.tunalands.tunaLands
import com.salkcoding.tunalands.util.*
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class LandManager {

    private val landMap = ConcurrentHashMap<String, Lands.ChunkInfo>()
    private val playerLandMap = ConcurrentHashMap<UUID, Lands>()
    private val task: BukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(tunaLands, Runnable {
        playerLandMap.forEach { (_, lands) ->
            if (lands.enable) {
                val expired = lands.expiredMillisecond
                val present = System.currentTimeMillis()
                if (present > expired)
                    lands.enable = false
            }
        }
    }, 100, 100)

    fun shutdown() {
        task.cancel()
    }

    fun deleteLands(owner: Player) {
        val lands = playerLandMap[owner.uniqueId]!!
        lands.landList.forEach { query ->
            landMap.remove(query)
        }
        playerLandMap.remove(owner.uniqueId)
    }

    fun changeChunksOwner(oldOwner: Player, newOwner: Player) {
        val lands = playerLandMap[oldOwner.uniqueId]!!
        lands.landList.forEach { query ->
            val info = landMap[query]!!
            info.owner = newOwner.name
            info.ownerUUID = newOwner.uniqueId
        }
    }

    fun getPlayerLandMap(): ConcurrentHashMap<UUID, Lands> {
        return playerLandMap
    }

    fun isProtectedLand(chunk: Chunk): Boolean {
        return landMap.containsKey(chunk.toQuery())
    }

    fun getPlayerLands(playerUUID: UUID): Lands? {
        playerLandMap.forEach { (_, lands) ->
            if (playerUUID in lands.memberMap) return lands
        }
        return null
    }

    fun getVisitorLands(playerUUID: UUID): Lands? {
        playerLandMap.forEach { (_, lands) ->
            if (playerUUID in lands.memberMap) {
                if (lands.memberMap[playerUUID]!!.rank == Rank.VISITOR) {
                    return lands
                }
            }
        }
        return null
    }

    fun getPlayerLandList(playerUUID: UUID): List<String>? {
        playerLandMap.forEach { (_, lands) ->
            if (playerUUID in lands.memberMap) return lands.landList
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

    fun buyLand(player: Player, flag: Block) {
        val chunk = flag.chunk
        val query = chunk.toQuery()
        if (landMap.containsKey(query)) {
            player.sendMessage("${landMap[query]!!.owner}가 이미 구매한 땅입니다.".errorFormat())
        } else {
            //Additional buying
            val uuid = player.uniqueId
            val lands = this.getPlayerLands(uuid)
            if (lands == null) {
                player.sendMessage("코어를 먼저 만드세요!".errorFormat())
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
                                    player.sendMessage("해당 위치의 땅을 구매했습니다.".infoFormat())
                                } else {
                                    playerLandMap[uuid]!!.landList.remove(query)
                                    landMap.remove(query)
                                    player.sendMessage("땅따먹기 방지에 의해 구매가 취소되었습니다!".errorFormat())
                                }
                            })
                        })
                    } else {
                        player.sendMessage("바로 옆에 자신의 땅이 맞닿아있어야합니다.".errorFormat())
                    }
                }
                else -> {
                    player.sendMessage("권한이 없습니다!".errorFormat())
                }
            }
        }
    }

    fun buyLand(player: Player, upCore: Block, downCore: Block) {
        val chunk = upCore.chunk
        val query = chunk.toQuery()
        if (landMap.containsKey(query)) {
            player.sendMessage("${landMap[query]!!.owner}가 이미 구매한 땅입니다.".errorFormat())
        } else {
            //First buy
            val uuid = player.uniqueId
            if (!playerLandMap.containsKey(uuid)) {
                val now = System.currentTimeMillis()
                val expired = Calendar.getInstance()
                expired.add(Calendar.MINUTE, 30)//30 Minutes(Temp value)
                playerLandMap[uuid] =
                    Lands(
                        player.name,
                        //uuid,
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
                landMap[query] = Lands.ChunkInfo(player.name, uuid, chunk.world.name, chunk.x, chunk.z)
                player.sendMessage("해당 위치의 땅을 구매했습니다.".infoFormat())
            }
        }
    }

    fun sellLand(player: Player, chunk: Chunk) {
        val query = chunk.toQuery()
        if (landMap.containsKey(query)) {
            val uuid = player.uniqueId
            val lands = this.getPlayerLands(uuid)
            if (lands == null) {
                player.sendMessage("코어를 먼저 만드세요!".errorFormat())
                return
            }

            val info = lands.upCore
            if (info.chunk.isSameChunk(chunk)) {
                player.sendMessage("코어가 위치한 땅은 제거할 수 없습니다.".errorFormat())
                return
            }

            when (lands.memberMap[uuid]!!.rank) {
                Rank.OWNER, Rank.DELEGATOR -> {
                    val removedInfo = landMap.remove(query)!!
                    lands.landList.remove(query)
                    Bukkit.getScheduler().runTaskAsynchronously(tunaLands, Runnable {
                        val floodFill = playerLandMap[uuid]!!.checkFloodFill()
                        Bukkit.getScheduler().runTask(tunaLands, Runnable {
                            if (floodFill) {
                                if (lands.landList.isEmpty()) playerLandMap.remove(player.uniqueId)
                                player.sendMessage("제거되었습니다.".infoFormat())
                            } else {
                                landMap[query] = removedInfo
                                playerLandMap[uuid]!!.landList.add(query)
                                player.sendMessage("땅따먹기 방지에 의해 제거가 취소되었습니다!".errorFormat())
                            }
                        })
                    })
                }
                else -> {
                    player.sendMessage("${landMap[query]!!.owner}의 땅 소유자와 관리 대리인만 해당 땅을 제거할 수 있습니다.".warnFormat())
                }
            }
        } else {
            player.sendMessage("해당 땅은 소유된 땅이 아닙니다.".warnFormat())
        }
    }

    fun debug() {
        landMap.entries.forEach { (_, land) ->
            val c = land.chunk
            val blockX: Int = c.x shl 4
            val blockZ: Int = c.z shl 4

            var location = c.world.getHighestBlockAt(blockX, blockZ).location.add(0.0, 5.0, 0.0)
            location.world.spawnParticle(Particle.FIREWORKS_SPARK, location, 0, .0, .0, .0, .0, null, true)

            location = c.world.getHighestBlockAt(blockX, blockZ + 16).location.add(0.0, 5.0, 0.0)
            location.world.spawnParticle(Particle.FIREWORKS_SPARK, location, 0, .0, .0, .0, .0, null, true)

            location = c.world.getHighestBlockAt(blockX + 16, blockZ).location.add(0.0, 5.0, 0.0)
            location.world.spawnParticle(Particle.FIREWORKS_SPARK, location, 0, .0, .0, .0, .0, null, true)

            location = c.world.getHighestBlockAt(blockX + 16, blockZ + 16).location.add(0.0, 5.0, 0.0)
            location.world.spawnParticle(Particle.FIREWORKS_SPARK, location, 0, .0, .0, .0, .0, null, true)
        }
    }
}