package com.salkcoding.tunalands.data.lands

import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Location
import java.util.*

class Lands(
    var ownerName: String,
    var ownerUUID: UUID,
    val landList: MutableList<String>,
    val landHistory: LandHistory,
    val upCoreLocation: Location, //Chest
    val downCoreLocation: Location, //Core block
    var expiredMillisecond: Long,

    var open: Boolean = false,
    var lore: MutableList<String> = mutableListOf(
        "${ownerName}의 땅입니다.",
        "안녕하세요",
        "제 땅입니다"
    ),
    var welcomeMessage: MutableList<String> = mutableListOf(
        "${ownerName}의 땅입니다.",
        "어서오세요",
        "편안히 있다가 가세요"
    ),
    var memberSpawn: Location = upCoreLocation,
    var visitorSpawn: Location = upCoreLocation,
    val visitorSetting: LandSetting = LandSetting(),
    val partTimeJobSetting: LandSetting = LandSetting(),
    val memberSetting: LandSetting = LandSetting(),
    val delegatorSetting: DelegatorSetting = DelegatorSetting(),
    val memberMap: MutableMap<UUID, MemberData> = mutableMapOf(),
    val banMap: MutableMap<UUID, BanData> = mutableMapOf(),
) {
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
        var ownerName: String,
        var ownerUUID: UUID,
        val worldName: String,
        val xChunk: Int,
        val zChunk: Int
    ) {
        val chunk: Chunk = Bukkit.getWorld(worldName)!!.getChunkAt(xChunk, zChunk)
    }
}