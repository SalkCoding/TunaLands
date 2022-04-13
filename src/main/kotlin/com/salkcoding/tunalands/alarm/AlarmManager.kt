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
            alarmMap[lands] = Alarm(lands)
    }

    fun resetAlarm(lands: Lands) {
        if (lands !in alarmMap)
            return

        val alarm = alarmMap[lands]!!
        val expired = lands.getEstimatedMillisecondsLeftWithCurrentFuel()

        //n days m hours k minutes
        val hours = (expired / 3600000) % 24
        if (hours >= 3) alarm.alarm3hours = true
        if (hours >= 2) alarm.alarm2hours = true
        if (hours >= 1) alarm.alarm1hours = true

        if (hours < 0) {//0 hours n minutes
            val minutes = (expired / 60000) % 60
            if (minutes >= 30) alarm.alarm30minutes = true
            if (minutes >= 10) alarm.alarm10minutes = true
            if (minutes >= 5) alarm.alarm5minutes = true
            if (minutes >= 1) alarm.alarm1minute = true
        } else {//n hours m minutes
            alarm.alarm30minutes = true
            alarm.alarm10minutes = true
            alarm.alarm5minutes = true
            alarm.alarm1minute = true
        }
    }

    fun unregisterAlarm(lands: Lands) {
        alarmMap.remove(lands)
    }

    fun dispose() {
        task.cancel()
    }

}