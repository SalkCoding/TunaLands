package com.salkcoding.tunalands.api.event

import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class LandGUIOpenEvent(
    val player: Player,
    val type: GUIType
) : Event() {

    companion object {
        private val handler = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList = handler
    }

    override fun getHandlers(): HandlerList = handler

    enum class GUIType {
        BAN_LIST, MAIN, RECOMMEND, SHOP, USER_LIST, VISIT,
        DELEGATOR_SETTING, MEMBER_SETTING, PARTIMEJOB_SETTING,
        SETTING, VISITOR_SETTING
    }
}