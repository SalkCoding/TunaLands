package com.salkcoding.tunalands.file

import com.google.gson.JsonObject
import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.tunaLands
import java.io.File

object ImposeTimeWriter {

    fun saveImposeTime() {
        val file = File(tunaLands.dataFolder, "imposeTime.json")
        if (!file.exists()) file.createNewFile()

        val jsonObject = JsonObject()
        jsonObject.addProperty("time", landManager.getFuelConsumeRunner().getNextImposeTime())
        file.bufferedWriter().use { writer ->
            writer.write(jsonObject.toString())
        }
    }
}