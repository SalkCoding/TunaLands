package com.salkcoding.tunalands.gui.render.settinggui

import br.com.devsrsouza.kotlinbukkitapi.extensions.item.displayName
import com.salkcoding.tunalands.gui.GuiInterface
import com.salkcoding.tunalands.guiManager
import com.salkcoding.tunalands.lands.Lands
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.util.backButton
import com.salkcoding.tunalands.util.times
import com.salkcoding.tunalands.util.toColoredText
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory

class MemberSettingGui(private val player: Player, private val lands: Lands, private val rank: Rank) : GuiInterface {

    //First row
    private val canPVP = (Material.DIAMOND_SWORD * 1).apply { this.displayName("${ChatColor.WHITE}PVP") }
    private val breakBlock = (Material.DIAMOND_PICKAXE * 1).apply { this.displayName("${ChatColor.WHITE}블록 부수기") }
    private val placeBlock = (Material.WHITE_CONCRETE * 1).apply { this.displayName("${ChatColor.WHITE}블록 설치") }
    private val canHurt = (Material.RED_DYE * 1).apply { this.displayName("${ChatColor.WHITE}데미지") }
    private val pickupExp = (Material.EXPERIENCE_BOTTLE * 1).apply { this.displayName("${ChatColor.WHITE}경험치 오브 줍기") }
    private val pickupItem = (Material.PUMPKIN_SEEDS * 1).apply { this.displayName("${ChatColor.WHITE}아이템 줍기") }
    private val dropItem = (Material.MELON_SEEDS * 1).apply { this.displayName("${ChatColor.WHITE}아이템 버리기") }
    private val openChest = (Material.CHEST * 1).apply { this.displayName("${ChatColor.WHITE}창고 사용") }
    private val eatCake = (Material.CAKE * 1).apply { this.displayName("${ChatColor.WHITE}케이크 소비") }

    //Second row
    private val useCircuit = (Material.REPEATER * 1).apply { this.displayName("${ChatColor.WHITE}회로 조작") }
    private val useLever = (Material.LEVER * 1).apply { this.displayName("${ChatColor.WHITE}레버 사용") }
    private val useButton = (Material.STONE_BUTTON * 1).apply { this.displayName("${ChatColor.WHITE}버튼 사용") }
    private val usePressureSensor =
        (Material.OAK_PRESSURE_PLATE * 1).apply { this.displayName("${ChatColor.WHITE}압력판 사용") }
    private val useDoor = (Material.OAK_DOOR * 1).apply { this.displayName("${ChatColor.WHITE}문 사용") }
    private val useTrapdoor = (Material.OAK_TRAPDOOR * 1).apply { this.displayName("${ChatColor.WHITE}다락문 사용") }
    private val useFenceGate = (Material.OAK_FENCE_GATE * 1).apply { this.displayName("${ChatColor.WHITE}울타리 문 사용") }
    private val useHopper = (Material.HOPPER * 1).apply { this.displayName("${ChatColor.WHITE}깔대기 사용") }
    private val useDispenserAndDropper =
        (Material.DISPENSER * 1).apply { this.displayName("${ChatColor.WHITE}발사기/공급기 사용") }

    //Third row
    private val useCraftTable = (Material.CRAFTING_TABLE * 1).apply { this.displayName("${ChatColor.WHITE}작업대 사용") }
    private val useFurnace = (Material.FURNACE * 1).apply { this.displayName("${ChatColor.WHITE}화로 사용") }
    private val useBed = (Material.RED_BED * 1).apply { this.displayName("${ChatColor.WHITE}침대 사용") }
    private val useEnchantingTable =
        (Material.ENCHANTING_TABLE * 1).apply { this.displayName("${ChatColor.WHITE}인첸트 테이블 사용") }
    private val useAnvil = (Material.ANVIL * 1).apply { this.displayName("${ChatColor.WHITE}모루 사용") }
    private val useCauldron = (Material.CAULDRON * 1).apply { this.displayName("${ChatColor.WHITE}가마솥 사용") }
    private val useBrewingStand = (Material.BREWING_STAND * 1).apply { this.displayName("${ChatColor.WHITE}양조기 사용") }
    private val useBeacon = (Material.BEACON * 1).apply { this.displayName("${ChatColor.WHITE}신호기 사용") }
    private val useArmorStand = (Material.ARMOR_STAND * 1).apply { this.displayName("${ChatColor.WHITE}갑옷 거치대 사용") }

