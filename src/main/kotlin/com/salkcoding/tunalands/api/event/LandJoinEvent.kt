package com.salkcoding.tunalands.api.event

import com.salkcoding.tunalands.lands.Lands
import com.salkcoding.tunalands.lands.Rank
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class LandJoinEvent(
    val lands: Lands,
    val player: Player,
    val rank: Rank
) : Event() {

    companion object {
        private val handler = HandlerList()
    }

    override fun getHandlers(): HandlerList = handler
}