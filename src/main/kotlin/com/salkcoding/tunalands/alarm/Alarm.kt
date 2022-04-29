package com.salkcoding.tunalands.alarm

import com.salkcoding.tunalands.configuration
import com.salkcoding.tunalands.lands.Lands
import com.salkcoding.tunalands.util.warnFormat
import org.bukkit.Bukkit
import java.time.Duration
import java.time.LocalDateTime
import kotlin.math.roundToLong

class Alarm(val lands: Lands) {

    var alarm3hours: Boolean = true
    var alarm2hours: Boolean = true
    var alarm1hours: Boolean = true
    var alarm30minutes: Boolean = true
    var alarm10minutes: Boolean = true
    var alarm5minutes: Boolean = true
    var alarm1minute: Boolean = true

    fun alarm() {
        //All of alarms were notified
        if (!alarm1minute) return


        val expired = lands.getEstimatedMillisecondsLeftWithCurrentFuel()
        val days = expired / 86400000
        if (days > 0) return

        val hours = (expired / 3600000) % 24
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

        val minutes = (expired / 60000) % 60
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

}