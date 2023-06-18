package com.salkcoding.tunalands.display

import com.salkcoding.tunalands.lands.Lands
import org.bukkit.entity.EntityType
import org.bukkit.entity.TextDisplay
import org.bukkit.entity.Display.Billboard

class TimerDisplay(
    private val lands: Lands
) : Display() {

    companion object {
        const val ReadyMessage = "준비중..."
    }

    override fun create() {
        val location = lands.upCoreLocation.toCenterLocation()
        location.y += 1.5

        hologram = location.world.spawnEntity(location, EntityType.TEXT_DISPLAY) as TextDisplay
        hologram.billboard = Billboard.CENTER
        hologram.text = ReadyMessage
    }

    override fun update(): Boolean {
        try {
            if (!hologram.isPersistent)
                throw IllegalStateException("Hologram already disabled!")
        } catch (e: UninitializedPropertyAccessException) {
            throw IllegalStateException("Hologram isn't initialized!")
        }

        val builder = StringBuilder()
        // Text Line 0 (땅 이름)
        builder.append(lands.landsName).append("\n")

        // Text Line 1 (현재 연료: a개)
        builder.append("현재 연료: ${lands.fuelLeft}개").append("\n")

        // Text Line 2 (예상: a일 b시간 c분 d초 남음)
        val timeLeftInSeconds = lands.fuelLeft / lands.secondPerFuel

        if (timeLeftInSeconds > 0) {
            val days = (timeLeftInSeconds / 86400).toLong()
            val hours = ((timeLeftInSeconds / 3600) % 24).toLong()
            val minutes = ((timeLeftInSeconds / 60) % 60).toLong()
            val seconds = (timeLeftInSeconds % 60).toLong()

            val timeMessage = when {
                days > 0 -> "예상: ${days}일 ${hours}시간 ${minutes}분 ${seconds}초 남음"
                hours > 0 -> "예상: ${hours}시간 ${minutes}분 ${seconds}초 남음"
                minutes > 0 -> "예상: ${minutes}분 ${seconds}초 남음"
                seconds > 0 -> "예상: ${seconds}초 남음"
                else -> "예상: 0초 남음"
            }

            builder.append(timeMessage).append("\n")
        } else {
            builder.append("예상: 0초 남음").append("\n")
        }

        // Text Line 3 (*시간당 x개 소모 (하루에 y개)
        val fuelPerHour = lands.secondPerFuel * 3600
        val fuelPerDay = lands.secondPerFuel * 86400
        builder.append(String.format("*시간 당 %.2f개 소모 (하루에 %.2f개)", fuelPerHour, fuelPerDay))

        hologram.text = builder.toString()
        return true
    }

    override fun remove() {
        try {
            hologram.remove()
        } catch (e: UninitializedPropertyAccessException) {
            throw IllegalStateException("Hologram not created!")
        }
    }
}