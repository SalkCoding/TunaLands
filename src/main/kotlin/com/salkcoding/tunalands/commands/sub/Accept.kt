package com.salkcoding.tunalands.commands.sub

import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.Lands
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.util.errorFormat
import com.salkcoding.tunalands.util.infoFormat
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Accept : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        when {
            label == "accept" && args.isEmpty() -> {
                val player = sender as? Player
                if (player != null) {
                    work(player)
                } else sender.sendMessage("콘솔에서는 사용 불가능한 명령어입니다.".errorFormat())
                return true
            }
        }
        return false
    }

    companion object {
        fun work(player: Player) {
            val uuid = player.uniqueId
            if (inviteMap.containsKey(uuid)) {
                val data = inviteMap[uuid]!!
                val host = data.host
                val lands = landManager.getPlayerLands(host.uniqueId, Rank.OWNER, Rank.DELEGATOR)
                if (lands != null) {
                    player.sendMessage("초대를 수락했습니다.".infoFormat())
                    if (host.isOnline)
                        host.sendMessage("${player.name}이/가 초대에 수락했습니다.".infoFormat())

                    landManager.getPlayerLands(
                        player.uniqueId,
                        Rank.VISITOR
                    )?.memberMap?.remove(player.uniqueId)

                    val present = System.currentTimeMillis()
                    lands.memberMap[uuid] = Lands.MemberData(uuid, data.targetRank, present, present)
                    data.task.cancel()
                    inviteMap.remove(uuid)
                } else player.sendMessage("해당 땅이 더이상 존재하지 않습니다.".errorFormat())
            } else player.sendMessage("받은 초대가 없습니다.".errorFormat())
        }
    }
}