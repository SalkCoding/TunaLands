package com.salkcoding.tunalands.display

import org.bukkit.entity.TextDisplay

abstract class Display {

    protected lateinit var hologram: TextDisplay
    var isPause: Boolean = false

    open fun create() {
        throw NotImplementedError("Create method not implemented")
    }

    open fun update(): Boolean {
        throw NotImplementedError("Update method not implemented")
    }

    open fun remove() {
        throw NotImplementedError("Create method not implemented")
    }

    fun setMessage(vararg messages: String) {
        hologram.text = messages.joinToString("\n")
    }
}