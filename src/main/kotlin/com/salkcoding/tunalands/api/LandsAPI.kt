package com.salkcoding.tunalands.api

import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.Lands
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

object LandsAPI {

    fun getPlayerRank(uuid: UUID): Rank? {
        return when (val lands = landManager.getPlayerLands(uuid)) {
            null -> null
            else -> lands.memberMap[uuid]!!.rank
        }
    }

    fun getPlayerLandsMemberList(uuid: UUID): List<String>? {
        return when (val lands = landManager.getPlayerLands(uuid)) {
            null -> null
            else -> {
                val memberMap = lands.memberMap
                val list = mutableListOf<String>()
                memberMap.keys.forEach { member ->
                    Bukkit.getOfflinePlayer(member).name?.let { list.add(it) }
                }
                list
            }
        }
    }

    /**
     * Returns the rank of the player for the Lands the player is currently located at.
     * If the player is not located at a lands or has no rank, it will return null.
     *
     * @param  p  target player
     * @return the rank of the player at current location
     */
    fun getPlayerRankAtChunk(p: Player): Rank? {
        val land = landManager.getLandsWithChunk(p.location.chunk) ?: return null

        for (member: MutableMap.MutableEntry<UUID, Lands.MemberData> in land.memberMap) {
            if (p.uniqueId == member.key) {
                return member.value.rank
            }
        }
        return null
    }

    /**
     * Returns a list of online members for the lands at current player's location.
     * If the player is not located at a lands or has no rank, it will return an empty list.
     *
     * @param  p  target player
     * @return the rank of the player at current location
     */
    fun getOnlineLandsMembersAtChunk(p: Player): List<Player> {
        val land = landManager.getLandsWithChunk(p.location.chunk) ?: return emptyList()
        val onlineMembers = mutableListOf<Player>()

        for (member: MutableMap.MutableEntry<UUID, Lands.MemberData> in land.memberMap) {
            val onlineMember = Bukkit.getPlayer(member.key)
            if (onlineMember != null) {
                onlineMembers.add(onlineMember)
            }
        }

        return onlineMembers
    }

    /**
     * Returns the UUID of the owner that owns the chunk at x, z in world worldName
     * chunkData string is in the format of chunk_x:chunk_z
     *
     * @param  worldName  world name
     * @param  chunkData  chunk_x:chunk_z
     * @return the UUID of the owner at specified chunk
     */
    fun getOwnerUUIDWithChunkQuery(worldName: String, chunkData: String): UUID? {
        val land = landManager.getLandsWithChunkQuery(worldName, chunkData) ?: return null
        return land.ownerUUID
    }
}