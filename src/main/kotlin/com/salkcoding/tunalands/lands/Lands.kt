package com.salkcoding.tunalands.lands

import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Location
import java.util.*

class Lands(
    var ownerName: String,
    //val ownerUUID: UUID,
    val landList: MutableList<String>,
    val landHistory: LandHistory,
    val upCore: Location, //Chest
    val downCore: Location, //Core block
    var expiredMillisecond: Long
) {
    var enable = true
    var open = false
    var lore = mutableListOf(
        "${ownerName}의 땅입니다.",
        "",
        ""
    )
    var welcomeMessage = "${ownerName}의 땅입니다."

    var memberSpawn = upCore
    var visitorSpawn = upCore

    val visitorSetting = LandSetting()
    val memberSetting = LandSetting()
    val delegatorSetting = DelegatorSetting()
    val partTimeJobSetting = LandSetting()

    val visitorMap = mutableMapOf<UUID, VisitorData>()

    val memberMap = mutableMapOf<UUID, MemberData>()
    val banMap = mutableMapOf<UUID, BanData>()

    data class VisitorData(
        val uuid: UUID,
        val visit: Long
    )

    data class MemberData(
        val uuid: UUID,
        var rank: Rank,
        val joined: Long,
        var lastLogin: Long
    )

    data class BanData(
        val uuid: UUID,
        val banned: Long
    )

    data class LandHistory(
        var visitorCount: Long,
        val createdMillisecond: Long,
    )

    data class ChunkInfo(
        var owner: String,
        var ownerUUID: UUID,
        val worldName: String,
        val xChunk: Int,
        val zChunk: Int
    ) {
        val chunk: Chunk = Bukkit.getWorld(worldName)!!.getChunkAt(xChunk, zChunk)
    }
}