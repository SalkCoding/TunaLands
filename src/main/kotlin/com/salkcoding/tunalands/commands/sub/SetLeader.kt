package com.salkcoding.tunalands.commands.sub

import com.salkcoding.tunalands.bungee.BungeeSender
import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.util.errorFormat
import com.salkcoding.tunalands.util.infoFormat
import com.salkcoding.tunalands.util.warnFormat
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SetLeader : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        when {
            label == "setleader" && args.size == 1 -> {
                val player = sender as? Player
                if (player != null) {
                    val lands = landManager.getPlayerLands(player.uniqueId, Rank.OWNER)
                    if (lands != null) {
                        val data = lands.memberMap[player.uniqueId]!!
                        val targetName = args[0]
                        val targetOffline = Bukkit.getOfflinePlayerIfCached(targetName)
                        if (targetOffline == null) {
                            player.sendMessage("존재하지 않는 유저입니다!".errorFormat())
                            return true
                        }

                        if (targetOffline.uniqueId == player.uniqueId) {
                            player.sendMessage("당신은 이미 땅의 소유자입니다.".errorFormat())
                            return true
                        }

                        val targetData = lands.memberMap[targetOffline.uniqueId]
                        if (targetData != null) {
                            when (targetData.rank) {
                                Rank.DELEGATOR, Rank.MEMBER -> {
                                    if (targetOffline.isOnline) {
                                        val target = targetOffline.player!!
                                        lands.ownerName = target.name
                                        landManager.changeChunksOwner(player, target)

                                        targetData.rank = Rank.OWNER
                                        data.rank = Rank.DELEGATOR

                                        target.sendMessage("${player.name}의 땅의 소유자가 되었습니다.".infoFormat())
                                    } else {
                                        lands.ownerName = targetName
                                        landManager.changeChunksOwner(player, targetOffline)

                                        targetData.rank = Rank.OWNER
                                        data.rank = Rank.DELEGATOR

                                        BungeeSender.sendMessage(
                                            targetName,
                                            "${player.name}의 땅의 소유자가 되었습니다.".infoFormat()
                                        )
                                    }

                                    player.sendMessage("${targetName}은/는 이제 땅의 소유자입니다.".infoFormat())
                                    player.sendMessage("관리 대리인으로 강등되셨습니다.".warnFormat())

                                    lands.memberMap.forEach { (uuid, _) ->
                                        val member = Bukkit.getPlayer(uuid) ?: return@forEach
                                        member.sendMessage("${targetName}이/가 새로운 땅의 소유자가 되었습니다.".infoFormat())
                                    }
                                }
                                else -> player.sendMessage("관리 대리인과 멤버만 소유자가 될 수 있습니다.".errorFormat())
                            }
                        } else player.sendMessage("${targetName}은/는 당신의 땅에 소속되어있지 않습니다.".errorFormat())
                    } else player.sendMessage("해당 명령어는 땅 소유자만 사용가능합니다.".errorFormat())
                } else sender.sendMessage("콘솔에서는 사용할 수 없는 명령어입니다.".errorFormat())
                return true
            }
        }
        return false
    }
}