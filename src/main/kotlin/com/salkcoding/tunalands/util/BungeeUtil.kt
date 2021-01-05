package com.salkcoding.tunalands.util

import com.google.common.io.ByteStreams
import com.salkcoding.tunalands.channelName
import com.salkcoding.tunalands.tunaLands
import org.bukkit.entity.Player
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.IOException

fun Player.connectServer(serverName: String) {
    val out = ByteStreams.newDataOutput()
    out.writeUTF("Connect")
    out.writeUTF(serverName)

    this.sendPluginMessage(tunaLands, "BungeeCord", out.toByteArray())
}

fun Player.teleportToServerAsync(serverName: String, world: String, x: Double, y: Double, z: Double) {
    this.connectServer(serverName)

    val out = ByteStreams.newDataOutput()
    out.writeUTF("Forward") // So BungeeCord knows to forward it
    out.writeUTF(serverName)//Server name
    out.writeUTF("Teleport") // The channel name to check if this your data

    val messageBytes = ByteArrayOutputStream()
    val messageOut = DataOutputStream(messageBytes)
    try {
        messageOut.writeUTF(world)
        messageOut.writeDouble(x)
        messageOut.writeDouble(y)
        messageOut.writeDouble(z)
    } catch (exception: IOException) {
        exception.printStackTrace()
    }

    out.writeShort(messageBytes.toByteArray().size)
    out.write(messageBytes.toByteArray())

    println(messageBytes.toByteArray().size)

    this.sendPluginMessage(tunaLands, channelName, out.toByteArray())
}