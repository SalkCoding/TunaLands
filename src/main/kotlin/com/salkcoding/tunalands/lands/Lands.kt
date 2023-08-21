package com.salkcoding.tunalands.lands

import com.salkcoding.tunalands.bukkitLinkedAPI
import com.salkcoding.tunalands.configuration
import com.salkcoding.tunalands.lands.setting.DelegatorSetting
import com.salkcoding.tunalands.lands.setting.LandSetting
import com.salkcoding.tunalands.tunaLands
import com.salkcoding.tunalands.util.ObservableMap
import com.salkcoding.tunalands.util.infoFormat
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Chunk
import org.bukkit.Location
import java.util.*
import kotlin.math.roundToLong

data class Lands(
    var ownerName: String,
    var ownerUUID: UUID,
    val landMap: HashMap<String, LandType>,
    val landHistory: LandHistory,
    val upCoreLocation: Location, //Chest
    val downCoreLocation: Location, //Core block
    var fuelSecLeft: Long, // amount of fuel left
    //Optional variables of Constructor
    var enable: Boolean = true,
    var open: Boolean = true,
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
    val memberMap: ObservableMap<UUID, MemberData> = ObservableMap(
        plugin = tunaLands,
        map = mutableMapOf(),
        onChange = object : ObservableMap.Observed<UUID, MemberData> {
            override fun syncChanges(newMap: MutableMap<UUID, MemberData>) {
                val message = newMap.map { (uuid, data) ->
                    "${uuid},${Bukkit.getOfflinePlayer(uuid).name},${data.rank}"
                }.joinToString(";")

                tunaLands.broadcastLandMembersRunnable.queue.offer(message)
            }
        }),
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
        val zChunk: Int,
        var landType: LandType
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
                    bukkitLinkedAPI.sendMessageAcrossServer(offlinePlayer.name, message)
            }
        }
    }

    fun sendMessageToOnlineMembers(message: String) {
        memberMap.keys.forEach { uuid ->
            val offlinePlayer = Bukkit.getOfflinePlayer(uuid)
            if (offlinePlayer.isOnline)
                offlinePlayer.player!!.sendMessage(message)
            else
                bukkitLinkedAPI.sendMessageAcrossServer(offlinePlayer.name, message)
        }
    }

    fun fuelRecomputeAndSave(beforeMemberCnt: Int, afterMemberCnt: Int) {
        fuelSecLeft = fuelCompute(beforeMemberCnt,afterMemberCnt)
        sendMessageToOnlineMembers("인원이 변동되어, 예상 연료 시간이 변경되었습니다.".infoFormat())
    }

    fun fuelCompute(beforeMemberCnt: Int, afterMemberCnt: Int):Long {
        val currentFuelSec = this.fuelSecLeft.toDouble()
        val addAmountBefore = configuration.fuel.getFuelAddAmount(beforeMemberCnt)
        val addAmountAfter = configuration.fuel.getFuelAddAmount(afterMemberCnt)

        val fuelSecToFuel = currentFuelSec / addAmountBefore.addAmount
        val newFuelSecLeft = fuelSecToFuel * addAmountAfter.addAmount

        return newFuelSecLeft.roundToLong()
    }

    override fun toString(): String {
        val normal = landMap.values.count { it == LandType.NORMAL }
        val delegator = memberMap.values.count { it.rank == Rank.DELEGATOR }
        val member = memberMap.values.count { it.rank == Rank.MEMBER }
        val parttime = memberMap.values.count { it.rank == Rank.PARTTIMEJOB }
        val visitor = memberMap.values.count { it.rank == Rank.VISITOR }
        return "{ownerName: $ownerName, " +
                "ownerUUID: $ownerUUID, " +
                "land: ${landMap.size}" +
                "(Normal: ${normal}, " +
                "(Farm: ${landMap.size - normal}), " +
                "fuelLeft: $fuelSecLeft, " +
                "member: ${memberMap.size}" +
                "(owner: 1, " +
                "delegator: $delegator, " +
                "member: $member, " +
                "parttime: $parttime, " +
                "visitor: $visitor), " +
                "ban: ${banMap.size}" +
                "}"
    }

}