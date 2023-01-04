package com.salkcoding.tunalands.listener

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.wrappers.BlockPosition
import com.comphenix.protocol.wrappers.WrappedBlockData
import com.salkcoding.tunalands.gui.render.settinggui.loreChatMap
import com.salkcoding.tunalands.gui.render.settinggui.welcomeMessageChatMap
import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.protocolManager
import com.salkcoding.tunalands.tunaLands
import org.bukkit.ChatColor
import org.bukkit.Material
import java.lang.reflect.InvocationTargetException
import com.salkcoding.tunalands.util.infoFormat

class LoreSignUpdatePacketListener {

    fun registerListener() {
        protocolManager.addPacketListener(object : PacketAdapter(tunaLands, PacketType.Play.Client.UPDATE_SIGN) {
            override fun onPacketReceiving(event: PacketEvent) {
                val player = event.player
                val uuid = player.uniqueId
                val packet = event.packet

                if (loreChatMap.contains(uuid)) {
                    event.isCancelled = true

                    val pos = loreChatMap.remove(player.uniqueId)!!
                    val receivedPos = packet.blockPositionModifier.read(0)

                    if (receivedPos.x != pos.first || receivedPos.y != pos.second || receivedPos.z != pos.third) return

                    val newLore = packet.stringArrays.read(0).map { line ->
                        ChatColor.translateAlternateColorCodes('&', line)
                    }.toMutableList()

                    val lands = landManager.getPlayerLands(uuid, Rank.OWNER, Rank.DELEGATOR) ?: return

                    lands.lore = newLore

                    player.sendMessage("설명 설정이 아래와 같이 변경되었습니다.".infoFormat())

                    lands.lore.forEach { line ->
                        player.sendMessage(line)
                    }

                    // Set block change to original bedrock
                    val blockSignPacket = PacketContainer(PacketType.Play.Server.BLOCK_CHANGE)
                    blockSignPacket.blockPositionModifier.write(0, BlockPosition(pos.first, pos.second, pos.third))
                    blockSignPacket.blockData.write(0, WrappedBlockData.createData(Material.BEDROCK))

                    try {
                        protocolManager.sendServerPacket(player, blockSignPacket)
                    } catch (e: InvocationTargetException) {
                        throw RuntimeException("Cannot send packet $blockSignPacket", e)
                    }

                } else if (welcomeMessageChatMap.contains(uuid)) {
                    event.isCancelled = true
                    val pos = welcomeMessageChatMap.remove(player.uniqueId)!!
                    val receivedPos = packet.blockPositionModifier.read(0)

                    if (receivedPos.x != pos.first || receivedPos.y != pos.second || receivedPos.z != pos.third) return

                    val newLore = packet.stringArrays.read(0).map { line ->
                        ChatColor.translateAlternateColorCodes('&', line)
                    }.toMutableList()

                    val lands = landManager.getPlayerLands(uuid, Rank.OWNER, Rank.DELEGATOR) ?: return

                    lands.welcomeMessage = newLore

                    player.sendMessage("환영 메시지 설정이 아래와 같이 변경되었습니다.".infoFormat())

                    lands.welcomeMessage.forEach { line ->
                        player.sendMessage(line)
                    }

                    // Set block change to original bedrock
                    val blockSignPacket = PacketContainer(PacketType.Play.Server.BLOCK_CHANGE)
                    blockSignPacket.blockPositionModifier.write(0, BlockPosition(pos.first, pos.second, pos.third))
                    blockSignPacket.blockData.write(0, WrappedBlockData.createData(Material.BEDROCK))

                    try {
                        protocolManager.sendServerPacket(player, blockSignPacket)
                    } catch (e: InvocationTargetException) {
                        throw RuntimeException("Cannot send packet $blockSignPacket", e)
                    }

                }
            }

        })
    }
}
