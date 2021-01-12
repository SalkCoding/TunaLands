package com.salkcoding.tunalands

import com.salkcoding.tunalands.bungee.CommandListener
import com.salkcoding.tunalands.bungee.PlayerListListener
import com.salkcoding.tunalands.bungee.ReloadListener
import com.salkcoding.tunalands.bungee.proxyPlayerSet
import com.salkcoding.tunalands.commands.LandCommandHandler
import com.salkcoding.tunalands.commands.debug.Debug
import com.salkcoding.tunalands.commands.sub.*
import com.salkcoding.tunalands.gui.GuiManager
import com.salkcoding.tunalands.lands.LandManager
import com.salkcoding.tunalands.listener.*
import com.salkcoding.tunalands.listener.region.*
import com.salkcoding.tunalands.util.consoleFormat
import io.github.leonardosnt.bungeechannelapi.BungeeChannelApi
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.plugin.java.JavaPlugin
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.IOException


const val chunkDebug = true
const val channelName = "BungeeCord"

lateinit var tunaLands: TunaLands
lateinit var configuration: Config

lateinit var guiManager: GuiManager
lateinit var landManager: LandManager

lateinit var bungeeApi: BungeeChannelApi

class TunaLands : JavaPlugin() {

    override fun onEnable() {
        tunaLands = this

        guiManager = GuiManager()
        landManager = LandManager()

        bungeeApi = BungeeChannelApi.of(this)
        val playerListListener = PlayerListListener()
        bungeeApi.registerForwardListener("tunalands-playerjoin", playerListListener)
        bungeeApi.registerForwardListener("tunalands-playerquit", playerListListener)
        bungeeApi.registerForwardListener("tunalands-reload", ReloadListener())
        //Global listener
        bungeeApi.registerForwardListener(CommandListener())

        //For reload
        server.onlinePlayers.forEach {
            proxyPlayerSet.add(it.uniqueId)
        }

        Bukkit.getScheduler().runTaskAsynchronously(this, Runnable {
            val messageBytes = ByteArrayOutputStream()
            val messageOut = DataOutputStream(messageBytes)
            try {
                messageOut.writeUTF(configuration.serverName)
            } catch (exception: IOException) {
                exception.printStackTrace()
            }

            bungeeApi.forward("ALL", "tunalands-reload", messageBytes.toByteArray())
        })


        val handler = LandCommandHandler()
        handler.register("accept", Accept())
        handler.register("alba", Alba())
        handler.register("ban", Ban())
        handler.register("banlist", BanList())
        handler.register("cancel", Cancel())
        handler.register("delete", Delete())
        handler.register("demote", Demote())
        handler.register("deny", Deny())
        handler.register("hego", Hego())
        handler.register("help", Help())
        handler.register("invite", Invite())
        handler.register("kick", Kick())
        handler.register("leave", Leave())
        handler.register("promote", Promote())
        handler.register("setleader", SetLeader())
        handler.register("setspawn", SetSpawn())
        handler.register("spawn", Spawn())
        handler.register("unban", Unban())
        handler.register("visit", Visit())

        handler.register("debug", Debug())

        getCommand("land")!!.setExecutor(handler)

        server.pluginManager.registerEvents(ArmorStandListener(), this)
        server.pluginManager.registerEvents(BlockBreakListener(), this)
        server.pluginManager.registerEvents(BlockPlaceListener(), this)
        server.pluginManager.registerEvents(BreedListener(), this)
        server.pluginManager.registerEvents(BucketListener(), this)
        server.pluginManager.registerEvents(ChestedHorseListener(), this)
        server.pluginManager.registerEvents(ClickedInteractListener(), this)
        server.pluginManager.registerEvents(ConsumeListener(), this)
        server.pluginManager.registerEvents(DropItemListener(), this)
        server.pluginManager.registerEvents(EntityMountListener(), this)
        server.pluginManager.registerEvents(FishingListener(), this)
        server.pluginManager.registerEvents(HurtListener(), this)
        server.pluginManager.registerEvents(LeashEntityListener(), this)
        server.pluginManager.registerEvents(PhysicalInteractListener(), this)
        server.pluginManager.registerEvents(PickupExpListener(), this)
        server.pluginManager.registerEvents(PickupItemListener(), this)
        server.pluginManager.registerEvents(PVPListener(), this)
        server.pluginManager.registerEvents(ShearListener(), this)
        server.pluginManager.registerEvents(ThrowListener(), this)

        server.pluginManager.registerEvents(ChatListener(), this)
        server.pluginManager.registerEvents(ChestGuiOpenListener(), this)
        server.pluginManager.registerEvents(CoreListener(), this)
        server.pluginManager.registerEvents(ExplodeListener(), this)
        server.pluginManager.registerEvents(FlagListener(), this)
        server.pluginManager.registerEvents(InventoryClickListener(), this)
        server.pluginManager.registerEvents(InventoryCloseListener(), this)
        server.pluginManager.registerEvents(InventoryDragListener(), this)
        server.pluginManager.registerEvents(PlayerConnectListener(), this)

        if (chunkDebug) {
            logger.warning("Chunk debug mode is enabled.".consoleFormat())
            server.scheduler.runTaskTimer(this, Runnable {
                landManager.debug()
            }, 20, 5)
        }

        configRead()

        logger.info("Plugin is now enabled".consoleFormat())
    }

