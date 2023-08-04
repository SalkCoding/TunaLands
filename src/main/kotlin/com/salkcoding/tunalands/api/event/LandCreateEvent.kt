package com.salkcoding.tunalands.api.event

import com.salkcoding.tunalands.lands.Lands
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class LandCreateEvent(
    val lands: Lands,
    val player: Player,
    val location: Location
) : Event() {

    companion object {
        private val handler = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList = handler
    }

    override fun getHandlers(): HandlerList = handler
}