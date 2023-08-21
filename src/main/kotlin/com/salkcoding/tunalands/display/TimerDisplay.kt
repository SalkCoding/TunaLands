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
        location.y += 1.2

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
            if (hologram.isDead) {
                hologram.remove()
                create()
            }
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

        // Text Line 3 (현재 땅 소속 인원: a명)
        builder.append("현재 땅 소속 인원: ${lands.memberMap.size}명\n")

        // Text Line 4 (예상: a일 b시간 c분 d초 남음)
        val timeLeftInSeconds = lands.fuelSecLeft
        if (timeLeftInSeconds > 0) {
            val days = timeLeftInSeconds / 86400
            val hours = (timeLeftInSeconds / 3600) % 24
            val minutes = (timeLeftInSeconds / 60) % 60
            val seconds = timeLeftInSeconds % 60

            val timeMessage = "예상: ${when {
                days > 0 -> "${days}일 ${hours}시간 ${minutes}분 ${seconds}초"
                hours > 0 -> "${hours}시간 ${minutes}분 ${seconds}초"
                minutes > 0 -> "${minutes}분 ${seconds}초"
                seconds > 0 -> "${seconds}초"
                else -> "0초"
            }} 남음"

            builder.append(timeMessage).append("\n")
        } else {
            builder.append("예상: 0초 남음").append("\n")
        }

        // Text Line 5 (연료 개당 가치: b시간 c분 d초 남음)

        val addSeconds = configuration.fuel.getFuelAddAmount(lands).addAmount
        if (addSeconds > 0) {
            val days = addSeconds / 86400
            val hours = (addSeconds / 3600) % 24
            val minutes = (addSeconds / 60) % 60
            val seconds = addSeconds % 60

            val timeMessage = "연료 개당 가치: ${when {
                days > 0 -> "${days}일 ${hours}시간 ${minutes}분 ${seconds}초"
                hours > 0 -> "${hours}시간 ${minutes}분 ${seconds}초"
                minutes > 0 -> "${minutes}분 ${seconds}초"
                seconds > 0 -> "${seconds}초"
                else -> "0초"
            }}"
            builder.append(timeMessage)
        } else {
            builder.append("연료 개당 가치: 0초")
        }

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