    //Fourth row
    private val canSow = (Material.WHEAT_SEEDS * 1).apply { this.displayName("${ChatColor.WHITE}농작물 심기") }
    private val canHarvest = (Material.DIAMOND_HOE * 1).apply { this.displayName("${ChatColor.WHITE}농작물 수확") }
    private val canBreed = (Material.WHEAT * 1).apply { this.displayName("${ChatColor.WHITE}동물 교배") }
    private val useBucket = (Material.BUCKET * 1).apply { this.displayName("${ChatColor.WHITE}양동이 사용") }
    private val useMilk = (Material.MILK_BUCKET * 1).apply { this.displayName("${ChatColor.WHITE}우유 마시기") }
    private val throwEgg = (Material.EGG * 1).apply { this.displayName("${ChatColor.WHITE}달걀 던지기") }
    private val useShears = (Material.SHEARS * 1).apply { this.displayName("${ChatColor.WHITE}양털 깎기") }
    private val useFlintAndSteel =
        (Material.FLINT_AND_STEEL * 1).apply { this.displayName("${ChatColor.WHITE}부싯돌과 부시 사용") }
    private val canRuinFarmland = (Material.FARMLAND * 1).apply { this.displayName("${ChatColor.WHITE}짓밟기") }

    //Fifth row
    private val useMinecart = (Material.MINECART * 1).apply { this.displayName("${ChatColor.WHITE}마인카트 사용") }
    private val canFishing = (Material.FISHING_ROD * 1).apply { this.displayName("${ChatColor.WHITE}낚시") }
    private val useBoat = (Material.OAK_BOAT * 1).apply { this.displayName("${ChatColor.WHITE}배 사용") }
    private val canRiding = (Material.SADDLE * 1).apply { this.displayName("${ChatColor.WHITE}라이딩") }
    private val useChestedHorse =
        (Material.DIAMOND_HORSE_ARMOR * 1).apply { this.displayName("${ChatColor.WHITE}말 인벤토리 사용") }
    private val useLead = (Material.LEAD * 1).apply { this.displayName("${ChatColor.WHITE}끈 사용") }
    private val breakItemFrame = (Material.ITEM_FRAME * 1).apply { this.displayName("${ChatColor.WHITE}아이템 액자 부수기") }
    private val useNoteBlock = (Material.NOTE_BLOCK * 1).apply { this.displayName("${ChatColor.WHITE}노트블록 사용") }
    private val useJukebox = (Material.JUKEBOX * 1).apply { this.displayName("${ChatColor.WHITE}주크박스 사용") }

