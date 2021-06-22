package com.salkcoding.tunalands.commands.sub

import com.salkcoding.tunalands.gui.render.openRecommendGui
import com.salkcoding.tunalands.util.errorFormat
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Recommend : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (label == "recommend" && args.isEmpty()) {
            val player = sender as? Player
            if (player != null) {
                player.openRecommendGui()
            } else sender.sendMessage("콘솔에서는 사용할 수 없는 명령어입니다.".errorFormat())
            return true
        }
        return true
    }
}