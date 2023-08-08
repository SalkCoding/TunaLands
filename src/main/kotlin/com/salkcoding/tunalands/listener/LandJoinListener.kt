package com.salkcoding.tunalands.listener

import com.salkcoding.tunalands.api.event.LandCreateEvent
import com.salkcoding.tunalands.api.event.LandJoinEvent
import com.salkcoding.tunalands.util.infoFormat
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class LandJoinListener : Listener {

    @EventHandler
    fun onCreate(event: LandCreateEvent) {
        event.player.sendMessage("/tl help 명령어로 마을 관련 명령어를 확인할 수 있습니다.".infoFormat())
    }

    @EventHandler
    fun onJoin(event: LandJoinEvent) {
        event.player.sendMessage("/tl help 명령어로 마을 관련 명령어를 확인할 수 있습니다.".infoFormat())
    }
}