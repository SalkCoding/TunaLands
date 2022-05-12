package com.salkcoding.tunalands.commands.sub

import com.salkcoding.tunalands.configuration
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.economy
import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.util.errorFormat
import com.salkcoding.tunalands.util.hasNotEnoughMoney
import com.salkcoding.tunalands.util.infoFormat
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Rename : CommandExecutor {

    private val namingRegex = Regex("[\\w\\dㄱ-힣]{3,8}")

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (label == "rename" && args.isNotEmpty()) {
            val player = sender as? Player
            if (player != null) {
                val price = configuration.command.renamePrice.toDouble()
                if (player.hasNotEnoughMoney(price)) {
                    val delta = price - economy.getBalance(player)
                    player.sendMessage("${"%.2f".format(delta)}캔이 부족합니다.".errorFormat())
                    return true
                }

                val lands = landManager.getPlayerLands(player.uniqueId, Rank.OWNER)
                if (lands != null) {
                    val newLandsName = args.joinToString(" ")
                    if (!newLandsName.matches(namingRegex)) {
                        player.sendMessage("땅 이름은 특수문자를 포함할 수 없으면 3~8자로만 지정할 수 있습니다.".errorFormat())
                        return true
                    }

                    if(landManager.isSameLandsNameExist(newLandsName)){
                        player.sendMessage("같은 이름의 땅이 이미 존재합니다.".errorFormat())
                        return true
                    }

                    val res = economy.withdrawPlayer(player, price)
                    if (res.type == EconomyResponse.ResponseType.SUCCESS) {
                        lands.landsName = "${ChatColor.WHITE}$newLandsName"
                        player.sendMessage("땅 이름을 ${ChatColor.GRAY}${lands.landsName}${ChatColor.RESET}으로 변경하였습니다.".infoFormat())
                    } else {
                        player.sendMessage("캔이 부족합니다.".errorFormat())
                    }
                } else player.sendMessage("땅의 소유자만 사용 가능한 명령어입니다.".errorFormat())
            } else sender.sendMessage("콘솔에서는 사용할 수 없는 명령어입니다.".errorFormat())
            return true
        }
        return false
    }
}