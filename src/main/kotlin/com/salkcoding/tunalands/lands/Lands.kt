package com.salkcoding.tunalands.lands

import com.salkcoding.tunalands.bungeeApi
import com.salkcoding.tunalands.lands.setting.DelegatorSetting
import com.salkcoding.tunalands.lands.setting.LandSetting
import com.salkcoding.tunalands.util.warnFormat
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Chunk
import org.bukkit.Location
import java.util.*

data class Lands(
    var ownerName: String,
    var ownerUUID: UUID,
    val landList: MutableList<String>,
    val landHistory: LandHistory,
    val upCoreLocation: Location, //Chest
    val downCoreLocation: Location, //Core block
    var expiredMillisecond: Long,
    //Optional variables of Constructor
    var enable: Boolean = true,
    var open: Boolean = false,
    var recommend: Int = 0,
    var landsName: String = "${ChatColor.WHITE}${ownerName}의 소유지",
    var lore: MutableList<String> = mutableListOf(
        "${ownerName}의 소유지입니다.",
        "안녕하세요",
    ),
    var welcomeMessage: MutableList<String> = mutableListOf(
        "${ownerName}의 땅 입니다.",
        "어서오세요",
    ),
    var memberSpawn: Location = upCoreLocation.toCenterLocation(),
    var visitorSpawn: Location = upCoreLocation.toCenterLocation(),
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

    fun sendMessageToOnlineMembers(messages: List<String>) {
        memberMap.keys.forEach { uuid ->
            val offlinePlayer = Bukkit.getOfflinePlayer(uuid)
            messages.forEach { message ->
                if (offlinePlayer.isOnline)
                    offlinePlayer.player!!.sendMessage(message)
                else
                    bungeeApi.sendMessage(offlinePlayer.name, message)
            }
        }
    }

    fun sendMessageToOnlineMembers(message: String) {
        memberMap.keys.forEach { uuid ->
            val offlinePlayer = Bukkit.getOfflinePlayer(uuid)
            if (offlinePlayer.isOnline)
                offlinePlayer.player!!.sendMessage(message)
            else
                bungeeApi.sendMessage(offlinePlayer.name, message)
        }
    }
}