    override fun render(inv: Inventory) {
        val setting = lands.memberSetting

        //First row
        canPVP.apply { this.lore = listOf("${ChatColor.WHITE}상태: ${setting.canPVP.toColoredText()}") }
        breakBlock.apply { this.lore = listOf("${ChatColor.WHITE}상태: ${setting.breakBlock.toColoredText()}") }
        placeBlock.apply { this.lore = listOf("${ChatColor.WHITE}상태: ${setting.placeBlock.toColoredText()}") }
        canHurt.apply { this.lore = listOf("${ChatColor.WHITE}상태: ${setting.canHurt.toColoredText()}") }
        pickupExp.apply { this.lore = listOf("${ChatColor.WHITE}상태: ${setting.pickupExp.toColoredText()}") }
        pickupItem.apply { this.lore = listOf("${ChatColor.WHITE}상태: ${setting.pickupItem.toColoredText()}") }
        dropItem.apply { this.lore = listOf("${ChatColor.WHITE}상태: ${setting.dropItem.toColoredText()}") }
        openChest.apply { this.lore = listOf("${ChatColor.WHITE}상태: ${setting.openChest.toColoredText()}") }
        eatCake.apply { this.lore = listOf("${ChatColor.WHITE}상태: ${setting.eatCake.toColoredText()}") }

        //Second row
        useCircuit.apply { this.lore = listOf("${ChatColor.WHITE}상태: ${setting.useCircuit.toColoredText()}") }
        useLever.apply { this.lore = listOf("${ChatColor.WHITE}상태: ${setting.useLever.toColoredText()}") }
        useButton.apply { this.lore = listOf("${ChatColor.WHITE}상태: ${setting.useButton.toColoredText()}") }
        usePressureSensor.apply {
            this.lore = listOf("${ChatColor.WHITE}상태: ${setting.usePressureSensor.toColoredText()}")
        }
        useDoor.apply { this.lore = listOf("${ChatColor.WHITE}상태: ${setting.useDoor.toColoredText()}") }
        useTrapdoor.apply { this.lore = listOf("${ChatColor.WHITE}상태: ${setting.useTrapdoor.toColoredText()}") }
        useFenceGate.apply { this.lore = listOf("${ChatColor.WHITE}상태: ${setting.useFenceGate.toColoredText()}") }
        useHopper.apply { this.lore = listOf("${ChatColor.WHITE}상태: ${setting.useHopper.toColoredText()}") }
        useDispenserAndDropper.apply {
            this.lore = listOf("${ChatColor.WHITE}상태: ${setting.useDispenserAndDropper.toColoredText()}")
        }

        //Third row
        useCraftTable.apply { this.lore = listOf("${ChatColor.WHITE}상태: ${setting.useCraftTable.toColoredText()}") }
        useFurnace.apply { this.lore = listOf("${ChatColor.WHITE}상태: ${setting.useFurnace.toColoredText()}") }
        useBed.apply { this.lore = listOf("${ChatColor.WHITE}상태: ${setting.useBed.toColoredText()}") }
        useEnchantingTable.apply {
            this.lore = listOf("${ChatColor.WHITE}상태: ${setting.useEnchantingTable.toColoredText()}")
        }
        useAnvil.apply { this.lore = listOf("${ChatColor.WHITE}상태: ${setting.useAnvil.toColoredText()}") }
        useCauldron.apply { this.lore = listOf("${ChatColor.WHITE}상태: ${setting.useCauldron.toColoredText()}") }
        useBrewingStand.apply { this.lore = listOf("${ChatColor.WHITE}상태: ${setting.useBrewingStand.toColoredText()}") }
        useBeacon.apply { this.lore = listOf("${ChatColor.WHITE}상태: ${setting.useBeacon.toColoredText()}") }
        useArmorStand.apply { this.lore = listOf("${ChatColor.WHITE}상태: ${setting.useArmorStand.toColoredText()}") }

        //Fourth row
        canSow.apply { this.lore = listOf("${ChatColor.WHITE}상태: ${setting.canSow.toColoredText()}") }
        canHarvest.apply { this.lore = listOf("${ChatColor.WHITE}상태: ${setting.canHarvest.toColoredText()}") }
        canBreed.apply { this.lore = listOf("${ChatColor.WHITE}상태: ${setting.canBreed.toColoredText()}") }
        useBucket.apply { this.lore = listOf("${ChatColor.WHITE}상태: ${setting.useBucket.toColoredText()}") }
        useMilk.apply { this.lore = listOf("${ChatColor.WHITE}상태: ${setting.useMilk.toColoredText()}") }
        throwEgg.apply { this.lore = listOf("${ChatColor.WHITE}상태: ${setting.throwEgg.toColoredText()}") }
        useShears.apply { this.lore = listOf("${ChatColor.WHITE}상태: ${setting.useShears.toColoredText()}") }
        useFlintAndSteel.apply {
            this.lore = listOf("${ChatColor.WHITE}상태: ${setting.useFlintAndSteel.toColoredText()}")
        }
        canRuinFarmland.apply { this.lore = listOf("${ChatColor.WHITE}상태: ${setting.canRuinFarmland.toColoredText()}") }

        //Fifth row
        useMinecart.apply { this.lore = listOf("${ChatColor.WHITE}상태: ${setting.useMinecart.toColoredText()}") }
        canFishing.apply { this.lore = listOf("${ChatColor.WHITE}상태: ${setting.canFishing.toColoredText()}") }
        useBoat.apply { this.lore = listOf("${ChatColor.WHITE}상태: ${setting.useBoat.toColoredText()}") }
        canRiding.apply { this.lore = listOf("${ChatColor.WHITE}상태: ${setting.canRiding.toColoredText()}") }
        useChestedHorse.apply { this.lore = listOf("${ChatColor.WHITE}상태: ${setting.useChestedHorse.toColoredText()}") }
        useLead.apply { this.lore = listOf("${ChatColor.WHITE}상태: ${setting.useLead.toColoredText()}") }
        breakItemFrame.apply { this.lore = listOf("${ChatColor.WHITE}상태: ${setting.breakItemFrame.toColoredText()}") }
        useNoteBlock.apply { this.lore = listOf("${ChatColor.WHITE}상태: ${setting.useNoteBlock.toColoredText()}") }
        useJukebox.apply { this.lore = listOf("${ChatColor.WHITE}상태: ${setting.useJukebox.toColoredText()}") }

        inv.setItem(0, backButton)
        inv.setItem(8, backButton)

        inv.setItem(9, canPVP)
        inv.setItem(10, breakBlock)
        inv.setItem(11, placeBlock)
        inv.setItem(12, canHurt)
        inv.setItem(13, pickupExp)
        inv.setItem(14, pickupItem)
        inv.setItem(15, dropItem)
        inv.setItem(16, openChest)
        inv.setItem(17, eatCake)
        inv.setItem(18, useCircuit)
        inv.setItem(19, useLever)
        inv.setItem(20, useButton)
        inv.setItem(21, usePressureSensor)
        inv.setItem(22, useDoor)
        inv.setItem(23, useTrapdoor)
        inv.setItem(24, useFenceGate)
        inv.setItem(25, useHopper)
        inv.setItem(26, useDispenserAndDropper)
        inv.setItem(27, useCraftTable)
        inv.setItem(28, useFurnace)
        inv.setItem(29, useBed)
        inv.setItem(30, useEnchantingTable)
        inv.setItem(31, useAnvil)
        inv.setItem(32, useCauldron)
        inv.setItem(33, useBrewingStand)
        inv.setItem(34, useBeacon)
        inv.setItem(35, useArmorStand)
        inv.setItem(36, canSow)
        inv.setItem(37, canHarvest)
        inv.setItem(38, canBreed)
        inv.setItem(39, useBucket)
        inv.setItem(40, useMilk)
        inv.setItem(41, throwEgg)
        inv.setItem(42, useShears)
        inv.setItem(43, useFlintAndSteel)
        inv.setItem(44, canRuinFarmland)
        inv.setItem(45, useMinecart)
        inv.setItem(46, canFishing)
        inv.setItem(47, useBoat)
        inv.setItem(48, canRiding)
        inv.setItem(49, useChestedHorse)
        inv.setItem(50, useLead)
        inv.setItem(51, breakItemFrame)
        inv.setItem(52, useNoteBlock)
        inv.setItem(53, useJukebox)
    }

