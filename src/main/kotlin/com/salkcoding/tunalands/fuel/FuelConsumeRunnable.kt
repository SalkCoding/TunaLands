package com.salkcoding.tunalands.fuel

import com.salkcoding.tunalands.configuration
import com.salkcoding.tunalands.displayManager
import com.salkcoding.tunalands.file.ImposeTimeReader
import com.salkcoding.tunalands.lands.Lands
import com.salkcoding.tunalands.util.infoFormat
import com.salkcoding.tunalands.util.warnFormat
import org.bukkit.ChatColor
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class FuelConsumeRunnable(private val playerLandMap: ConcurrentHashMap<UUID, Lands>) : Runnable {

    private var nextImposeTime = ImposeTimeReader.loadImposeTime()

    init {
        //첫 실행이라 한번도 부과해본적이 없다면
        if (nextImposeTime == 0L) {
            //06시 밀리세컨드로 세팅
            nextImposeTime = Calendar.getInstance().apply {
                //이미 오전 설정된 시간이 지났다면, 다음날 해당 시간으로
                if (this.get(Calendar.HOUR_OF_DAY) >= configuration.fuel.imposeTime)
                    this.add(Calendar.DATE, 1)
                //아니면 그냥 시간만 설정
                this.set(Calendar.HOUR_OF_DAY, configuration.fuel.imposeTime)
                this.set(Calendar.MINUTE, 0)
                this.set(Calendar.SECOND, 0)
            }.timeInMillis
        }
    }

    fun getNextImposeTime() = nextImposeTime

    fun impose() {
        playerLandMap.forEach { (_, lands) ->
            lands.sendMessageToOnlineMembers("연료가 차감되었습니다.".infoFormat())
            if (lands.enable) {
                lands.fuelLeft -= lands.dayPerFuel//연료 차감
                if (lands.fuelLeft <= 0) {//만료
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
            } else {
                displayManager.pauseDisplayIfNotPaused(lands)?.setMessage(
                    "${ChatColor.RED}비활성화 ${ChatColor.WHITE}상태",
                    "${ChatColor.GOLD}연료${ChatColor.WHITE}를 사용하여 ${ChatColor.GREEN}재활성화 ${ChatColor.WHITE}해야합니다!"
                )
            }
        }
    }

    //1분마다 체크
    override fun run() {
        //다음 부과 시간이 안되었다면 리턴
        if (System.currentTimeMillis() < nextImposeTime) return
        //다음 날 설정된 시간이라면 부과 (하루가 지났다면)
        impose()

        //시간 업데이트 다음날 이 시간
        nextImposeTime += 86400000
    }
}