    override fun onDisable() {
        landManager.close()
        guiManager.allClose()
        logger.warning("All guis are closed".consoleFormat())

        logger.warning("All of chunks are now unprotected".consoleFormat())
        logger.warning("Plugin is now disabled".consoleFormat())
    }

    private fun configRead() {
        saveDefaultConfig()

        //Protect
        val serverName = config.getString("serverName")!!
        logger.info("serverName: $serverName")
        val configProtect = config.getConfigurationSection("protect")!!
        val protect = Config.Protect(
            Material.valueOf(configProtect.getString("coreBlock")!!),
            configProtect.getInt("createPrice"),
            configProtect.getInt("baseMaxExtendCount"),
            configProtect.getInt("baseLimitExtendPrice")
        )
        logger.info("protect: ${protect.toString().consoleFormat()}}")
        //Flag
        val configFlag = config.getConfigurationSection("flag")!!
        val flag = Config.Flag(
            configFlag.getInt("takeFlagPrice"),
            configFlag.getInt("releaseFlagPrice")
        )
        logger.info("flag: ${flag.toString().consoleFormat()}")
        //Command
        val configCommand = config.getConfigurationSection("command")!!
        val cooldownSection = configCommand.getConfigurationSection("cooldown")!!
        val priceSection = configCommand.getConfigurationSection("price")!!
        val command = Config.Command(
            cooldownSection.getLong("rejoin"),
            cooldownSection.getLong("visit"),
            cooldownSection.getLong("spawn"),
            priceSection.getInt("setSpawnPrice")
        )
        logger.info("command: ${command.toString().consoleFormat()}")
        //Limit worlds
        val limitWorld = config.getStringList("limitWorld")
        logger.info("limitWorld: ${limitWorld.toString().consoleFormat()}")

        configuration = Config(serverName, protect, flag, command, limitWorld)
    }
}

data class Config(
    val serverName: String,
    val protect: Protect,
    val flag: Flag,
    val command: Command,
    val limitWorld: List<String>
) {

    data class Protect(
        val coreBlock: Material,
        val createPrice: Int,
        val baseMaxExtendCount: Int,
        val baseLimitExtendPrice: Int
    )

    data class Flag(
        val takeFlagPrice: Int,
        val releaseFlagPrice: Int
    )

    data class Command(
        val rejoinCooldown: Long,
        val visitCooldown: Long,
        val spawnCooldown: Long,
        val setSpawnPrice: Int
    )
}