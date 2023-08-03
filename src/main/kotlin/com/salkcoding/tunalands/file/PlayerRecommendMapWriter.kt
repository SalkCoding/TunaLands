package com.salkcoding.tunalands.file

import com.google.gson.JsonObject
import com.salkcoding.tunalands.recommendManager
import com.salkcoding.tunalands.tunaLands
import java.io.File
import java.util.*

object PlayerRecommendMapWriter {

    fun savePlayerRecommendMap(): MutableMap<UUID, Long> {
        val folder = File(tunaLands.dataFolder, "recommendTime")
        if (!folder.exists())
            folder.mkdirs()

        val recommendMap = recommendManager.getRecommendMap()
        recommendMap.forEach { (uuid, calculatedTime) ->
            val file = File(folder, "${uuid}.json")
            if (!file.exists())
                file.createNewFile()

            val jsonObject = JsonObject()
            jsonObject.addProperty("uuid", uuid.toString())
            jsonObject.addProperty("time", calculatedTime)
            file.bufferedWriter().use { writer ->
                writer.write(jsonObject.toString())
            }
        }
        return recommendMap
    }
}