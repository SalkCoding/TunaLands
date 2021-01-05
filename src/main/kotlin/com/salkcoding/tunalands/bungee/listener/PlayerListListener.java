package com.salkcoding.tunalands.bungee.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.salkcoding.tunalands.TunaLands;
import com.salkcoding.tunalands.TunaLandsKt;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class PlayerListListener implements PluginMessageListener {

    private HashMap<UUID, String[]> receivedList = null;

    public synchronized String[] getPlayerList(Player player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("PlayerList");
        out.writeUTF("ALL");

        TunaLands tunaLands = TunaLandsKt.tunaLands;
        player.sendPluginMessage(tunaLands, "BungeeCord", out.toByteArray());

        try {
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return receivedList.get(player.getUniqueId());
    }

    @Override
    public synchronized void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {
        if (!channel.equals(TunaLandsKt.channelName)) return;

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subChannel = in.readUTF();
        if (!subChannel.equals("PlayerList")) return;

        String server = in.readUTF();
        if (!server.equals("ALL")) return;

        receivedList.put(player.getUniqueId(), in.readUTF().split(", "));

        notifyAll();
    }
}
