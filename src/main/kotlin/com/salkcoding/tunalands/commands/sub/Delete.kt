package com.salkcoding.tunalands.commands.sub

import com.google.gson.JsonObject
import com.salkcoding.tunalands.bukkitLinkedAPI
import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.metamorphosis
import com.salkcoding.tunalands.util.errorFormat
import com.salkcoding.tunalands.util.infoFormat
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

class Delete : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (label == "delete" && args.isEmpty()) {
            val player = sender as? Player
            if (player != null) {
                work(player)
            } else sender.sendMessage("콘솔에서는 사용할 수 없는 명령어입니다.".errorFormat())
            return true
        }
        return false
    }

    companion object {
        fun work(uuid: UUID) {
            val offlinePlayer = Bukkit.getOfflinePlayer(uuid)
            work(offlinePlayer)
        }

        private fun work(offlinePlayer: OfflinePlayer) {
            if (offlinePlayer.isOnline) {
                val player = offlinePlayer.player!!
                val lands = landManager.getPlayerLands(player.uniqueId, Rank.OWNER)
                if (lands != null) {
                    if (lands.memberMap.size == 1) {
                        landManager.deleteLands(lands)
                        player.sendMessage("땅을 삭제했습니다.".infoFormat())
                        player.playSound(player.location, Sound.ENTITY_GENERIC_EXPLODE, 0.5f, 1f)
                    } else player.sendMessage("모든 멤버가 나가기전까지는 땅을 삭제할 수 없습니다.".errorFormat())
                } else player.sendMessage("해당 명령어는 땅 소유자만 사용가능합니다.".errorFormat())
            } else {
                val hostName = offlinePlayer.name
                val lands = landManager.getPlayerLands(offlinePlayer.uniqueId, Rank.OWNER)
                if (lands != null) {
                    if (lands.memberMap.size == 1) {
                        landManager.deleteLands(lands)
                        bukkitLinkedAPI.sendMessageAcrossServer(hostName, "땅을 삭제했습니다.".infoFormat())
                    } else bukkitLinkedAPI.sendMessageAcrossServer(
                        hostName,
                        "모든 멤버가 나가기전까지는 땅을 삭제할 수 없습니다.".errorFormat()
                    )
                } else bukkitLinkedAPI.sendMessageAcrossServer(hostName, "해당 명령어는 땅 소유자만 사용가능합니다.".errorFormat())
            }
            val json = JsonObject()
            json.addProperty("uuid", offlinePlayer.uniqueId.toString())
            metamorphosis.send("com.salkcoding.tunalands.sync_delete", json.toString())
        }
    }
}
