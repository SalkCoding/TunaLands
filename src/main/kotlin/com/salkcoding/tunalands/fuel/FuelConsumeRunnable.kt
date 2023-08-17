package com.salkcoding.tunalands.fuel

import com.salkcoding.tunalands.displayManager
import com.salkcoding.tunalands.lands.Lands
import com.salkcoding.tunalands.util.warnFormat
import org.bukkit.ChatColor
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class FuelConsumeRunnable(private val playerLandMap: ConcurrentHashMap<UUID, Lands>) : Runnable {

    override fun run() {
        playerLandMap.forEach { (_, lands) ->
            if (!lands.enable) return@forEach

            lands.fuelLeft -= 20//연료 차감

            if (lands.fuelLeft > 0) return@forEach

            //만료
            lands.sendMessageToOnlineMembers(
                listOf(
                    "땅 보호 기간이 만료되어 비활성화 상태로 전환됩니다!".warnFormat(),
                    "코어에 연료를 넣어 활성화하지 않을 경우 모든 블럭과의 상호작용이 불가능합니다!".warnFormat()
                )
            )
            lands.enable = false
            lands.fuelLeft = 0
            displayManager.pauseDisplay(lands)?.setMessage(
                "${ChatColor.RED}비활성화 ${ChatColor.WHITE}상태",
                "${ChatColor.GOLD}연료${ChatColor.WHITE}를 사용하여 ${ChatColor.GREEN}재활성화 ${ChatColor.WHITE}해야합니다!"
            )
        }
    }
}