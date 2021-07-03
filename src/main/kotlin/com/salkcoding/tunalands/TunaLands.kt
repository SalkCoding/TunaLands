package com.salkcoding.tunalands

import com.salkcoding.tunalands.alarm.AlarmManager
import com.salkcoding.tunalands.listener.JoinListener
import com.salkcoding.tunalands.border.BorderManager
import com.salkcoding.tunalands.bungee.CommandListener
import com.salkcoding.tunalands.bungee.channelapi.BungeeChannelApi
import com.salkcoding.tunalands.commands.LandCommandHandler
import com.salkcoding.tunalands.commands.debug.Debug
import com.salkcoding.tunalands.commands.sub.*
import com.salkcoding.tunalands.lands.LandManager
import com.salkcoding.tunalands.database.Database
import com.salkcoding.tunalands.display.DisplayChunkListener
import com.salkcoding.tunalands.display.DisplayManager
import com.salkcoding.tunalands.gui.GuiManager
import com.salkcoding.tunalands.io.AutoSaver
import com.salkcoding.tunalands.lands.LeftManager
import com.salkcoding.tunalands.listener.*
import com.salkcoding.tunalands.listener.region.*
import com.salkcoding.tunalands.recipe.ReleaseFlagRecipe
import com.salkcoding.tunalands.recipe.TakeFlagRecipe
import com.salkcoding.tunalands.vote.RecommendManager
import fish.evatuna.metamorphosis.Metamorphosis
import me.baiks.bukkitlinked.BukkitLinked
import me.baiks.bukkitlinked.api.BukkitLinkedAPI
import net.milkbowl.vault.economy.Economy
import org.bukkit.Material
import org.bukkit.plugin.java.JavaPlugin

lateinit var tunaLands: TunaLands

lateinit var landManager: LandManager
lateinit var borderManager: BorderManager
lateinit var guiManager: GuiManager
lateinit var displayManager: DisplayManager
lateinit var alarmManager: AlarmManager
lateinit var recommendManager: RecommendManager
lateinit var leftManager: LeftManager

lateinit var metamorphosis: Metamorphosis
lateinit var bukkitLinkedAPI: BukkitLinkedAPI
lateinit var bungeeApi: BungeeChannelApi
lateinit var economy: Economy
lateinit var database: Database
lateinit var configuration: Config

lateinit var currentServerName: String

class TunaLands : JavaPlugin() {

    override fun onEnable() {
        tunaLands = this

        configRead()

        landManager = LandManager()
        borderManager = BorderManager()
        guiManager = GuiManager()
        displayManager = DisplayManager()
        alarmManager = AlarmManager()
        recommendManager = RecommendManager(configuration.recommend.reset * 50, configuration.recommend.cooldown * 50)
        leftManager = LeftManager(configuration.command.rejoinCooldown * 50)

        val metamorphosis = server.pluginManager.getPlugin("Metamorphosis") as? Metamorphosis
        if (metamorphosis == null) {
            server.pluginManager.disablePlugin(this)
            logger.warning("Metamorphosis is not running on this server!")
            return
        }

        val bukkitLinked = server.pluginManager.getPlugin("BukkitLinked") as? BukkitLinked
        if (bukkitLinked == null) {
            server.pluginManager.disablePlugin(this)
            logger.warning("BukkitLinked is not running on this server!")
            return
        }
        bukkitLinkedAPI = bukkitLinked.api

        //For sending message
        bungeeApi = BungeeChannelApi.of(this)

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
        handler.register("recommend", Recommend())
        handler.register("rename", Rename())
        handler.register("setleader", SetLeader())
        handler.register("setspawn", SetSpawn())
        handler.register("spawn", Spawn())
        handler.register("unban", Unban())
        handler.register("visit", Visit())

        handler.register("debug", Debug())

        getCommand("tunaland")!!.setExecutor(handler)
        //For bungee command support
        server.pluginManager.registerEvents(CommandListener(), this)

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

        server.pluginManager.registerEvents(ChestGuiOpenListener(), this)
        server.pluginManager.registerEvents(ChunkEffectListener(), this)
        server.pluginManager.registerEvents(DisplayChunkListener(), this)
        server.pluginManager.registerEvents(CoreListener(), this)
        server.pluginManager.registerEvents(ExplodeListener(), this)
        server.pluginManager.registerEvents(FlagListener(), this)
        server.pluginManager.registerEvents(InOutListener(), this)
        server.pluginManager.registerEvents(InventoryClickListener(), this)
        server.pluginManager.registerEvents(InventoryCloseListener(), this)
        server.pluginManager.registerEvents(InventoryDragListener(), this)
        server.pluginManager.registerEvents(JoinListener(), this)
        server.pluginManager.registerEvents(LoreChatListener(), this)

        server.pluginManager.registerEvents(JoinListener(), this)

        TakeFlagRecipe.registerRecipe()
        ReleaseFlagRecipe.registerRecipe()

        database = Database()

        server.scheduler.runTaskTimerAsynchronously(this, AutoSaver(), 18000, 18000)

        logger.info("Plugin is now enabled")
    }

    override fun onDisable() {
        database.dispose()

        recommendManager.dispose()
        alarmManager.dispose()
        displayManager.dispose()
        guiManager.dispose()
        borderManager.dispose()
        landManager.dispose()

        TakeFlagRecipe.unregisterRecipe()
        ReleaseFlagRecipe.unregisterRecipe()

        logger.warning("All guis are closed")

        logger.warning("All of chunks are now unprotected")
        logger.warning("Plugin is now disabled")
    }

    private fun configRead() {
        saveDefaultConfig()
        currentServerName = config.getString("serverName")!!
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
        val configProtect = config.getConfigurationSection("protect")!!
        val protect = Config.Protect(
            Material.valueOf(configProtect.getString("coreBlock")!!),
            configProtect.getInt("createPrice"),
        )
        logger.info("protect: $protect")
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
        //Recommend
        val configRecommend = config.getConfigurationSection("recommend")!!
        val recommend = Config.Recommend(
            configRecommend.getLong("reset"),
            configRecommend.getLong("cooldown")
        )
        logger.info("recommend: $recommend")
        //Command
        val configCommand = config.getConfigurationSection("command")!!
        val cooldownSection = configCommand.getConfigurationSection("cooldown")!!
        val priceSection = configCommand.getConfigurationSection("price")!!
        val command = Config.Command(
            cooldownSection.getLong("rejoin"),
            cooldownSection.getLong("visit"),
            cooldownSection.getLong("spawn"),
            priceSection.getInt("setSpawnPrice"),
            priceSection.getInt("renamePrice")
        )
        logger.info("command: $command")
        //Limit worlds
        val limitWorld = config.getStringList("limitWorld")
        logger.info("limitWorld: $limitWorld")

        configuration = Config(database, protect, fuel, recommend, command, limitWorld)

        if (!setupEconomy()) {
            logger.warning("Disabled due to no Vault dependency found!")
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
    val protect: Protect,
    val fuel: Fuel,
    val recommend: Recommend,
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
        val createPrice: Int
    )

    data class Fuel(
        val m30: Int,
        val h1: Int,
        val h6: Int,
        val h12: Int,
        val h24: Int
    )

    data class Recommend(
        val reset: Long,
        val cooldown: Long
    )

    data class Command(
        val rejoinCooldown: Long,
        val visitCooldown: Long,
        val spawnCooldown: Long,
        val setSpawnPrice: Int,
        val renamePrice: Int
    )
}