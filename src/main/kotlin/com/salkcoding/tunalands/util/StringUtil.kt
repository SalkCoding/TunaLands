package com.salkcoding.tunalands.util

import org.bukkit.ChatColor

fun String.infoFormat(): String {
    return "${ChatColor.WHITE}[${ChatColor.GREEN}!${ChatColor.WHITE}] ${ChatColor.RESET}$this"
}

fun String.warnFormat(): String {
    return "${ChatColor.WHITE}[${ChatColor.GREEN}!${ChatColor.WHITE}] ${ChatColor.RESET}$this"
}

fun String.errorFormat(): String {
    return "${ChatColor.WHITE}[${ChatColor.GREEN}!${ChatColor.WHITE}] ${ChatColor.RESET}$this"
}

fun String.consoleFormat(): String {
    return "[TunaLands] $this"
}