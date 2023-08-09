package com.salkcoding.tunalands.bungee

import com.google.gson.Gson
import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.LandType
import com.salkcoding.tunalands.metamorphosis
import com.salkcoding.tunalands.tunaLands
import fish.evatuna.metamorphosis.redis.MetamorphosisReceiveEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class NonMainServerSyncSender : Listener {
    /** Over, Nether -> Main */
    private val CHUNK_QUERY_UPDATE_REQUEST = "com.salkcoding.tunalands.chunk_query_update_request"

    /** Main -> Over, Nether
     * Does NOT require a CHUNK_QUERY_UPDATE_REQUEST to send CHUNK_QUERY_UPDATE_RESPONSE */
    private val CHUNK_QUERY_UPDATE_RESPONSE = "com.salkcoding.tunalands.chunk_query_update_response"

    // How many updates should we send in one go
    private val PARTITION_SIZE = 20

    private val gson = Gson()

    @EventHandler
    fun onMetaReceive(e: MetamorphosisReceiveEvent) {
        if (e.key == CHUNK_QUERY_UPDATE_REQUEST) {
            sendChunkAllInfo()
        }
    }

    /**
     * For sending a list of updates to sub servers
     * @param updates Map<ChunkQuery, Pair<WorldName, LandType>?>
     * if Pair is null, it means that the ChunkQuery is no longer owned by someone
     * ChunkQuery is in the format of "chunkX;chunkZ"
     */
    fun sendChunkInfo(updates: Map<String, Pair<String, LandType>?>) {
        tunaLands.logger.info("Preparing to send chunk info (n=${updates.size}) via Meta...")
        val map = updates.map { (chunkQuery, chunkInfo) ->
            ChunkQueryUpdate(
                chunkQuery = chunkQuery,
                world = chunkInfo?.first,
                landType = chunkInfo?.second
            )
        }
        sendUpdate(map)
    }

    fun sendChunkAllInfo() {
        tunaLands.logger.info("Preparing to send all chunk info via Meta...")
        val map = landManager.getLandMap().map { (_, chunk) ->
            val world = chunk.worldName
            val chunkQuery = "${chunk.xChunk};${chunk.zChunk}"
            val landType = chunk.landType

            ChunkQueryUpdate(chunkQuery, world, landType)
        }
        sendUpdate(map)
    }

    private fun sendUpdate(updates: List<NonMainServerSyncSender.ChunkQueryUpdate>) {
        val partitions = updates.chunked(PARTITION_SIZE)
        tunaLands.logger.info("Sending ChunkQueryUpdate(n=${updates.size}) partitioned into ${partitions.size} partitions(each with $PARTITION_SIZE)")
        partitions.forEach { partition ->
            val message = gson.toJson(partition)
            metamorphosis.send(CHUNK_QUERY_UPDATE_RESPONSE, message)
        }
        tunaLands.logger.info("All ChunkQueryUpdate has been queued in Meta for sending.")
    }

    data class ChunkQueryUpdate(
        val chunkQuery: String,
        val world: String?,
        val landType: LandType?
    )
}