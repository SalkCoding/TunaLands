package com.salkcoding.tunalands.file

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.tunaLands
import java.io.File

object PlayerLandMapWriter {

    fun savePlayerLandMap() {
        val folder = File(tunaLands.dataFolder, "userdata")
        if (!folder.exists())
            folder.mkdirs()

        landManager.getPlayerLandMap().forEach { (uuid, lands) ->
            val file = File(folder, "${uuid}.json")
            if (!file.exists())
                file.createNewFile()

            val jsonObject = JsonObject()

            //Initialize data
            val jsonLandList = JsonArray()
            lands.landMap.forEach { (query, type) ->
                val info = JsonObject()
                info.addProperty("coordinate", query)
                info.addProperty("landType", type.name)
                jsonLandList.add(info)
            }

            val landHistory = lands.landHistory
            val jsonLandHistory = JsonObject().apply {
                this.addProperty("visitorCount", landHistory.visitorCount)
                this.addProperty("createdMillisecond", landHistory.createdMillisecond)
            }

            //It just saved upCore data. So when we load it, we have to calculate the downCore location.
            val jsonLocationArray = JsonArray()
            listOf(
                lands.upCoreLocation,
                lands.visitorSpawn,
                lands.memberSpawn
            ).forEach { location ->
                jsonLocationArray.add(JsonObject().apply {
                    this.addProperty("world", location.world.name)
                    this.addProperty("x", location.x)
                    this.addProperty("y", location.y)
                    this.addProperty("z", location.z)
                })
            }
            val jsonLore = JsonArray()
            lands.lore.forEach {
                jsonLore.add(it)
            }

            val jsonWelcomeMessage = JsonArray()
            lands.welcomeMessage.forEach {
                jsonWelcomeMessage.add(it)
            }

            val jsonMemberMap = JsonArray()
            lands.memberMap.forEach { (_, memberData) ->
                val jsonMemberData = JsonObject().apply {
                    this.addProperty("uuid", memberData.uuid.toString())
                    this.addProperty("rank", memberData.rank.toString())
                    this.addProperty("joined", memberData.joined)
                    this.addProperty("lastLogin", memberData.lastLogin)
                }
                jsonMemberMap.add(jsonMemberData)
            }

            val jsonBanMap = JsonArray()
            lands.banMap.forEach { (_, memberData) ->
                val jsonMemberData = JsonObject().apply {
                    this.addProperty("uuid", memberData.uuid.toString())
                    this.addProperty("banned", memberData.banned)
                }
                jsonBanMap.add(jsonMemberData)
            }

            val jsonSettingArray = JsonArray()
            listOf(
                lands.visitorSetting,
                lands.partTimeJobSetting,
                lands.memberSetting
            ).forEach { setting ->
                jsonSettingArray.add(JsonObject().apply {
                    this.addProperty("canPVP", setting.canPVP)
                    this.addProperty("breakBlock", setting.breakBlock)
                    this.addProperty("placeBlock", setting.placeBlock)
                    this.addProperty("canHurt", setting.canHurt)
                    this.addProperty("pickupExp", setting.pickupExp)
                    this.addProperty("pickupItem", setting.pickupItem)
                    this.addProperty("dropItem", setting.dropItem)
                    this.addProperty("openChest", setting.openChest)
                    this.addProperty("eatCake", setting.eatCake)
                    this.addProperty("useCircuit", setting.useCircuit)
                    this.addProperty("useLever", setting.useLever)
                    this.addProperty("useButton", setting.useButton)
                    this.addProperty("usePressureSensor", setting.usePressureSensor)
                    this.addProperty("useDoor", setting.useDoor)
                    this.addProperty("useTrapdoor", setting.useTrapdoor)
                    this.addProperty("useFenceGate", setting.useFenceGate)
                    this.addProperty("useHopper", setting.useHopper)
                    this.addProperty("useDispenserAndDropper", setting.useDispenserAndDropper)
                    this.addProperty("useCraftTable", setting.useCraftTable)
                    this.addProperty("useFurnace", setting.useFurnace)
                    this.addProperty("useBed", setting.useBed)
                    this.addProperty("useEnchantingTable", setting.useEnchantingTable)
                    this.addProperty("useAnvil", setting.useAnvil)
                    this.addProperty("useCauldron", setting.useCauldron)
                    this.addProperty("useBrewingStand", setting.useBrewingStand)
                    this.addProperty("useBeacon", setting.useBeacon)
                    this.addProperty("useArmorStand", setting.useArmorStand)
                    this.addProperty("canSow", setting.canSow)
                    this.addProperty("canHarvest", setting.canHarvest)
                    this.addProperty("canBreed", setting.canBreed)
                    this.addProperty("useBucket", setting.useBucket)
                    this.addProperty("useMilk", setting.useMilk)
                    this.addProperty("throwEgg", setting.throwEgg)
                    this.addProperty("useShears", setting.useShears)
                    this.addProperty("useFlintAndSteel", setting.useFlintAndSteel)
                    this.addProperty("canRuinFarmland", setting.canRuinFarmland)
                    this.addProperty("useMinecart", setting.useMinecart)
                    this.addProperty("canFishing", setting.canFishing)
                    this.addProperty("useBoat", setting.useBoat)
                    this.addProperty("canRiding", setting.canRiding)
                    this.addProperty("useChestedHorse", setting.useChestedHorse)
                    this.addProperty("useLead", setting.useLead)
                    this.addProperty("breakItemFrame", setting.breakItemFrame)
                    this.addProperty("useNoteBlock", setting.useNoteBlock)
                    this.addProperty("useJukebox", setting.useJukebox)
                })
            }

            val delegatorSetting = lands.delegatorSetting
            val jsonDelegatorSetting = JsonObject().apply {
                this.addProperty("canSetVisitorSetting", delegatorSetting.canSetVisitorSetting)
                this.addProperty("canSetPartTimeJobSetting", delegatorSetting.canSetPartTimeJobSetting)
                this.addProperty("canSetMemberSetting", delegatorSetting.canSetMemberSetting)
                this.addProperty("canSetSpawn", delegatorSetting.canSetSpawn)
                this.addProperty("canBan", delegatorSetting.canBan)
                this.addProperty("canSetRegionSetting", delegatorSetting.canSetRegionSetting)
            }

            //Write upper jsonObject
            jsonObject.addProperty("ownerName", lands.ownerName)
            jsonObject.addProperty("ownerUUID", lands.ownerUUID.toString())
            jsonObject.addProperty("fuelLeft", lands.fuelLeft)
            jsonObject.addProperty("dayPerFuel", lands.dayPerFuel)
            jsonObject.addProperty("enable", lands.enable)
            jsonObject.addProperty("open", lands.open)
            jsonObject.addProperty("recommend", lands.recommend)
            jsonObject.addProperty("landsName", lands.landsName)
            jsonObject.add("landList", jsonLandList)
            jsonObject.add("landHistory", jsonLandHistory)
            jsonObject.add("core", jsonLocationArray[0])
            jsonObject.add("visitorSpawn", jsonLocationArray[1])
            jsonObject.add("memberSpawn", jsonLocationArray[2])
            jsonObject.add("lore", jsonLore)
            jsonObject.add("welcomeMessage", jsonWelcomeMessage)
            jsonObject.add("memberMap", jsonMemberMap)
            jsonObject.add("banMap", jsonBanMap)
            jsonObject.add("visitorSetting", jsonSettingArray[0])
            jsonObject.add("partTimeJobSetting", jsonSettingArray[1])
            jsonObject.add("memberSetting", jsonSettingArray[2])
            jsonObject.add("delegatorSetting", jsonDelegatorSetting)

            file.bufferedWriter().use { writer -> writer.write(jsonObject.toString()) }
        }
    }
}