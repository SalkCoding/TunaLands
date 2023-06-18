package com.salkcoding.tunalands.alarm

import com.salkcoding.tunalands.lands.Lands
import com.salkcoding.tunalands.util.warnFormat

class FuelAlarm(val lands: Lands) : Alarm {

    private var alarm3hours: Boolean = true
    private var alarm2hours: Boolean = true
    private var alarm1hours: Boolean = true
    private var alarm30minutes: Boolean = true
    private var alarm10minutes: Boolean = true
    private var alarm5minutes: Boolean = true
    private var alarm1minute: Boolean = true

    override fun alarm() {
        //All alarms were notified
        if (!alarm1minute) return

        val left = lands.fuelLeft / lands.secondPerFuel

        val days = (left / 86400).toLong()

        if (days > 0) return

        val hours = ((left / 3600) % 24).toLong()
        if (hours == 2L && alarm3hours) {
            alarm3hours = false
            lands.sendMessageToOnlineMembers("땅 보호가 3시간 후 만료됩니다!".warnFormat())
            return
        } else if (hours == 1L && alarm2hours) {
            alarm2hours = false
            lands.sendMessageToOnlineMembers("땅 보호가 2시간 후 만료됩니다!".warnFormat())
            return
        } else if (hours == 0L && alarm1hours) {
            alarm1hours = false
            lands.sendMessageToOnlineMembers("땅 보호가 1시간 후 만료됩니다!".warnFormat())
            return
        }

        if (hours > 0) return

        val minutes = ((left / 60) % 60).toLong()
        if (minutes == 29L && alarm30minutes) {
            alarm30minutes = false
            lands.sendMessageToOnlineMembers("땅 보호가 30분 후 만료됩니다!".warnFormat())
            return
        } else if (minutes == 9L && alarm10minutes) {
            alarm10minutes = false
            lands.sendMessageToOnlineMembers("땅 보호가 10분 후 만료됩니다!".warnFormat())
            return
        } else if (minutes == 4L && alarm5minutes) {
            alarm5minutes = false
            lands.sendMessageToOnlineMembers("땅 보호가 5분 후 만료됩니다!".warnFormat())
            return
        } else if (minutes == 0L && alarm1minute) {
            alarm1minute = false
            lands.sendMessageToOnlineMembers("땅 보호가 1분 후 만료됩니다!".warnFormat())
            return
        }
    }

    override fun reset() {
        val left = lands.fuelLeft / lands.secondPerFuel

        //n days m hours k minutes
        val hours = ((left / 3600) % 24).toLong()
        if (hours >= 3) alarm3hours = true
        if (hours >= 2) alarm2hours = true
        if (hours >= 1) alarm1hours = true

        if (hours < 0) {//0 hours n minutes
            val minutes = ((left / 60) % 60).toLong()
            if (minutes >= 30) alarm30minutes = true
            if (minutes >= 10) alarm10minutes = true
            if (minutes >= 5) alarm5minutes = true
            if (minutes >= 1) alarm1minute = true
        } else {//n hours m minutes
            alarm30minutes = true
            alarm10minutes = true
            alarm5minutes = true
            alarm1minute = true
        }
    }

}