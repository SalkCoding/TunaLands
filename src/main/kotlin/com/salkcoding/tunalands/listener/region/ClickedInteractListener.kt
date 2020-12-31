package com.salkcoding.tunalands.listener.region

import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.util.errorFormat
import org.bukkit.Material
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class ClickedInteractListener : Listener {

    @EventHandler
    fun onClicked(event: PlayerInteractEvent) {
        if (event.useInteractedBlock() == Event.Result.DENY) return
        if (event.action != Action.RIGHT_CLICK_BLOCK) return
        val block = event.clickedBlock!!

        val lands = landManager.getLandsWithChunk(block.chunk) ?: return
        if (!lands.enable) return

        val player = event.player
        if (player.uniqueId in lands.memberMap) {
            val setting = when (lands.memberMap[player.uniqueId]!!.rank) {
                Rank.OWNER, Rank.DELEGATOR -> return
                Rank.MEMBER -> lands.memberSetting
                Rank.PARTTIMEJOB -> lands.partTimeJobSetting
                Rank.VISITOR -> lands.visitorSetting
            }

            when (block.type) {
                Material.CAKE -> {
                    if (!setting.eatCake)
                        event.isCancelled = true
                }
                Material.CHEST,
                Material.ENDER_CHEST -> {
                    if (!setting.openChest)
                        event.isCancelled = true
                }
                Material.REPEATER,
                Material.COMPARATOR,
                Material.DAYLIGHT_DETECTOR,
                Material.TRAPPED_CHEST -> {
                    if (!setting.useCircuit)
                        event.isCancelled = true
                }
                Material.LEVER -> {
                    if (!setting.useLever)
                        event.isCancelled = true
                }
                Material.STONE_BUTTON,
                Material.OAK_BUTTON,
                Material.SPRUCE_BUTTON,
                Material.BIRCH_BUTTON,
                Material.JUNGLE_BUTTON,
                Material.ACACIA_BUTTON,
                Material.DARK_OAK_BUTTON,
                Material.CRIMSON_BUTTON,
                Material.WARPED_BUTTON,
                Material.POLISHED_BLACKSTONE_BUTTON -> {
                    if (!setting.useButton)
                        event.isCancelled = true
                }
                Material.IRON_DOOR,
                Material.OAK_DOOR,
                Material.SPRUCE_DOOR,
                Material.BIRCH_DOOR,
                Material.JUNGLE_DOOR,
                Material.ACACIA_DOOR,
                Material.DARK_OAK_DOOR,
                Material.CRIMSON_DOOR,
                Material.WARPED_DOOR -> {
                    if (!setting.useDoor)
                        event.isCancelled = true
                }
                Material.IRON_TRAPDOOR,
                Material.OAK_TRAPDOOR,
                Material.SPRUCE_TRAPDOOR,
                Material.BIRCH_TRAPDOOR,
                Material.JUNGLE_TRAPDOOR,
                Material.ACACIA_TRAPDOOR,
                Material.DARK_OAK_TRAPDOOR,
                Material.CRIMSON_TRAPDOOR,
                Material.WARPED_TRAPDOOR -> {
                    if (!setting.useTrapdoor)
                        event.isCancelled = true
                }
                Material.OAK_FENCE_GATE,
                Material.SPRUCE_FENCE_GATE,
                Material.BIRCH_FENCE_GATE,
                Material.JUNGLE_FENCE_GATE,
                Material.ACACIA_FENCE_GATE,
                Material.DARK_OAK_FENCE_GATE,
                Material.CRIMSON_FENCE_GATE,
                Material.WARPED_FENCE_GATE -> {
                    if (!setting.useFenceGate)
                        event.isCancelled = true
                }
                Material.HOPPER -> {
                    if (!setting.useHopper)
                        event.isCancelled = true
                }
                Material.DISPENSER,
                Material.DROPPER -> {
                    if (!setting.useDispenserAndDropper)
                        event.isCancelled = true
                }
                Material.CRAFTING_TABLE -> {
                    if (!setting.useCraftTable)
                        event.isCancelled = true
                }
                Material.FURNACE -> {
                    if (!setting.useCraftTable)
                        event.isCancelled = true
                }
                Material.WHITE_BED,
                Material.ORANGE_BED,
                Material.MAGENTA_BED,
                Material.LIGHT_BLUE_BED,
                Material.YELLOW_BED,
                Material.LIME_BED,
                Material.PINK_BED,
                Material.GRAY_BED,
                Material.LIGHT_GRAY_BED,
                Material.CYAN_BED,
                Material.PURPLE_BED,
                Material.BLUE_BED,
                Material.BROWN_BED,
                Material.GREEN_BED,
                Material.RED_BED,
                Material.BLACK_BED -> {
                    if (!setting.useBed)
                        event.isCancelled = true
                }
                Material.ENCHANTING_TABLE -> {
                    if (!setting.useEnchantingTable)
                        event.isCancelled = true
                }
                Material.ANVIL,
                Material.CHIPPED_ANVIL,
                Material.DAMAGED_ANVIL -> {
                    if (!setting.useAnvil)
                        event.isCancelled = true
                }
                Material.CAULDRON -> {
                    if (!setting.useCauldron)
                        event.isCancelled = true
                }
                Material.BREWING_STAND -> {
                    if (!setting.useBrewingStand)
                        event.isCancelled = true
                }
                Material.BEACON -> {
                    if (!setting.useBeacon)
                        event.isCancelled = true
                }
                Material.NOTE_BLOCK -> {
                    if (!setting.useNoteBlock)
                        event.isCancelled = true
                }
                Material.JUKEBOX -> {
                    if (!setting.useJukebox)
                        event.isCancelled = true
                }
                else -> return
            }
        }

        if (event.useInteractedBlock() == Event.Result.DENY)
            player.sendMessage("권한이 없습니다!".errorFormat())
    }
}