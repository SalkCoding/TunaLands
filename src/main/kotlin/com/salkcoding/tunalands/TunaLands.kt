package com.salkcoding.tunalands

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.salkcoding.tunalands.border.BorderManager
import com.salkcoding.tunalands.bungee.BroadcastLandMembersRunnable
import com.salkcoding.tunalands.bungee.CommandListener
import com.salkcoding.tunalands.commands.LandCommandHandler
import com.salkcoding.tunalands.commands.debug.Debug
import com.salkcoding.tunalands.commands.sub.*
import com.salkcoding.tunalands.commands.sub.Map
import com.salkcoding.tunalands.config.Config
import com.salkcoding.tunalands.display.DisplayChunkListener
import com.salkcoding.tunalands.display.DisplayManager
import com.salkcoding.tunalands.gui.GuiManager
import com.salkcoding.tunalands.file.PlayerLandMapAutoSaver
import com.salkcoding.tunalands.lands.LandManager
import com.salkcoding.tunalands.lands.LeftManager
import com.salkcoding.tunalands.listener.*
import com.salkcoding.tunalands.listener.land.protect.*
import com.salkcoding.tunalands.recommend.RecommendManager
import fish.evatuna.metamorphosis.Metamorphosis
import me.baiks.bukkitlinked.BukkitLinked
import me.baiks.bukkitlinked.api.BukkitLinkedAPI
import net.milkbowl.vault.economy.Economy
import org.bukkit.plugin.java.JavaPlugin
import java.util.concurrent.LinkedBlockingQueue

lateinit var tunaLands: TunaLands

lateinit var landManager: LandManager
lateinit var borderManager: BorderManager
lateinit var guiManager: GuiManager
lateinit var displayManager: DisplayManager
lateinit var recommendManager: RecommendManager
lateinit var leftManager: LeftManager

lateinit var metamorphosis: Metamorphosis
lateinit var bukkitLinkedAPI: BukkitLinkedAPI
lateinit var economy: Economy
lateinit var configuration: Config

lateinit var currentServerName: String

lateinit var protocolManager: ProtocolManager

class TunaLands : JavaPlugin() {

    val broadcastLandMembersRunnable: BroadcastLandMembersRunnable = BroadcastLandMembersRunnable(LinkedBlockingQueue())

    override fun onEnable() {
        val tempMetamorphosis = server.pluginManager.getPlugin("Metamorphosis") as? Metamorphosis
        if (tempMetamorphosis == null) {
            server.pluginManager.disablePlugin(this)
            logger.warning("Metamorphosis is not running on this server!")
            return
        }
        metamorphosis = tempMetamorphosis

        val tempBukkitLinked = server.pluginManager.getPlugin("BukkitLinked") as? BukkitLinked
        if (tempBukkitLinked == null) {
            server.pluginManager.disablePlugin(this)
            logger.warning("BukkitLinked is not running on this server!")
            return
        }
        bukkitLinkedAPI = tempBukkitLinked.api


        protocolManager = ProtocolLibrary.getProtocolManager()

        if (!setupEconomy()) {
            logger.warning("Disabled due to no Vault dependency found!")
            server.pluginManager.disablePlugin(this)
            return
        }

        tunaLands = this

        configuration = Config()

        //Independent manager
        guiManager = GuiManager()
        borderManager = BorderManager()

        displayManager = DisplayManager()

        //Depend on displayManager
        landManager = LandManager()

        recommendManager = RecommendManager(configuration.recommend.reset * 50, configuration.recommend.cooldown * 50)
        leftManager = LeftManager(configuration.commandCooldown.rejoinCooldown * 50)

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

        handler.register("map", Map())

        handler.register("debug", Debug())

        getCommand("tunaland")!!.setExecutor(handler)

        listOf(
            //For bungee command support
            CommandListener(),
            //Protect listener
            ArmorStandListener(),
            BlockBreakListener(),
            BlockDropItemListener(),
            BlockPlaceListener(),
            BreedListener(),
            BucketListener(),
            ChestedHorseListener(),
            ClickedInteractListener(),
            ConsumeListener(),
            DropItemListener(),
            EntityMountListener(),
            FishingListener(),
            HurtListener(),
            LeashEntityListener(),
            PhysicalInteractListener(),
            PickupExpListener(),
            PickupItemListener(),
            PVPListener(),
            ShearListener(),
            ThrowListener(),
            //General listener
            ChestGuiOpenListener(),
            ChunkEffectListener(),
            DisplayChunkListener(),
            CoreListener(),
            ExplodeListener(),
            FlagListener(),
            InOutListener(),
            InventoryClickListener(),
            InventoryCloseListener(),
            InventoryDragListener(),
            JoinListener(),
        ).forEach { listener ->
            server.pluginManager.registerEvents(listener, this)
        }

        LoreSignUpdatePacketListener().registerListener()

        server.scheduler.runTaskTimerAsynchronously(this, PlayerLandMapAutoSaver(), 18000, 18000)
        server.scheduler.runTaskTimerAsynchronously(this, broadcastLandMembersRunnable, 100, 100)

        logger.info("Plugin is now enabled")
    }

    override fun onDisable() {
        //Independent manager
        recommendManager.dispose()
        leftManager.dispose()

        borderManager.dispose()
        guiManager.dispose()

        //Depend on displayManager
        landManager.dispose()
        displayManager.dispose()

        logger.warning("All guis are closed")

        logger.warning("All of chunks are now unprotected")
        logger.warning("Plugin is now disabled")
    }

    private fun setupEconomy(): Boolean {
        if (server.pluginManager.getPlugin("Vault") == null) return false
        val rsp = server.servicesManager.getRegistration(Economy::class.java) ?: return false
        economy = rsp.provider
        return true
    }
}