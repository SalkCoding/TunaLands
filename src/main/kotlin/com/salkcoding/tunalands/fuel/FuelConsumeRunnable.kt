package com.salkcoding.tunalands.fuel

import com.salkcoding.tunalands.configuration
import com.salkcoding.tunalands.displayManager
import com.salkcoding.tunalands.lands.Lands
import com.salkcoding.tunalands.util.warnFormat
import org.bukkit.ChatColor
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class FuelConsumeRunnable(private val playerLandMap: ConcurrentHashMap<UUID, Lands>) : Runnable {
    override fun run() {
        playerLandMap.forEach { (_, lands) ->
            val timeToConsumeFuel = lands.nextTimeFuelNeedsToBeConsumed
            val present = LocalDateTime.now()

            if (lands.enable && present.isAfter(timeToConsumeFuel)) {
                // 새롭게 연료를 소비해야되는 시간이 됨

                if (lands.fuelLeft > 0) {
                    // minutesPerFuel 이 소숫점일 수도 있어서 밀리초로 변환 후 적용합니다.
                    // 예: minutesPerFuel 이 0.01 일 경우, 연료 하나당 0.01분을 커버해줍니다.
                    // => 0.01분 = 0.6초 = 600밀리초
                    val secondsPerFuel = configuration.fuel.getFuelRequirement(lands).secondsPerFuel
                    val msPerFuel = (secondsPerFuel * 1000).toLong()

                    lands.nextTimeFuelNeedsToBeConsumed = present.plus(msPerFuel, ChronoUnit.MILLIS)
                    lands.fuelLeft--
                } else {
                    lands.sendMessageToOnlineMembers(
                        listOf(
                            "땅 보호 기간이 만료되어 비활성화 상태로 전환됩니다!".warnFormat(),
                            "코어에 연료를 넣어 활성화하지 않을 경우 모든 블럭과의 상호작용이 불가능합니다!".warnFormat()
                        )
                    )
                    displayManager.pauseDisplay(lands)?.setMessage(
                        "${ChatColor.RED}비활성화 ${ChatColor.WHITE}상태",
                        "${ChatColor.GOLD}연료${ChatColor.WHITE}를 사용하여 ${ChatColor.GREEN}재활성화 ${ChatColor.WHITE}해야합니다!"
                    )
                    lands.enable = false
                }
            } else if (!lands.enable) {
                displayManager.pauseDisplayIfNotPaused(lands)?.setMessage(
                    "${ChatColor.RED}비활성화 ${ChatColor.WHITE}상태",
                    "${ChatColor.GOLD}연료${ChatColor.WHITE}를 사용하여 ${ChatColor.GREEN}재활성화 ${ChatColor.WHITE}해야합니다!"
                )
            }
        }
    }
}