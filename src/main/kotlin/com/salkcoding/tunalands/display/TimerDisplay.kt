package com.salkcoding.tunalands.display

import com.salkcoding.tunalands.configuration
import com.salkcoding.tunalands.lands.LandType
import com.salkcoding.tunalands.lands.Lands
import org.bukkit.ChatColor
import org.bukkit.entity.Display.Billboard
import org.bukkit.entity.EntityType
import org.bukkit.entity.TextDisplay

class TimerDisplay(
    private val lands: Lands
) : Display() {

    companion object {
        const val ReadyMessage = "준비중..."
    }

    override fun create() {
        val location = lands.upCoreLocation.toCenterLocation()
        location.y += 1.0

        hologram = location.world.spawnEntity(location, EntityType.TEXT_DISPLAY) as TextDisplay
        hologram.billboard = Billboard.CENTER
        hologram.text = when (lands.enable) {
            true -> ReadyMessage
            false -> {
                isPause = true
                "${ChatColor.RED}비활성화 ${ChatColor.WHITE}상태" +
                        "\n${ChatColor.GOLD}연료${ChatColor.WHITE}를 사용하여 ${ChatColor.GREEN}재활성화 ${ChatColor.WHITE}해야합니다!"
            }
        }
    }

    override fun update() {
        try {
            if (hologram.isDead) create()
        } catch (e: UninitializedPropertyAccessException) {
            throw IllegalStateException("Hologram isn't initialized!")
        }

        if (isPause) return

        val builder = StringBuilder()
        // Text Line 0 (땅 이름)
        builder.append("${lands.landsName}\n")

        // Text Line 1 (최대 땅 점유 개수)
        builder.append("땅: ${lands.landMap.size}/${configuration.protect.getMaxOccupied(lands).maxChunkAmount}\n")

        // Text Line 2 (최대 농작지 점유 개수)
        builder.append("농작지: ${lands.landMap.filter { it.value == LandType.FARM }.size}/${configuration.farm.limitOccupied}\n")

        // Text Line 3 (현재 연료: a개)
        builder.append("현재 연료: ${lands.fuelLeft}개\n")

        // Text Line 4 (예상: a일 b시간 c분 d초 남음)
        val timeLeftInMilliseconds = lands.getExpiredDateToMilliseconds()
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

            builder.append(timeMessage).append("\n")
        } else {
            builder.append("예상: 0초 남음").append("\n")
        }

        // Text Line 5 (* 하루당 x개 소모)
        builder.append("* 하루당 ${lands.dayPerFuel}개 소모")

        hologram.text = builder.toString()
    }

    override fun remove() {
        try {
            hologram.remove()
        } catch (e: UninitializedPropertyAccessException) {
            throw IllegalStateException("Hologram not created!")
        }
    }
}