package com.salkcoding.tunalands.display

import com.gmail.filoghost.holographicdisplays.api.HologramsAPI
import com.gmail.filoghost.holographicdisplays.api.line.TextLine
import com.salkcoding.tunalands.lands.Lands
import com.salkcoding.tunalands.tunaLands
import org.bukkit.Bukkit
import org.bukkit.ChatColor

class TimerDisplay(
    private val lands: Lands
) : Display() {

    override fun create() {
        val location = lands.upCoreLocation.toCenterLocation()
        location.y += 1.5

        hologram = HologramsAPI.createHologram(tunaLands, location)
        hologram.appendTextLine(lands.landsName)
        hologram.appendTextLine("준비중...")
    }

    override fun update(): Boolean {
        try {
            if (hologram.isDeleted)
                throw IllegalStateException("Hologram already deleted!")
        } catch (e: UninitializedPropertyAccessException) {
            throw IllegalStateException("Hologram not initialized!")
        }

        val hologramTexts: MutableList<String> = mutableListOf()
        // Text Line 0 (땅 이름)
        hologramTexts.add(lands.landsName)

        // Text Line 1 (현재 연료: a개)
        hologramTexts.add("현재 연료: ${lands.fuelLeft}개")

        // Text Line 2 (예상: a일 b시간 c분 d초 남음)
        val timeLeftInMilliseconds = lands.getEstimatedMillisecondsLeftWithCurrentFuel()

        if (timeLeftInMilliseconds > 0) {
            val days = timeLeftInMilliseconds / 86400000
            val hours = (timeLeftInMilliseconds / 3600000) % 24
            val minutes = (timeLeftInMilliseconds / 60000) % 60
            val seconds = (timeLeftInMilliseconds / 1000) % 60

            val timeMessage = when {
                days > 0 -> "예상: ${days}일 ${hours}시간 ${minutes}분 ${seconds}초 남음"
                hours > 0 -> "예상: ${hours}시간 ${minutes}분 ${seconds}초 남음"
                minutes > 0 -> "예상: ${minutes}분 ${seconds}초 남음"
                seconds > 0 -> "예상: ${seconds}초 남음"
                else -> "예상: 0초 남음"
            }

            hologramTexts.add(timeMessage)
        } else {
            hologramTexts.add("예상: 0초 남음")
        }

        // Text Line 3 (*시간당 x개 소모 (하루에 y개)
        // Milliseconds in a day = 86400000
        val fuelPerHour = 3600000.0 / lands.getMillisecondsPerFuel()
        val fuelPerDay = 86400000.0 / lands.getMillisecondsPerFuel()
        hologramTexts.add(String.format("*시간 당 %.2f개 소모 (하루에 %.2f개)", fuelPerHour, fuelPerDay))


        // Sync invoke
        // Change Hologram
//        Bukkit.getScheduler().runTask(tunaLands, Runnable {
//            //Flicker prevent
//            var removeLinesFrom: Int? = null
//            for (lineNum in 0 until hologram.size()) {
//                val line = hologram.getLine(lineNum) as TextLine
//                line.text = hologramTexts[lineNum]
//                if (lineNum < hologramTexts.size) {
//                    if (line.text != hologramTexts[lineNum]) {
//                        // if line text is same, leave it
//                        line.text = hologramTexts[lineNum]
//                    }
//                } else {
//                    removeLinesFrom = lineNum
//                    break
//                }
//            }
//
////            // Remove unused lines
//            if (removeLinesFrom != null) {
//                for (ignored in removeLinesFrom until hologram.size()) {
//                    hologram.removeLine(removeLinesFrom)
//                }
//            }
//        })

//        Bukkit.getScheduler().runTask(tunaLands, Runnable {
            //Flicker prevent
            var lineNum: Int = 0
            hologramTexts.forEach { text ->
                if (lineNum < hologram.size()) {
                    val line = hologram.getLine(lineNum) as TextLine
                    if (line.text != text) {
                        line.text = text
                    }
                } else {
                    hologram.appendTextLine(text)
                }
                lineNum++
            }

            while (hologram.size() - 1 > lineNum) {
                hologram.removeLine(hologram.size() - 1)
            }
//        })

        return true
    }

    override fun pause() {
        val line = hologram.getLine(1) as TextLine
        line.text = "${ChatColor.RED}비활성화"
        pause = true
    }

    override fun resume() {
        update()
        pause = false
    }

    override fun remove() {
        try {
            hologram.delete()
        } catch (e: UninitializedPropertyAccessException) {
            throw IllegalStateException("Hologram not created!")
        }
    }
}