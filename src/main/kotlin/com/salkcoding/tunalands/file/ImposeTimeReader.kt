package com.salkcoding.tunalands.file

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.salkcoding.tunalands.tunaLands
import java.io.File

object ImposeTimeReader {

    fun loadImposeTime(): Long {
        val file = File(tunaLands.dataFolder, "imposeTime.json")
        if (!file.exists()) return 0

        lateinit var jsonObject: JsonObject
        file.bufferedReader().use { reader ->
            jsonObject = JsonParser.parseReader(reader).asJsonObject
        }
        return jsonObject["time"].asLong
    }

}