    override fun onClick(event: InventoryClickEvent) {
        event.isCancelled = true
        if (event.action != InventoryAction.PICKUP_ALL && event.action != InventoryAction.PICKUP_HALF)
            return

        val setting = lands.memberSetting
        val inv = event.inventory
        //First row
        when (event.rawSlot) {
            0, 8 -> {
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
                player.openSettingGui(lands, rank)//Back button
            }
            9 -> {
                setting.canPVP = !setting.canPVP
                canPVP.apply {
                    this.lore = listOf("${ChatColor.WHITE}상태: ${setting.canPVP.toColoredText()}")
                    inv.setItem(9, this)
                }
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
            }
            10 -> {
                setting.breakBlock = !setting.breakBlock
                breakBlock.apply {
                    this.lore = listOf("${ChatColor.WHITE}상태: ${setting.breakBlock.toColoredText()}")
                    inv.setItem(10, this)
                }
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
            }
            11 -> {
                setting.placeBlock = !setting.placeBlock
                placeBlock.apply {
                    this.lore = listOf("${ChatColor.WHITE}상태: ${setting.placeBlock.toColoredText()}")
                    inv.setItem(11, this)
                }
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
            }
            12 -> {
                setting.canHurt = !setting.canHurt
                canHurt.apply {
                    this.lore = listOf("${ChatColor.WHITE}상태: ${setting.canHurt.toColoredText()}")
                    inv.setItem(12, this)
                }
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
            }
            13 -> {
                setting.pickupExp = !setting.pickupExp
                pickupExp.apply {
                    this.lore = listOf("${ChatColor.WHITE}상태: ${setting.pickupExp.toColoredText()}")
                    inv.setItem(13, this)
                }
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
            }
            14 -> {
                setting.pickupItem = !setting.pickupItem
                pickupItem.apply {
                    this.lore = listOf("${ChatColor.WHITE}상태: ${setting.pickupItem.toColoredText()}")
                    inv.setItem(14, this)
                }
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
            }
            15 -> {
                setting.dropItem = !setting.dropItem
                dropItem.apply {
                    this.lore = listOf("${ChatColor.WHITE}상태: ${setting.dropItem.toColoredText()}")
                    inv.setItem(15, this)
                }
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
            }
            16 -> {
                setting.openChest = !setting.openChest
                openChest.apply {
                    this.lore = listOf("${ChatColor.WHITE}상태: ${setting.openChest.toColoredText()}")
                    inv.setItem(16, this)
                }
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
            }
            17 -> {
                setting.eatCake = !setting.eatCake
                eatCake.apply {
                    this.lore = listOf("${ChatColor.WHITE}상태: ${setting.eatCake.toColoredText()}")
                    inv.setItem(17, this)
                }
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
            }
            //Second row
            18 -> {
                setting.useCircuit = !setting.useCircuit
                useCircuit.apply {
                    this.lore = listOf("${ChatColor.WHITE}상태: ${setting.useCircuit.toColoredText()}")
                    inv.setItem(18, this)
                }
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
            }
            19 -> {
                setting.useLever = !setting.useLever
                useLever.apply {
                    this.lore = listOf("${ChatColor.WHITE}상태: ${setting.useLever.toColoredText()}")
                    inv.setItem(19, this)
                }
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
            }
            20 -> {
                setting.useButton = !setting.useButton
                useButton.apply {
                    this.lore = listOf("${ChatColor.WHITE}상태: ${setting.useButton.toColoredText()}")
                    inv.setItem(20, this)
                }
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
            }
            21 -> {
                setting.usePressureSensor = !setting.usePressureSensor
                usePressureSensor.apply {
                    this.lore = listOf("${ChatColor.WHITE}상태: ${setting.usePressureSensor.toColoredText()}")
                    inv.setItem(21, this)
                }
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
            }
            22 -> {
                setting.useDoor = !setting.useDoor
                useDoor.apply {
                    this.lore = listOf("${ChatColor.WHITE}상태: ${setting.useDoor.toColoredText()}")
                    inv.setItem(22, this)
                }
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
            }
            23 -> {
                setting.useTrapdoor = !setting.useTrapdoor
                useTrapdoor.apply {
                    this.lore = listOf("${ChatColor.WHITE}상태: ${setting.useTrapdoor.toColoredText()}")
                    inv.setItem(23, this)
                }
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
            }
            24 -> {
                setting.useFenceGate = !setting.useFenceGate
                useFenceGate.apply {
                    this.lore = listOf("${ChatColor.WHITE}상태: ${setting.useFenceGate.toColoredText()}")
                    inv.setItem(24, this)
                }
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
            }
            25 -> {
                setting.useHopper = !setting.useHopper
                useHopper.apply {
                    this.lore = listOf("${ChatColor.WHITE}상태: ${setting.useHopper.toColoredText()}")
                    inv.setItem(25, this)
                }
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
            }
            26 -> {
                setting.useDispenserAndDropper = !setting.useDispenserAndDropper
                useDispenserAndDropper.apply {
                    this.lore = listOf("${ChatColor.WHITE}상태: ${setting.useDispenserAndDropper.toColoredText()}")
                    inv.setItem(26, this)
                }
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
            }
            //Third row
            27 -> {
                setting.useCraftTable = !setting.useCraftTable
                useCraftTable.apply {
                    this.lore = listOf("${ChatColor.WHITE}상태: ${setting.useCraftTable.toColoredText()}")
                    inv.setItem(27, this)
                }
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
            }
            28 -> {
                setting.useFurnace = !setting.useFurnace
                useFurnace.apply {
                    this.lore = listOf("${ChatColor.WHITE}상태: ${setting.useFurnace.toColoredText()}")
                    inv.setItem(28, this)
                }
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
            }
            29 -> {
                setting.useBed = !setting.useBed
                useBed.apply {
                    this.lore = listOf("${ChatColor.WHITE}상태: ${setting.useBed.toColoredText()}")
                    inv.setItem(29, this)
                }
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
            }
            30 -> {
                setting.useEnchantingTable = !setting.useEnchantingTable
                useEnchantingTable.apply {
                    this.lore = listOf("${ChatColor.WHITE}상태: ${setting.useEnchantingTable.toColoredText()}")
                    inv.setItem(30, this)
                }
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
            }
            31 -> {
                setting.useAnvil = !setting.useAnvil
                useAnvil.apply {
                    this.lore = listOf("${ChatColor.WHITE}상태: ${setting.useAnvil.toColoredText()}")
                    inv.setItem(31, this)
                }
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
            }
            32 -> {
                setting.useCauldron = !setting.useCauldron
                useCauldron.apply {
                    this.lore = listOf("${ChatColor.WHITE}상태: ${setting.useCauldron.toColoredText()}")
                    inv.setItem(32, this)
                }
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
            }
            33 -> {
                setting.useBrewingStand = !setting.useBrewingStand
                useBrewingStand.apply {
                    this.lore = listOf("${ChatColor.WHITE}상태: ${setting.useBrewingStand.toColoredText()}")
                    inv.setItem(33, this)
                }
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
            }
            34 -> {
                setting.useBeacon = !setting.useBeacon
                useBeacon.apply {
                    this.lore = listOf("${ChatColor.WHITE}상태: ${setting.useBeacon.toColoredText()}")
                    inv.setItem(34, this)
                }
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
            }
            35 -> {
                setting.useArmorStand = !setting.useArmorStand
                useArmorStand.apply {
                    this.lore = listOf("${ChatColor.WHITE}상태: ${setting.useArmorStand.toColoredText()}")
                    inv.setItem(35, this)
                }
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
            }
            //Fourth row
            36 -> {
                setting.canSow = !setting.canSow
                canSow.apply {
                    this.lore = listOf("${ChatColor.WHITE}상태: ${setting.canSow.toColoredText()}")
                    inv.setItem(36, this)
                }
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
            }
            37 -> {
                setting.canHarvest = !setting.canHarvest
                canHarvest.apply {
                    this.lore = listOf("${ChatColor.WHITE}상태: ${setting.canHarvest.toColoredText()}")
                    inv.setItem(37, this)
                }
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
            }
            38 -> {
                setting.canBreed = !setting.canBreed
                canBreed.apply {
                    this.lore = listOf("${ChatColor.WHITE}상태: ${setting.canBreed.toColoredText()}")
                    inv.setItem(38, this)
                }
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
            }
            39 -> {
                setting.useBucket = !setting.useBucket
                useBucket.apply {
                    this.lore = listOf("${ChatColor.WHITE}상태: ${setting.useBucket.toColoredText()}")
                    inv.setItem(39, this)
                }
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
            }
            40 -> {
                setting.useMilk = !setting.useMilk
                useMilk.apply {
                    this.lore = listOf("${ChatColor.WHITE}상태: ${setting.useMilk.toColoredText()}")
                    inv.setItem(40, this)
                }
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
            }
            41 -> {
                setting.throwEgg = !setting.throwEgg
                throwEgg.apply {
                    this.lore = listOf("${ChatColor.WHITE}상태: ${setting.throwEgg.toColoredText()}")
                    inv.setItem(41, this)
                }
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
            }
            42 -> {
                setting.useShears = !setting.useShears
                useShears.apply {
                    this.lore = listOf("${ChatColor.WHITE}상태: ${setting.useShears.toColoredText()}")
                    inv.setItem(42, this)
                }
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
            }
            43 -> {
                setting.useFlintAndSteel = !setting.useFlintAndSteel
                useFlintAndSteel.apply {
                    this.lore = listOf("${ChatColor.WHITE}상태: ${setting.useFlintAndSteel.toColoredText()}")
                    inv.setItem(43, this)
                }
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
            }
            44 -> {
                setting.canRuinFarmland = !setting.canRuinFarmland
                canRuinFarmland.apply {
                    this.lore = listOf("${ChatColor.WHITE}상태: ${setting.canRuinFarmland.toColoredText()}")
                    inv.setItem(44, this)
                }
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
            }
            //Fifth row
            45 -> {
                setting.useMinecart = !setting.useMinecart
                useMinecart.apply {
                    this.lore = listOf("${ChatColor.WHITE}상태: ${setting.useMinecart.toColoredText()}")
                    inv.setItem(45, this)
                }
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
            }
            46 -> {
                setting.canFishing = !setting.canFishing
                canFishing.apply {
                    this.lore = listOf("${ChatColor.WHITE}상태: ${setting.canFishing.toColoredText()}")
                    inv.setItem(46, this)
                }
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
            }
            47 -> {
                setting.useBoat = !setting.useBoat
                useBoat.apply {
                    this.lore = listOf("${ChatColor.WHITE}상태: ${setting.useBoat.toColoredText()}")
                    inv.setItem(47, this)
                }
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
            }
            48 -> {
                setting.canRiding = !setting.canRiding
                canRiding.apply {
                    this.lore = listOf("${ChatColor.WHITE}상태: ${setting.canRiding.toColoredText()}")
                    inv.setItem(48, this)
                }
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
            }
            49 -> {
                setting.useChestedHorse = !setting.useChestedHorse
                useChestedHorse.apply {
                    this.lore = listOf("${ChatColor.WHITE}상태: ${setting.useChestedHorse.toColoredText()}")
                    inv.setItem(49, this)
                }
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
            }
            50 -> {
                setting.useLead = !setting.useLead
                useLead.apply {
                    this.lore = listOf("${ChatColor.WHITE}상태: ${setting.useLead.toColoredText()}")
                    inv.setItem(50, this)
                }
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
            }
            51 -> {
                setting.breakItemFrame = !setting.breakItemFrame
                breakItemFrame.apply {
                    this.lore = listOf("${ChatColor.WHITE}상태: ${setting.breakItemFrame.toColoredText()}")
                    inv.setItem(51, this)
                }
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
            }
            52 -> {
                setting.useNoteBlock = !setting.useNoteBlock
                useNoteBlock.apply {
                    this.lore = listOf("${ChatColor.WHITE}상태: ${setting.useNoteBlock.toColoredText()}")
                    inv.setItem(52, this)
                }
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
            }
            53 -> {
                setting.useJukebox = !setting.useJukebox
                useJukebox.apply {
                    this.lore = listOf("${ChatColor.WHITE}상태: ${setting.useJukebox.toColoredText()}")
                    inv.setItem(53, this)
                }
                player.playSound(player.location, Sound.UI_BUTTON_CLICK, 0.5f, 1.0f)
            }
        }
    }

    override fun onClose(event: InventoryCloseEvent) {
        guiManager.guiMap.remove(event.view)
    }
}

fun Player.openMemberSettingGui(lands: Lands, rank: Rank) {
    val inventory = Bukkit.createInventory(null, 54, "멤버 설정")
    val gui = MemberSettingGui(this, lands, rank)
    gui.render(inventory)

    val view = this.openInventory(inventory)!!
    guiManager.guiMap[view] = gui
}