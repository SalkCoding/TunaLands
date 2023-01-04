package com.salkcoding.tunalands.display

import eu.decentsoftware.holograms.api.holograms.Hologram

open class Display {

    protected lateinit var hologram: Hologram
    var pause: Boolean = false

    open fun create() {
        throw NotImplementedError("Create method not implemented")
    }

    open fun update(): Boolean {
        throw NotImplementedError("Update method not implemented")
    }

    open fun pause() {
        pause = true
    }

    open fun resume() {
        pause = false
    }

    open fun remove() {
        throw NotImplementedError("Create method not implemented")
    }
}