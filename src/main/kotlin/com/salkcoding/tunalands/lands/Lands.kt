package com.salkcoding.tunalands.lands

import org.bukkit.Bukkit
import org.bukkit.Chunk
import java.util.*

class Lands(
    val landList: MutableList<String>,
    val landInfo: LandInfo,
    val core: Core //Core is not chest, it is core block
) {
    var open = false
    val visitorSetting = LandSetting()
    val memberSetting = LandSetting()
    val delegatorSetting = DelegatorSetting()
    val partTimeJobSetting = LandSetting()

    data class LandInfo(
        val firstOwner: String,
        val firstOwnerUUID: UUID,
        val createdMillisecond: Long,
        val expiredMillisecond: Long,
        val memberList: MutableList<UUID>,
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