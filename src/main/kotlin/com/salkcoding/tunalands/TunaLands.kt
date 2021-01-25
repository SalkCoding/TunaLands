package com.salkcoding.tunalands

import com.salkcoding.tunalands.bungee.CommandListener
import com.salkcoding.tunalands.commands.LandCommandHandler
import com.salkcoding.tunalands.commands.debug.Debug
import com.salkcoding.tunalands.commands.sub.*
import com.salkcoding.tunalands.database.Database
import com.salkcoding.tunalands.display.DisplayChunkListener
import com.salkcoding.tunalands.display.DisplayManager
import com.salkcoding.tunalands.gui.GuiManager
import com.salkcoding.tunalands.lands.LandManager
import com.salkcoding.tunalands.listener.*
import com.salkcoding.tunalands.listener.region.*
import com.salkcoding.tunalands.bungee.channelapi.BungeeChannelApi
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.plugin.java.JavaPlugin
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.IOException
import com.salkcoding.tunalands.vault.economy.Economy
import me.baiks.bukkitlinked.BukkitLinked
import me.baiks.bukkitlinked.api.BukkitLinkedAPI


const val chunkDebug = true

lateinit var tunaLands: TunaLands
lateinit var configuration: Config
lateinit var guiManager: GuiManager
lateinit var landManager: LandManager
lateinit var displayManager: DisplayManager
lateinit var bukkitLinkedAPI: BukkitLinkedAPI
lateinit var bungeeApi: BungeeChannelApi
lateinit var economy: Economy
lateinit var database: Database

class TunaLands : JavaPlugin() {

    override fun onEnable() {
        tunaLands = this

        guiManager = GuiManager()
        landManager = LandManager()
        displayManager = DisplayManager()

        val bukkitLinked = server.pluginManager.getPlugin("BukkitLinked") as? BukkitLinked
        if(bukkitLinked == null){
            server.pluginManager.disablePlugin(this)
            return
        }
        bukkitLinkedAPI = bukkitLinked.api

        bungeeApi = BungeeChannelApi.of(this)
        //Global listener
        bungeeApi.registerForwardListener(CommandListener())

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
        server.pluginManager.registerEvents(DisplayChunkListener(), this)
        server.pluginManager.registerEvents(CoreListener(), this)
        server.pluginManager.registerEvents(ExplodeListener(), this)
        server.pluginManager.registerEvents(FlagListener(), this)
        server.pluginManager.registerEvents(InventoryClickListener(), this)
        server.pluginManager.registerEvents(InventoryCloseListener(), this)
        server.pluginManager.registerEvents(InventoryDragListener(), this)

        if (chunkDebug) {
            logger.warning("Chunk debug mode is enabled.")
            server.scheduler.runTaskTimer(this, Runnable {
                landManager.debug()
            }, 20, 5)
        }

        configRead()

        database = Database()

        logger.info("Plugin is now enabled")
    }

    override fun onDisable() {
        displayManager.deleteAll()
        landManager.close()
        database.close()
        guiManager.allClose()
        logger.warning("All guis are closed")

        logger.warning("All of chunks are now unprotected")
        logger.warning("Plugin is now disabled")
    }

    private fun configRead() {
        saveDefaultConfig()
        //DataBase
        val databaseConfig = config.getConfigurationSection("database")!!
        val database = Config.Database(
            databaseConfig.getString("name")!!,
            databaseConfig.getString("ip")!!,
            databaseConfig.getInt("port"),
            databaseConfig.getString("username")!!,
            databaseConfig.getString("password")!!,
            databaseConfig.getString("encoding")!!
        )
        logger.info("database: $database")
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
        logger.info("protect: $protect")
        //Flag
        val configFlag = config.getConfigurationSection("flag")!!
        val flag = Config.Flag(
            configFlag.getInt("takeFlagPrice"),
            configFlag.getInt("releaseFlagPrice")
        )
        logger.info("flag: $flag")
        //Fuel
        val configFuel = config.getConfigurationSection("fuel")!!
        val fuel = Config.Fuel(
            configFuel.getInt("m30"),
            configFuel.getInt("h1"),
            configFuel.getInt("h6"),
            configFuel.getInt("h12"),
            configFuel.getInt("h24"),
        )
        logger.info("fuel: $fuel")
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
        logger.info("command: $command")
        //Limit worlds
        val limitWorld = config.getStringList("limitWorld")
        logger.info("limitWorld: $limitWorld")

        configuration = Config(database, serverName, protect, flag, fuel, command, limitWorld)

        if (!setupEconomy()) {
            logger.warning("[${description.name}] - Disabled due to no Vault dependency found!")
            server.pluginManager.disablePlugin(this)
            return
        }
    }

    private fun setupEconomy(): Boolean {
        if (server.pluginManager.getPlugin("Vault") == null) return false
        val rsp = server.servicesManager.getRegistration(Economy::class.java) ?: return false
        economy = rsp.provider
        return true
    }
}

data class Config(
    val dataBase: Database,
    val serverName: String,
    val protect: Protect,
    val flag: Flag,
    val fuel: Fuel,
    val command: Command,
    val limitWorld: List<String>
) {

    data class Database constructor(
        val name: String,
        val ip: String,
        val port: Int,
        val username: String,
        val password: String,
        val encoding: String
    )

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

    data class Fuel(
        val m30: Int,
        val h1: Int,
        val h6: Int,
        val h12: Int,
        val h24: Int
    )

    data class Command(
        val rejoinCooldown: Long,
        val visitCooldown: Long,
        val spawnCooldown: Long,
        val setSpawnPrice: Int
    )
}