package com.salkcoding.tunalands.display

import org.bukkit.entity.TextDisplay

abstract class Display {

    protected lateinit var hologram: TextDisplay
    var isPause: Boolean = false

    open fun create() {
        throw NotImplementedError("Create method not implemented")
    }

    open fun update() {
        throw NotImplementedError("Update method not implemented")
    }

    open fun remove() {
        throw NotImplementedError("Remove method not implemented")
    }

    open fun isAlive(): Boolean {
        throw NotImplementedError("isAlive method not implemented")
    }

    fun setMessage(vararg messages: String) {
        try {
            hologram.text = messages.joinToString("\n")
        } catch (e: UninitializedPropertyAccessException) {
            throw IllegalStateException("Hologram isn't initialized!")
        }
    }
}