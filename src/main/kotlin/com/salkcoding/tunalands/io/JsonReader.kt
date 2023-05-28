package com.salkcoding.tunalands.io

import com.google.gson.JsonParser
import com.salkcoding.tunalands.lands.LandType
import com.salkcoding.tunalands.lands.setting.DelegatorSetting
import com.salkcoding.tunalands.lands.setting.LandSetting
import com.salkcoding.tunalands.lands.Lands
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.tunaLands
import com.salkcoding.tunalands.util.ObservableMap
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.HashMap

object JsonReader {

    fun loadPlayerLandMap(): ConcurrentHashMap<UUID, Lands> {
        val folder = File(tunaLands.dataFolder, "userdata")
        if (!folder.exists())
            folder.mkdirs()

        val playerLandMap = ConcurrentHashMap<UUID, Lands>()
        folder.listFiles()?.forEach { file ->
            file.bufferedReader().use { reader ->
                val jsonObject = JsonParser.parseReader(reader).asJsonObject

                val ownerName = jsonObject["ownerName"].asString
                val ownerUUID = UUID.fromString(jsonObject["ownerUUID"].asString)
                val fuelLeft = jsonObject["fuelLeft"]?.asLong ?: 0
                val expiredMillisecond = jsonObject["expiredMillisecond"].asLong
                val enable = jsonObject["enable"].asBoolean
                val open = jsonObject["open"].asBoolean
                val recommend = jsonObject["recommend"].asInt
                val landsName = jsonObject["landsName"].asString
                val landList = HashMap<String, LandType>()
                jsonObject["landList"].asJsonArray.forEach {
                    val info = it.asJsonObject
                    landList[info["coordinate"].asString] = LandType.valueOf(info["landType"].asString)
                }
                val jsonLandHistory = jsonObject["landHistory"].asJsonObject
                val landHistory = Lands.LandHistory(
                    jsonLandHistory["visitorCount"].asLong,
                    jsonLandHistory["createdMillisecond"].asLong
                )
                val locationList = mutableListOf<Location>()
                listOf(
                    "core",
                    "memberSpawn",
                    "visitorSpawn"
                ).forEach {
                    val jsonLocation = jsonObject[it].asJsonObject
                    locationList.add(
                        Location(
                            Bukkit.getWorld(jsonLocation["world"].asString),
                            jsonLocation["x"].asDouble,
                            jsonLocation["y"].asDouble,
                            jsonLocation["z"].asDouble
                        )
                    )
                }
                val lore = mutableListOf<String>()
                jsonObject["lore"].asJsonArray.forEach {
                    lore.add(ChatColor.translateAlternateColorCodes('&', it.asString))
                }
                val welcomeMessage = mutableListOf<String>()
                jsonObject["welcomeMessage"].asJsonArray.forEach {
                    welcomeMessage.add(ChatColor.translateAlternateColorCodes('&', it.asString))
                }
                val memberMap: MutableMap<UUID, Lands.MemberData> = ObservableMap(
                    map = mutableMapOf(),
                    onChange = object : ObservableMap.Observed<UUID, Lands.MemberData> {
                        override fun syncChanges(newMap: MutableMap<UUID, Lands.MemberData>) {
                            val message = newMap.map {
                                "${it.value.uuid},${Bukkit.getOfflinePlayer(it.value.uuid).name},${it.value.rank}"
                            }.joinToString(";")

                            tunaLands.broadcastLandMembersRunnable.queue.offer(message)
                        }
                    },
                    plugin = tunaLands
                )
                jsonObject["memberMap"].asJsonArray.forEach {
                    val jsonMemberData = it.asJsonObject
                    val memberUUID = UUID.fromString(jsonMemberData["uuid"].asString)
                    memberMap[memberUUID] =
                        Lands.MemberData(
                            memberUUID,
                            Rank.valueOf(jsonMemberData["rank"].asString),
                            jsonMemberData["joined"].asLong,
                            jsonMemberData["lastLogin"].asLong
                        )
                }
                val banMap = mutableMapOf<UUID, Lands.BanData>()
                jsonObject["banMap"].asJsonArray.forEach {
                    val jsonBanData = it.asJsonObject
                    val banUUID = UUID.fromString(jsonBanData["uuid"].asString)
                    banMap[banUUID] =
                        Lands.BanData(
                            banUUID,
                            jsonBanData["banned"].asLong,
                        )
                }
                val settingList = mutableListOf<LandSetting>()
                listOf(
                    "visitorSetting",
                    "partTimeJobSetting",
                    "memberSetting"
                ).forEach {
                    val jsonSetting = jsonObject[it].asJsonObject
                    settingList.add(
                        LandSetting(
                            jsonSetting["canPVP"].asBoolean,
                            jsonSetting["breakBlock"].asBoolean,
                            jsonSetting["placeBlock"].asBoolean,
                            jsonSetting["canHurt"].asBoolean,
                            jsonSetting["pickupExp"].asBoolean,
                            jsonSetting["pickupItem"].asBoolean,
                            jsonSetting["dropItem"].asBoolean,
                            jsonSetting["openChest"].asBoolean,
                            jsonSetting["eatCake"].asBoolean,
                            jsonSetting["useCircuit"].asBoolean,
                            jsonSetting["useLever"].asBoolean,
                            jsonSetting["useButton"].asBoolean,
                            jsonSetting["usePressureSensor"].asBoolean,
                            jsonSetting["useDoor"].asBoolean,
                            jsonSetting["useTrapdoor"].asBoolean,
                            jsonSetting["useFenceGate"].asBoolean,
                            jsonSetting["useHopper"].asBoolean,
                            jsonSetting["useDispenserAndDropper"].asBoolean,
                            jsonSetting["useCraftTable"].asBoolean,
                            jsonSetting["useFurnace"].asBoolean,
                            jsonSetting["useBed"].asBoolean,
                            jsonSetting["useEnchantingTable"].asBoolean,
                            jsonSetting["useAnvil"].asBoolean,
                            jsonSetting["useCauldron"].asBoolean,
                            jsonSetting["useBrewingStand"].asBoolean,
                            jsonSetting["useBeacon"].asBoolean,
                            jsonSetting["useArmorStand"].asBoolean,
                            jsonSetting["canSow"].asBoolean,
                            jsonSetting["canHarvest"].asBoolean,
                            jsonSetting["canBreed"].asBoolean,
                            jsonSetting["useBucket"].asBoolean,
                            jsonSetting["useMilk"].asBoolean,
                            jsonSetting["throwEgg"].asBoolean,
                            jsonSetting["useShears"].asBoolean,
                            jsonSetting["useFlintAndSteel"].asBoolean,
                            jsonSetting["canRuinFarmland"].asBoolean,
                            jsonSetting["useMinecart"].asBoolean,
                            jsonSetting["canFishing"].asBoolean,
                            jsonSetting["useBoat"].asBoolean,
                            jsonSetting["canRiding"].asBoolean,
                            jsonSetting["useChestedHorse"].asBoolean,
                            jsonSetting["useLead"].asBoolean,
                            jsonSetting["breakItemFrame"].asBoolean,
                            jsonSetting["useNoteBlock"].asBoolean,
                            jsonSetting["useJukebox"].asBoolean
                        )
                    )
                }
                val jsonDelegatorSetting = jsonObject["delegatorSetting"].asJsonObject
                val delegatorSetting = DelegatorSetting(
                    jsonDelegatorSetting["canSetVisitorSetting"].asBoolean,
                    jsonDelegatorSetting["canSetPartTimeJobSetting"].asBoolean,
                    jsonDelegatorSetting["canSetMemberSetting"].asBoolean,
                    jsonDelegatorSetting["canSetSpawn"].asBoolean,
                    jsonDelegatorSetting["canBan"].asBoolean,
                    jsonDelegatorSetting["canSetRegionSetting"].asBoolean,
                )

                val upCore = locationList[0]
                val downCore = Location(upCore.world, upCore.x, upCore.y - 1, upCore.z)
                playerLandMap[ownerUUID] =
                    Lands(
                        ownerName,
                        ownerUUID,
                        landList,
                        landHistory,
                        upCore,
                        downCore,
                        fuelLeft,
                        Instant.ofEpochMilli(expiredMillisecond)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime(),

                        enable,
                        open,
                        recommend,
                        landsName,
                        lore,
                        welcomeMessage,
                        locationList[1],
                        locationList[2],
                        settingList[0],
                        settingList[1],
                        settingList[2],
                        delegatorSetting,
                        memberMap,
                        banMap
                    )
            }
        }

        return playerLandMap
    }
}