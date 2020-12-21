package com.salkcoding.tunalands.lands

import com.salkcoding.tunalands.tunaLands
import com.salkcoding.tunalands.util.*
import org.bukkit.Chunk
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.Particle
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

    fun hasLand(player: Player): Boolean {
        return player.uniqueId in playerLandMap
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

    fun getPlayerLands(playerUUID: UUID): Lands? {
        return if (playerUUID in playerLandMap) playerLandMap[playerUUID] else null
    }

    fun getPlayerLandList(playerUUID: UUID): List<String>? {
        return if (playerUUID in playerLandMap) playerLandMap[playerUUID]!!.landList else null
    }

    fun getPlayerLandInfo(playerUUID: UUID): Lands.LandInfo? {
        return if (playerUUID in playerLandMap) playerLandMap[playerUUID]!!.landInfo else null
    }

    fun getLandVisitorSetting(playerUUID: UUID): LandSetting? {
        return if (playerUUID in playerLandMap) playerLandMap[playerUUID]!!.visitorSetting else null
    }

    fun getLandMemberSetting(playerUUID: UUID): LandSetting? {
        return if (playerUUID in playerLandMap) playerLandMap[playerUUID]!!.memberSetting else null
    }

    fun getLandDelegatorSetting(playerUUID: UUID): DelegatorSetting? {
        return if (playerUUID in playerLandMap) playerLandMap[playerUUID]!!.delegatorSetting else null
    }

    fun getLandPartTimeJobSetting(playerUUID: UUID): LandSetting? {
        return if (playerUUID in playerLandMap) playerLandMap[playerUUID]!!.partTimeJobSetting else null
    }

    fun buyLand(player: Player, core: Block) {
        val chunk = player.chunk
        val query = chunk.toQuery()
        if (query in landMap) {
            player.sendMessage("Already bought! Owned by ${landMap[query]!!.owner}".errorFormat())
        } else {
            val uuid = player.uniqueId
            if (uuid in playerLandMap) {
                if (chunk.isMeetOtherChunk(playerLandMap[uuid]!!.landList)) {//Additional buying
                    tunaLands.server.scheduler.runTaskAsynchronously(tunaLands, Runnable {
                        if (!playerLandMap[uuid]!!.checkFloodFill()) {
                            playerLandMap[uuid]!!.landList.add(query)
                        } else {
                            player.sendMessage("Flood fill false! Your bought will be cancelled!".errorFormat())
                        }
                    })
                } else {
                    player.sendMessage("You can only buy a chunk that meet other chunk that you owned".errorFormat())
                    return
                }
            } else {//First buying
                val now = System.currentTimeMillis()
                val expired = Calendar.getInstance()
                expired.add(Calendar.DATE, 1)//Next day(Temp value)
                playerLandMap[uuid] =
                    Lands(
                        mutableListOf(query),
                        Lands.LandInfo(
                            player.name,
                            uuid,
                            now,
                            expired.timeInMillis,
                            mutableListOf(uuid)
                        ),
                        Lands.Core(core.world.name, core.location.blockX, core.location.blockY, core.location.blockZ)
                    )
            }
            landMap[query] = Lands.ChunkInfo(player.name, uuid, chunk.world.name, chunk.x, chunk.z)
            player.sendMessage("You bought a chunk you stand!".infoFormat())
        }
    }

    fun sellLand(player: Player, chunk: Chunk) {
        val query = chunk.toQuery()
        if (query in landMap) {
            if (player.uniqueId == landMap[query]!!.ownerUUID) {
                landMap.remove(query)
                playerLandMap[player.uniqueId]!!.landList.remove(query)

                if (playerLandMap[player.uniqueId]!!.landList.isEmpty()) {
                    val core = playerLandMap[player.uniqueId]!!.core
                    chunk.world.getBlockAt(core.x, core.y, core.z).breakNaturally()//Core destroy naturally
                    chunk.world.getBlockAt(core.x, core.y + 1, core.z).type = Material.AIR//Chest delete

                    playerLandMap.remove(player.uniqueId)
                }

                player.sendMessage("Successfully removed".infoFormat())
            } else {
                player.sendMessage("You are not owner of this chunk. This chunk owned by ${landMap[query]!!.owner}".warnFormat())
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