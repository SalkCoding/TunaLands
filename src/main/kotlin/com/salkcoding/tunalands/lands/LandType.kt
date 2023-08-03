package com.salkcoding.tunalands.lands

enum class LandType {
    NORMAL, FARM;

    override fun toString(): String {
        return when (this) {
            NORMAL -> "일반 영토"
            FARM -> "농작지"
            else -> "UNKNOWN"
        }
    }
}