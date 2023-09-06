package com.salkcoding.tunalands.commands.sub

import com.salkcoding.tunalands.gui.render.openRecommendGui
import com.salkcoding.tunalands.util.errorFormat
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Recommend : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isNotEmpty()) return false

        val player = sender as? Player
        if (player == null) {
            sender.sendMessage("콘솔에서는 사용할 수 없는 명령어입니다.".errorFormat())
            return true
        }

        player.openRecommendGui()
        return true
    }
}