package com.salkcoding.tunalands.display

import com.salkcoding.tunalands.lands.Lands
import eu.decentsoftware.holograms.api.DHAPI

class TimerDisplay(
    private val lands: Lands
) : Display() {

    companion object {
        val ReadyMessage = listOf("준비중...")
    }

    override fun create() {
        val location = lands.upCoreLocation.toCenterLocation()
        location.y += 1.5

        this.hologram = DHAPI.createHologram(lands.landsName, location, ReadyMessage)
    }

    override fun update(): Boolean {
        try {
            if (hologram.isDisabled)
                throw IllegalStateException("Hologram already disabled!")
        } catch (e: UninitializedPropertyAccessException) {
            throw IllegalStateException("Hologram isn't initialized!")
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

        DHAPI.setHologramLines(hologram, hologramTexts)
        return true
    }

    override fun pause() {
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