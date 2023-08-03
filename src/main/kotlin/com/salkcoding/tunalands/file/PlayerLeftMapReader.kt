package com.salkcoding.tunalands.file

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.salkcoding.tunalands.tunaLands
import java.io.File
import java.util.*

object PlayerLeftMapReader {

    fun loadPlayerLeftMap(): MutableMap<UUID, Long> {
        val folder = File(tunaLands.dataFolder, "leftTime")
        if (!folder.exists())
            folder.mkdirs()

        val leftMap = mutableMapOf<UUID, Long>()
        folder.listFiles()?.forEach { file ->
            lateinit var jsonObject: JsonObject
            file.bufferedReader().use { reader ->
                jsonObject = JsonParser.parseReader(reader).asJsonObject
            }

            val uuid = UUID.fromString(jsonObject["uuid"].asString)
            val calculatedTime = jsonObject["time"].asLong
            leftMap[uuid] = calculatedTime
        }
        return leftMap
    }
}