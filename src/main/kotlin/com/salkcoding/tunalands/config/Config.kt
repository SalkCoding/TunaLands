package com.salkcoding.tunalands.config

import com.salkcoding.tunalands.config.section.*
import com.salkcoding.tunalands.currentServerName
import com.salkcoding.tunalands.tunaLands
import org.bukkit.Material

class Config {
    val protect: Protect
    val farm: Farm
    val fuel: Fuel
    val recommend: Recommend
    val commandCooldown: CommandCooldown
    val commandPrice: CommandPrice
    val limitWorld: List<String>
    val ignoreWorld: List<String>
    val flag: Flag
    val maxMemberLimit: Int
    val maxAlbaLimit: Int

    init {
        tunaLands.saveDefaultConfig()

        val config = tunaLands.config
        val logger = tunaLands.logger

        currentServerName = config.getString("serverName")!!

        maxMemberLimit = config.getInt("maxMemberLimit")
        maxAlbaLimit = config.getInt("maxAlbaLimit")

        //Protect
        val configProtect = config.getConfigurationSection("protect")!!
        protect = Protect(
            Material.valueOf(configProtect.getString("coreBlock")!!),
            configProtect.getInt("createPrice"),
            configProtect.getMapList("limitOccupied").map {
                Protect.LimitOccupied(
                    it["numOfMembers"] as Int,
                    it["chunk"] as Int
                )
            }
        )
        logger.info("protect: $protect")

        //Farm
        val farmSection = config.getConfigurationSection("farm")!!
        farm = Farm(
            farmSection.getInt("limitOccupied")
        )
        logger.info("farm: $farm")

        //Fuel
        val configFuel = config.getConfigurationSection("fuel")!!
        fuel = Fuel(
            configFuel.getDouble("price"),
            configFuel.getLong("defaultFuel"),
            configFuel.getMapList("fuelAddAmount").map {
                Fuel.AddAmount(
                    it["numOfMembers"] as Int,
                    (it["addAmount"].toString()).toLong()
                )
            }
        )
        logger.info("fuel: $fuel")

        //Recommend
        val configRecommend = config.getConfigurationSection("recommend")!!
        recommend = Recommend(
            configRecommend.getLong("reset"),
            configRecommend.getLong("cooldown")
        )
        logger.info("recommend: $recommend")

        //Command cooldown
        val configCommand = config.getConfigurationSection("command")!!
        val cooldownSection = configCommand.getConfigurationSection("cooldown")!!
        commandCooldown = CommandCooldown(
            cooldownSection.getLong("rejoin"),
            cooldownSection.getLong("visit"),
            cooldownSection.getLong("spawn")
        )
        logger.info("commandCooldown: $commandCooldown")

        //Command price
        val commandPriceSection = configCommand.getConfigurationSection("price")!!
        commandPrice = CommandPrice(
            commandPriceSection.getInt("setSpawnPrice"),
            commandPriceSection.getInt("renamePrice")
        )
        logger.info("commandPrice: $commandPrice")

        //Limit worlds
        limitWorld = config.getStringList("limitWorld")
        logger.info("limitWorld: $limitWorld")

        //ignore worlds
        ignoreWorld = config.getStringList("ignoreWorld")
        logger.info("ignoreWorld: $ignoreWorld")

        // Flag prices
        val flagSection = config.getConfigurationSection("flag")!!
        val flagPriceSection = flagSection.getConfigurationSection("price")!!
        flag = Flag(
            flagPriceSection.getDouble("takeProtectFlagPrice"),
            flagPriceSection.getDouble("releaseProtectFlagPrice"),
            flagPriceSection.getMapList("activePrice").map {
                Flag.ActivePrice(
                    it["chunk"] as Int,
                    it["price"] as Int
                )
            },
            flagPriceSection.getDouble("takeFarmFlagPrice"),
            flagPriceSection.getDouble("releaseFarmFlagPrice"),
        )
    }

}