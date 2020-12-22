package com.salkcoding.tunalands

import com.salkcoding.tunalands.commands.LandCommandHandler
import com.salkcoding.tunalands.commands.debug.Debug
import com.salkcoding.tunalands.commands.sub.*
import com.salkcoding.tunalands.listener.*
import com.salkcoding.tunalands.gui.GuiManager
import com.salkcoding.tunalands.lands.LandManager
import com.salkcoding.tunalands.listener.region.*
import com.salkcoding.tunalands.util.consoleFormat
import org.bukkit.Material
import org.bukkit.plugin.java.JavaPlugin

const val chunkDebug = true

lateinit var tunaLands: TunaLands
lateinit var configuration: Config

val guiManager = GuiManager()
val landManager = LandManager()

class TunaLands : JavaPlugin() {

    override fun onEnable() {
        tunaLands = this

        val handler = LandCommandHandler()
        handler.register("accept", Accept())
        handler.register("ban", Ban())
        handler.register("banlist", BanList())
        handler.register("cancel", Cancel())
        handler.register("coop", Coop())
        handler.register("delete", Delete())
        handler.register("demote", Demote())
        handler.register("help", Help())
        handler.register("invite", Invite())
        handler.register("kick", Kick())
        handler.register("leave", Leave())
        handler.register("promote", Promote())
        handler.register("reject", Reject())
        handler.register("setleader", SetLeader())
        handler.register("setspawn", SetSpawn())
        handler.register("spawn", Spawn())
        handler.register("unban", Unban())
        handler.register("visit", Visit())
        handler.register("vote", Vote())

        handler.register("debug", Debug())

        getCommand("land")!!.setExecutor(handler)

        server.pluginManager.registerEvents(BlockBreakListener(), this)
        server.pluginManager.registerEvents(BlockPlaceListener(), this)
        server.pluginManager.registerEvents(CakeListener(), this)
        server.pluginManager.registerEvents(ChestOpenListener(), this)
        server.pluginManager.registerEvents(DropItemListener(), this)
        server.pluginManager.registerEvents(HurtListener(), this)
        server.pluginManager.registerEvents(PickupExpListener(), this)
        server.pluginManager.registerEvents(PickupItemListener(), this)
        server.pluginManager.registerEvents(PVPListener(), this)
        //TODO from second row

        server.pluginManager.registerEvents(CoreListener(), this)
        server.pluginManager.registerEvents(InventoryClickListener(), this)
        server.pluginManager.registerEvents(InventoryCloseListener(), this)
        server.pluginManager.registerEvents(InventoryDragListener(), this)
        server.pluginManager.registerEvents(ChestGuiOpenListener(), this)

        if (chunkDebug) {
            logger.warning("Chunk debug mode is enabled".consoleFormat())
            server.scheduler.runTaskTimer(this, Runnable {
                landManager.debug()
            }, 20, 20)
        }

        configRead()

        logger.info("Plugin is now enabled".consoleFormat())
    }

    override fun onDisable() {
        guiManager.allClose()
        logger.warning("All guis are closed".consoleFormat())

        logger.warning("All of chunks are now unprotected".consoleFormat())
        logger.warning("Plugin is now disabled".consoleFormat())
    }

    private fun configRead() {
        saveDefaultConfig()

        //Protect
        val configProtect = config.getConfigurationSection("protect")!!
        val protect = Config.Protect(
            Material.valueOf(configProtect.getString("coreBlock")!!),
            configProtect.getInt("createPrice"),
            configProtect.getInt("baseMaxExtendCount"),
            configProtect.getInt("baseLimitExtendPrice")
        )
        logger.info(protect.toString())
        //Flag
        val configFlag = config.getConfigurationSection("flag")!!
        val flag = Config.Flag(
            configFlag.getInt("takeFlagPrice"),
            configFlag.getInt("releaseFlagPrice")
        )
        logger.info(flag.toString())
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
        logger.info(command.toString())
        //Limit worlds
        val limitWorld = config.getStringList("limitWorld")
        logger.info(limitWorld.toString())

        configuration = Config(protect, flag, limitWorld)
    }

}

data class Config(
    val protect: Protect,
    val flag: Flag,
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