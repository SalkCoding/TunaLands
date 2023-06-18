package com.salkcoding.tunalands.alarm

import com.salkcoding.tunalands.lands.Lands
import com.salkcoding.tunalands.tunaLands
import org.bukkit.Bukkit

class AlarmManager {

    private val alarmMap = mutableMapOf<Lands, Alarm>()
    private val task = Bukkit.getScheduler().runTaskTimerAsynchronously(tunaLands, Runnable {
        alarmMap.values.forEach { alarm ->
            alarm.alarm()
        }
    }, 1200, 1200)

    fun registerAlarm(lands: Lands) {
        if (lands !in alarmMap)
            alarmMap[lands] = FuelAlarm(lands)
    }

    fun resetAlarm(lands: Lands) {
        if (lands !in alarmMap)
            return

        alarmMap[lands]!!.reset()
    }

    fun unregisterAlarm(lands: Lands) {
        alarmMap.remove(lands)
    }

    fun dispose() {
        task.cancel()
    }

}