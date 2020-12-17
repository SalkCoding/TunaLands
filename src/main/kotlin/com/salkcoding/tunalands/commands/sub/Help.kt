package com.salkcoding.tunalands.commands.sub

import com.salkcoding.tunalands.util.infoFormat
import com.salkcoding.tunalands.util.warnFormat
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class Help : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        sender.sendMessage("Command list".infoFormat())
        sender.sendMessage("/land, /ld: open Gui".infoFormat())
        sender.sendMessage("/ld invite (name): Invite player to protected lands".infoFormat())
        sender.sendMessage("/ld cancel (name): Cancel invitation".infoFormat())
        sender.sendMessage("/ld invitelist: Show invitation list".infoFormat())
        sender.sendMessage("/ld accept: Accept invitation".infoFormat())
        sender.sendMessage("/ld reject: Reject invitation".infoFormat())
        sender.sendMessage("/ld ban (name): Ban player from protected lands".infoFormat())
        sender.sendMessage("/ld unban (name): Unban player from protected lands".infoFormat())
        sender.sendMessage("/ld banlist: Show list of player banned".infoFormat())
        sender.sendMessage("/ld kick (name): Kick player from protected lands".infoFormat())
        sender.sendMessage("/ld spawn: TODO add description".infoFormat())
        sender.sendMessage("/ld setspawn (main/visitor): TODO add description".infoFormat())
        sender.sendMessage("/ld leave: Leave from lands".infoFormat())
        sender.sendMessage("/ld visit (name): Ask visit to player".infoFormat())
        sender.sendMessage("/ld delete: Delete all protected lands".infoFormat())
        sender.sendMessage("/ld coop (name): Make player co-administration of lands".infoFormat())
        sender.sendMessage("/ld promote (name): Promote player".infoFormat())
        sender.sendMessage("/ld demote (name): Demote player".infoFormat())
        sender.sendMessage("/ld setleader (name): Make player leader of lands".infoFormat())
        sender.sendMessage("/ld vote (name): TODO add description".infoFormat())
        if (sender.isOp) {
            sender.sendMessage("/ld debug info (name): Show list, owned lands of player".warnFormat())
        }
        return true
    }
}