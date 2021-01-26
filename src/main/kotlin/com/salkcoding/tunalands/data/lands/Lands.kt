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
    val upCore: Location, //Chest
    val downCore: Location, //Core block
    var expiredMillisecond: Long,

    var open: Boolean = false,
    var lore: MutableList<String> = mutableListOf(
        "${ownerName}의 땅입니다.",
        "ㅎㅇ",
        "ㅂㅇ"
    ),
    var welcomeMessage: MutableList<String> = mutableListOf(
        "${ownerName}의 땅입니다.",
        "ㅎㅇ",
        "ㅂㅂ"
    ),
    var memberSpawn: Location = upCore,
    var visitorSpawn: Location = upCore,
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