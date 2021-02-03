package com.salkcoding.tunalands.util

import com.salkcoding.tunalands.data.lands.Rank
import org.bukkit.ChatColor

fun String.infoFormat(): String {
    return "${ChatColor.WHITE}[${ChatColor.GREEN}!${ChatColor.WHITE}] ${ChatColor.RESET}$this"
}

fun String.warnFormat(): String {
    return "${ChatColor.WHITE}[${ChatColor.YELLOW}!${ChatColor.WHITE}] ${ChatColor.RESET}$this"
}

fun String.errorFormat(): String {
    return "${ChatColor.WHITE}[${ChatColor.RED}!${ChatColor.WHITE}] ${ChatColor.RESET}$this"
}

fun Boolean.toColoredText(): String {
    return when {
        this -> "${ChatColor.GREEN}허용"
        else -> "${ChatColor.RED}금지"
    }
}

fun Rank.toColoredText(): String {
    return when(this){
        Rank.OWNER -> "${ChatColor.GOLD}소유자"
        Rank.DELEGATOR -> "${ChatColor.YELLOW}관리 대리인"
        Rank.PARTTIMEJOB -> "${ChatColor.GRAY}알바"
        Rank.MEMBER -> "${ChatColor.GREEN}멤버"
        Rank.VISITOR -> "${ChatColor.GRAY}방문자"
    }
}