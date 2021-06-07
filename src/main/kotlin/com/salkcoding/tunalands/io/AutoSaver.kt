package com.salkcoding.tunalands.io

import com.salkcoding.tunalands.tunaLands

class AutoSaver : Runnable {

    override fun run() {
        tunaLands.logger.info("Auto-save start")
        JsonWriter.savePlayerLandMap()
        tunaLands.logger.info("Auto-save completed")
    }
}