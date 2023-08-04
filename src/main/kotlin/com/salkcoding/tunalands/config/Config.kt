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
    val flag: Flag

    init {
        tunaLands.saveDefaultConfig()

        val config = tunaLands.config
        val logger = tunaLands.logger

        currentServerName = config.getString("serverName")!!
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
            configFuel.getInt("defaultFuel"),
            configFuel.getInt("imposeTime"),
            configFuel.getMapList("fuelRequirements").map {
                Fuel.FuelRequirement(
                    it["numOfMembers"] as Int,
                    it["dayPerFuel"] as Int
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