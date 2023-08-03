package com.salkcoding.tunalands.file

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.salkcoding.tunalands.tunaLands
import java.io.File
import java.util.*

object PlayerRecommendMapReader {

    fun loadPlayerRecommendMap(): MutableMap<UUID, Long> {
        val folder = File(tunaLands.dataFolder, "recommendTime")
        if (!folder.exists())
            folder.mkdirs()

        val recommendMap = mutableMapOf<UUID, Long>()
        folder.listFiles()?.forEach { file ->
            lateinit var jsonObject: JsonObject
            file.bufferedReader().use { reader ->
                jsonObject = JsonParser.parseReader(reader).asJsonObject
            }

            val uuid = UUID.fromString(jsonObject["uuid"].asString)
            val calculatedTime = jsonObject["time"].asLong
            recommendMap[uuid] = calculatedTime
        }
        return recommendMap
    }
}