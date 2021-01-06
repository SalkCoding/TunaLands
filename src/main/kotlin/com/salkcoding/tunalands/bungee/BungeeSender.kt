package com.salkcoding.tunalands.bungee

import br.com.devsrsouza.kotlinbukkitapi.extensions.bukkit.server
import com.google.common.io.ByteStreams
import com.salkcoding.tunalands.channelName
import com.salkcoding.tunalands.commands.sub.InviteData
import com.salkcoding.tunalands.lands.Lands
import com.salkcoding.tunalands.tunaLands
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.IOException
import java.util.*

object BungeeSender {

    //Ban, BanList, Delete, Demote, Hego, Help, Kick, Leave, Promote, SetLeader, Unban
    fun sendMessage(targetPlayer: String, string: String) {
        val out = ByteStreams.newDataOutput()
        out.writeUTF("Message")
        out.writeUTF(targetPlayer)
        out.writeUTF(string)

        server.sendPluginMessage(tunaLands, channelName, out.toByteArray())
    }

    fun sendVisitList(targetPlayer: String, playerLandMap: Map<UUID, Lands>) {
        /*
        * ForwardToPlayer
        * targetPlayer: String
        * Channel: TunaLands, Sub channel: Visit
        * Arguments
        * playerLandMap -> (uuid, enable, open, memberSize, visitorCount, createdMillisecond)
        */
        val out = ByteStreams.newDataOutput()
        out.writeUTF("ForwardToPlayer")
        out.writeUTF(targetPlayer)
        out.writeUTF("Visit")

        val messageBytes = ByteArrayOutputStream()
        val messageOut = DataOutputStream(messageBytes)
        try {
            playerLandMap.forEach { (_, data) ->
                messageOut.writeUTF(data.ownerUUID.toString())
                messageOut.writeBoolean(data.enable)
                messageOut.writeBoolean(data.open)
                messageOut.writeInt(data.memberMap.size)
                messageOut.writeLong(data.landHistory.visitorCount)
                messageOut.writeLong(data.landHistory.createdMillisecond)
                data.lore.forEach {
                    messageOut.writeUTF(it)
                }
                data.welcomeMessage.forEach {
                    messageOut.writeUTF(it)
                }
            }
        } catch (exception: IOException) {
            exception.printStackTrace()
        }

        out.writeShort(messageBytes.toByteArray().size)
        out.write(messageBytes.toByteArray())

        server.sendPluginMessage(tunaLands, channelName, out.toByteArray())
    }

    fun sendBanList(targetPlayer: String, banMap: Map<UUID, Lands.BanData>) {
        /*
        * ForwardToPlayer
        * targetPlayer: String
        * Channel: TunaLands, Sub channel: BanList
        * Arguments
        * banMap -> (uuid, banned)
        */
        val out = ByteStreams.newDataOutput()
        out.writeUTF("ForwardToPlayer")
        out.writeUTF(targetPlayer)
        out.writeUTF("BanList")

        val messageBytes = ByteArrayOutputStream()
        val messageOut = DataOutputStream(messageBytes)
        try {
            banMap.forEach { (_, data) ->
                messageOut.writeUTF(data.uuid.toString())
                messageOut.writeLong(data.banned)
            }
        } catch (exception: IOException) {
            exception.printStackTrace()
        }

        out.writeShort(messageBytes.toByteArray().size)
        out.write(messageBytes.toByteArray())

        server.sendPluginMessage(tunaLands, channelName, out.toByteArray())
    }

    fun sendCancel(targetPlayer: String, targetUUID: UUID) {
        /*
        * ForwardToPlayer
        * targetPlayer: String
        * Channel: TunaLands, Sub channel: Cancel
        * Arguments
        * host: Player, target: Player, targetRank: Rank
        */
        val out = ByteStreams.newDataOutput()
        out.writeUTF("ForwardToPlayer")
        out.writeUTF(targetPlayer)
        out.writeUTF("Cancel")

        val messageBytes = ByteArrayOutputStream()
        val messageOut = DataOutputStream(messageBytes)
        try {
            messageOut.writeUTF(targetUUID.toString())
        } catch (exception: IOException) {
            exception.printStackTrace()
        }

        out.writeShort(messageBytes.toByteArray().size)
        out.write(messageBytes.toByteArray())

        server.sendPluginMessage(tunaLands, channelName, out.toByteArray())
    }

    fun sendAlba(targetPlayer: String, data: InviteData) {
        /*
        * ForwardToPlayer
        * targetPlayer: String
        * Channel: TunaLands, Sub channel: Alba
        * Arguments
        * host: Player, target: Player, targetRank: Rank
        */
        val out = ByteStreams.newDataOutput()
        out.writeUTF("ForwardToPlayer")
        out.writeUTF(targetPlayer)
        out.writeUTF("Alba")

        val messageBytes = ByteArrayOutputStream()
        val messageOut = DataOutputStream(messageBytes)
        try {
            messageOut.writeUTF(data.host.uniqueId.toString())
            messageOut.writeUTF(data.target.uniqueId.toString())
            messageOut.writeUTF(data.targetRank.toString())
        } catch (exception: IOException) {
            exception.printStackTrace()
        }

        out.writeShort(messageBytes.toByteArray().size)
        out.write(messageBytes.toByteArray())

        server.sendPluginMessage(tunaLands, channelName, out.toByteArray())
    }

    fun sendInvite(targetPlayer: String, data: InviteData) {
        /*
        * ForwardToPlayer
        * targetPlayer: String
        * Channel: TunaLands, Sub channel: Invite
        * Arguments
        * host: Player, target: Player, targetRank: Rank
        */
        val out = ByteStreams.newDataOutput()
        out.writeUTF("ForwardToPlayer")
        out.writeUTF(targetPlayer)
        out.writeUTF("Invite")

        val messageBytes = ByteArrayOutputStream()
        val messageOut = DataOutputStream(messageBytes)
        try {
            messageOut.writeUTF(data.host.uniqueId.toString())
            messageOut.writeUTF(data.target.uniqueId.toString())
            messageOut.writeUTF(data.targetRank.toString())
        } catch (exception: IOException) {
            exception.printStackTrace()
        }

        out.writeShort(messageBytes.toByteArray().size)
        out.write(messageBytes.toByteArray())

        server.sendPluginMessage(tunaLands, channelName, out.toByteArray())
    }
}