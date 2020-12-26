package com.salkcoding.tunalands.lands

import org.bukkit.Bukkit
import org.bukkit.Chunk
import java.util.*

class Lands(
    val landList: MutableList<String>,
    val landHistory: LandHistory,
    val upCore: Core, //Chest
    val downCore: Core, //Core block
    var expiredMillisecond: Long
) {

    var enable = true
    var open = false

    val visitorSetting = LandSetting()
    val memberSetting = LandSetting()
    val delegatorSetting = DelegatorSetting()
    val partTimeJobSetting = LandSetting()

    val memberMap = mutableMapOf<UUID, MemberData>()
    val banMap = mutableMapOf<UUID, BanData>()

    data class MemberData(
        val uuid: UUID,
        val rank: Rank,
        val joined: Long,
        val lastLogin: Long
    )

    data class BanData(
        val uuid: UUID,
        val banned: Long
    )

    data class LandHistory(
        val firstOwner: String,
        val firstOwnerUUID: UUID,
        var visitorCount: Long,
        val createdMillisecond: Long,
    )

    data class ChunkInfo(
        val owner: String,
        val ownerUUID: UUID,
        val worldName: String,
        val xChunk: Int,
        val zChunk: Int
    ) {
        val chunk: Chunk = Bukkit.getWorld(worldName)!!.getChunkAt(xChunk, zChunk)
    }

    data class Core(
        val world: String,
        val x: Int,
        val y: Int,
        val z: Int
    )

}