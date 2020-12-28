package com.salkcoding.tunalands.commands

import com.salkcoding.tunalands.gui.render.openMainGui
import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.util.consoleFormat
import com.salkcoding.tunalands.util.errorFormat
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class LandCommandHandler : CommandExecutor {

    private val commandMap = HashMap<String, CommandExecutor>()

    //IgnoreCases
    fun register(command: String, executor: CommandExecutor) {
        val lowerCase = command.toLowerCase()
        if (lowerCase !in commandMap) {
            commandMap[lowerCase] = executor
            Bukkit.getLogger().info("Command $lowerCase registered".consoleFormat())
        } else
            throw IllegalArgumentException("Command: $lowerCase is already registered.")
    }

    //ignoreCases
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        //Main command
        /*if (args.isEmpty()) {
            val player = sender as? Player
            if (player != null) {
                if (landManager.hasLand(player))
                    player.openMainGui()
                else
                    player.sendMessage("Only player, has lands can use this command".errorFormat())
            } else {
                sender.sendMessage("Only player can use this command".errorFormat())
            }
            return true
        }*/
        if (args.isEmpty()) return false

        //Sub command
        val newLabel = args[0].toLowerCase()
        if (newLabel in commandMap) {
            val newArgs = args.toMutableList()
            newArgs.removeFirst()
            return commandMap[newLabel]!!.onCommand(sender, command, newLabel, newArgs.toTypedArray())
        }
        return false
    }
}