package com.salkcoding.tunalands.lands

import org.bukkit.Bukkit
import org.bukkit.Chunk
import java.util.*

class Lands(
    val ownerUUID: UUID,
    val landList: MutableList<String>,
    val landHistory: LandHistory,
    val core: Core //Core is not chest, it is core block
) {
    var open = false
    val visitorSetting = LandSetting()
    val memberSetting = LandSetting()
    val delegatorSetting = DelegatorSetting()
    val partTimeJobSetting = LandSetting()

    val memberList = mutableListOf<UUID>()
    val delegatorList = mutableListOf<UUID>()
    val partTimeJobList = mutableListOf<UUID>()

    fun getRank(playerUUID: UUID): Rank {
        return when (playerUUID) {
            in partTimeJobList -> Rank.PARTTIMEJOB
            in memberList -> Rank.MEMBER
            in delegatorList -> Rank.DELEGATOR
            ownerUUID -> Rank.OWNER
            else -> Rank.VISITOR
        }
    }

    data class LandHistory(
        val firstOwner: String,
        val firstOwnerUUID: UUID,
        val createdMillisecond: Long,
        val expiredMillisecond: Long,
    )

    data class ChunkInfo(
        val owner: String,
        val ownerUUID: UUID,
        val worldName: String,
        val xChunk: Int,
        val yChunk: Int
    ) {
        val chunk: Chunk = Bukkit.getWorld(worldName)!!.getChunkAt(xChunk, yChunk)
    }

    data class Core(
        val world: String,
        val x: Int,
        val y: Int,
        val z: Int
    )

}