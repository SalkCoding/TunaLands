package com.salkcoding.tunalands.fuel

import com.salkcoding.tunalands.alarmManager
import com.salkcoding.tunalands.displayManager
import com.salkcoding.tunalands.lands.Lands
import com.salkcoding.tunalands.util.warnFormat
import org.bukkit.ChatColor
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class FuelConsumeRunnable(private val playerLandMap: ConcurrentHashMap<UUID, Lands>) : Runnable {
    override fun run() {
        playerLandMap.forEach { (_, lands) ->
            if (lands.enable) {
                // 새롭게 연료를 소비해야되는 시간이 됨

                //만료
                if (lands.fuelLeft <= 0) {
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
                    alarmManager.unregisterAlarm(lands)
                } else {//연료 차감
                    lands.fuelLeft -= lands.secondPerFuel
                }
            } else {
                displayManager.pauseDisplayIfNotPaused(lands)?.setMessage(
                    "${ChatColor.RED}비활성화 ${ChatColor.WHITE}상태",
                    "${ChatColor.GOLD}연료${ChatColor.WHITE}를 사용하여 ${ChatColor.GREEN}재활성화 ${ChatColor.WHITE}해야합니다!"
                )
                alarmManager.unregisterAlarm(lands)
            }
        }
    }
}