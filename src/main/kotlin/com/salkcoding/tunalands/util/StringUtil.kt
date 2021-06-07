package com.salkcoding.tunalands.util

import com.salkcoding.tunalands.data.lands.Rank
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player

fun String.announceFormat(): String {
    return "\ue4db ${ChatColor.RESET}$this"
}

fun String.infoFormat(): String {
    return "\ue4dc ${ChatColor.RESET}$this"
}

fun String.warnFormat(): String {
    return "\ue4dd ${ChatColor.RESET}$this"
}

fun String.errorFormat(): String {
    return "\ue4de ${ChatColor.RESET}$this"
}

fun Player.sendErrorTipMessage(message: String) {
    //this.sendMessage(message.errorFormat())
    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "sendwarn ${this.name} $message")
}

fun Boolean.toColoredText(): String {
    return when (this) {
        true -> "${ChatColor.GREEN}허용"
        false -> "${ChatColor.RED}금지"
    }
}

fun Rank.toColoredText(): String {
    return when (this) {
        Rank.OWNER -> "${ChatColor.GOLD}소유자"
        Rank.DELEGATOR -> "${ChatColor.YELLOW}관리 대리인"
        Rank.PARTTIMEJOB -> "${ChatColor.GRAY}알바"
        Rank.MEMBER -> "${ChatColor.GREEN}멤버"
        Rank.VISITOR -> "${ChatColor.GRAY}방문자"
    }
}