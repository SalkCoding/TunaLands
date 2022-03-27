package com.salkcoding.tunalands.display

import com.gmail.filoghost.holographicdisplays.api.Hologram

abstract class Display {

    protected lateinit var hologram: Hologram
    var isPaused: Boolean = false

    open fun create() {
        throw NotImplementedError("Create method not implemented")
    }

    open fun update(): Boolean {
        throw NotImplementedError("Update method not implemented")
    }

    open fun pause() {
        isPaused = true
    }

    open fun resume() {
        isPaused = false
    }

    open fun remove() {
        throw NotImplementedError("Create method not implemented")
    }
}