package com.salkcoding.tunalands.file

import com.salkcoding.tunalands.tunaLands

class PlayerLandMapAutoSaver : Runnable {

    override fun run() {
        tunaLands.logger.info("Auto-save start")
        PlayerLandMapWriter.savePlayerLandMap()
        tunaLands.logger.info("Auto-save completed")
    }
}