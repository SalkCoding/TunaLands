package com.salkcoding.tunalands.gui.render.settinggui

import br.com.devsrsouza.kotlinbukkitapi.extensions.item.displayName
import com.salkcoding.tunalands.gui.GuiInterface
import com.salkcoding.tunalands.gui.render.openMainGui
import com.salkcoding.tunalands.guiManager
import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.util.backButton
import com.salkcoding.tunalands.util.times
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.Inventory

class MemberSettingGui(private val player: Player, private val rank: Rank) : GuiInterface {
    //First row
    private val canPVP = (Material.DIAMOND_SWORD * 1).apply { this.displayName("PVP") }
    private val breakBlock = (Material.DIAMOND_PICKAXE * 1).apply { this.displayName("블록 부수기") }
    private val placeBlock = (Material.WHITE_CONCRETE * 1).apply { this.displayName("블록 설치") }
    private val canHurt = (Material.RED_DYE * 1).apply { this.displayName("데미지") }
    private val pickupExp = (Material.EXPERIENCE_BOTTLE * 1).apply { this.displayName("경험치 오브 줍기") }
    private val pickupItem = (Material.PUMPKIN_SEEDS * 1).apply { this.displayName("아이템 줍기") }
    private val dropItem = (Material.MELON_SEEDS * 1).apply { this.displayName("아이템 버리기") }
    private val openChest = (Material.CHEST * 1).apply { this.displayName("창고 사용") }
    private val eatCake = (Material.CAKE * 1).apply { this.displayName("케이크 소비") }

    //Second row
    private val useCircuit = (Material.REPEATER * 1).apply { this.displayName("회로 조작") }
    private val useLever = (Material.LEVER * 1).apply { this.displayName("레버 사용") }
    private val useButton = (Material.STONE_BUTTON * 1).apply { this.displayName("버튼 사용") }
    private val usePressureSensor = (Material.OAK_PRESSURE_PLATE * 1).apply { this.displayName("압력판 사용") }
    private val useDoor = (Material.OAK_DOOR * 1).apply { this.displayName("문 사용") }
    private val useTrapdoor = (Material.OAK_TRAPDOOR * 1).apply { this.displayName("다락문 사용") }
    private val useFenceDoor = (Material.OAK_FENCE_GATE * 1).apply { this.displayName("울타리 문 사용") }
    private val useHopper = (Material.HOPPER * 1).apply { this.displayName("깔대기 사용") }
    private val useDispenserAndDropper = (Material.DISPENSER * 1).apply { this.displayName("발사기/공급기 사용") }

    //Third row
    private val useCraftTable = (Material.CRAFTING_TABLE * 1).apply { this.displayName("작업대 사용") }
    private val useFurnace = (Material.FURNACE * 1).apply { this.displayName("화로 사용") }
    private val useBed = (Material.RED_BED * 1).apply { this.displayName("침대 사용") }
    private val useEnchantingTable = (Material.ENCHANTING_TABLE * 1).apply { this.displayName("인첸트 테이블 사용") }
    private val useAnvil = (Material.ANVIL * 1).apply { this.displayName("모루 사용") }
    private val useCauldron = (Material.CAULDRON * 1).apply { this.displayName("가마솥 사용") }
    private val useBrewingStand = (Material.BREWING_STAND * 1).apply { this.displayName("양조기 사용") }
    private val useBeacon = (Material.BEACON * 1).apply { this.displayName("신호기 사용") }
    private val useArmorStand = (Material.ARMOR_STAND * 1).apply { this.displayName("갑옷 거치대 사용") }

    //Fourth row
    private val canSow = (Material.WHEAT_SEEDS * 1).apply { this.displayName("농작물 심기") }
    private val canHarvest = (Material.DIAMOND_HOE * 1).apply { this.displayName("농작물 수확") }
    private val canFeed = (Material.WHEAT * 1).apply { this.displayName("동물 먹이 주기") }
    private val useBucket = (Material.BUCKET * 1).apply { this.displayName("양동이 사용") }
    private val useMilk = (Material.MILK_BUCKET * 1).apply { this.displayName("우유 마시기") }
    private val throwEgg = (Material.EGG * 1).apply { this.displayName("달걀 던지기") }
    private val useShears = (Material.SHEARS * 1).apply { this.displayName("양털 깎기") }
    private val useFlintAndSteel = (Material.FLINT_AND_STEEL * 1).apply { this.displayName("부싯돌과 부시 사용") }
    private val canRuinFarmland = (Material.FARMLAND * 1).apply { this.displayName("짓밟기") }

    //Fifth row
    private val useMinecart = (Material.MINECART * 1).apply { this.displayName("마인카트 사용") }
    private val canFishing = (Material.FISHING_ROD * 1).apply { this.displayName("낚시") }
    private val useBoat = (Material.OAK_BOAT * 1).apply { this.displayName("배 사용") }
    private val canRiding = (Material.SADDLE * 1).apply { this.displayName("라이딩") }
    private val useChestedHorse = (Material.DIAMOND_HORSE_ARMOR * 1).apply { this.displayName("말 인벤토리 이용") }
    private val useLead = (Material.LEAD * 1).apply { this.displayName("끈 사용") }
    private val breakItemFrame = (Material.ITEM_FRAME * 1).apply { this.displayName("아이템 액자 부수기") }
    private val useNoteBlock = (Material.NOTE_BLOCK * 1).apply { this.displayName("노트블록 사용") }
    private val useJukebox = (Material.JUKEBOX * 1).apply { this.displayName("주크박스 사용") }

    override fun render(inv: Inventory) {
        val setting = landManager.getLandMemberSetting(player.uniqueId)!!

        //First row
        canPVP.apply { this.lore = listOf("상태: ${setting.canPVP}") }
        breakBlock.apply { this.lore = listOf("상태: ${setting.breakBlock}") }
        placeBlock.apply { this.lore = listOf("상태: ${setting.placeBlock}") }
        canHurt.apply { this.lore = listOf("상태: ${setting.canHurt}") }
        pickupExp.apply { this.lore = listOf("상태: ${setting.pickupExp}") }
        pickupItem.apply { this.lore = listOf("상태: ${setting.pickupItem}") }
        dropItem.apply { this.lore = listOf("상태: ${setting.dropItem}") }
        openChest.apply { this.lore = listOf("상태: ${setting.openChest}") }
        eatCake.apply { this.lore = listOf("상태: ${setting.eatCake}") }

        //Second row
        useCircuit.apply { this.lore = listOf("상태: ${setting.useCircuit}") }
        useLever.apply { this.lore = listOf("상태: ${setting.useLever}") }
        useButton.apply { this.lore = listOf("상태: ${setting.useButton}") }
        usePressureSensor.apply { this.lore = listOf("상태: ${setting.usePressureSensor}") }
        useDoor.apply { this.lore = listOf("상태: ${setting.useDoor}") }
        useTrapdoor.apply { this.lore = listOf("상태: ${setting.useTrapdoor}") }
        useFenceDoor.apply { this.lore = listOf("상태: ${setting.useFenceDoor}") }
        useHopper.apply { this.lore = listOf("상태: ${setting.useHopper}") }
        useDispenserAndDropper.apply { this.lore = listOf("상태: ${setting.useDispenserAndDropper}") }

        //Third row
        useCraftTable.apply { this.lore = listOf("상태: ${setting.useCraftTable}") }
        useFurnace.apply { this.lore = listOf("상태: ${setting.useFurnace}") }
        useBed.apply { this.lore = listOf("상태: ${setting.useBed}") }
        useEnchantingTable.apply { this.lore = listOf("상태: ${setting.useEnchantingTable}") }
        useAnvil.apply { this.lore = listOf("상태: ${setting.useAnvil}") }
        useCauldron.apply { this.lore = listOf("상태: ${setting.useCauldron}") }
        useBrewingStand.apply { this.lore = listOf("상태: ${setting.useBrewingStand}") }
        useBeacon.apply { this.lore = listOf("상태: ${setting.useBeacon}") }
        useArmorStand.apply { this.lore = listOf("상태: ${setting.useArmorStand}") }

        //Fourth row
        canSow.apply { this.lore = listOf("상태: ${setting.canSow}") }
        canHarvest.apply { this.lore = listOf("상태: ${setting.canHarvest}") }
        canFeed.apply { this.lore = listOf("상태: ${setting.canFeed}") }
        useBucket.apply { this.lore = listOf("상태: ${setting.useBucket}") }
        useMilk.apply { this.lore = listOf("상태: ${setting.useMilk}") }
        throwEgg.apply { this.lore = listOf("상태: ${setting.throwEgg}") }
        useShears.apply { this.lore = listOf("상태: ${setting.useShears}") }
        useFlintAndSteel.apply { this.lore = listOf("상태: ${setting.useFlintAndSteel}") }
        canRuinFarmland.apply { this.lore = listOf("상태: ${setting.canRuinFarmland}") }

        //Fifth row
        useMinecart.apply { this.lore = listOf("상태: ${setting.useMinecart}") }
        canFishing.apply { this.lore = listOf("상태: ${setting.canFishing}") }
        useBoat.apply { this.lore = listOf("상태: ${setting.useBoat}") }
        canRiding.apply { this.lore = listOf("상태: ${setting.canRiding}") }
        useChestedHorse.apply { this.lore = listOf("상태: ${setting.useChestedHorse}") }
        useLead.apply { this.lore = listOf("상태: ${setting.useLead}") }
        breakItemFrame.apply { this.lore = listOf("상태: ${setting.breakItemFrame}") }
        useNoteBlock.apply { this.lore = listOf("상태: ${setting.useNoteBlock}") }
        useJukebox.apply { this.lore = listOf("상태: ${setting.useJukebox}") }

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
        inv.setItem(24, useFenceDoor)
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
        inv.setItem(38, canFeed)
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
        val setting = landManager.getLandMemberSetting(player.uniqueId)!!
        val inv = event.inventory
        when (event.rawSlot) {
            0, 8 -> player.openSettingGui(rank)//Back button
            //First row
            9 -> {
                setting.canPVP = !setting.canPVP
                canPVP.apply {
                    this.lore = listOf("상태: ${setting.canPVP}")
                    inv.setItem(9, this)
                }
            }
            10 -> {
                setting.breakBlock = !setting.breakBlock
                breakBlock.apply {
                    this.lore = listOf("상태: ${setting.breakBlock}")
                    inv.setItem(10, this)
                }
            }
            11 -> {
                setting.placeBlock = !setting.placeBlock
                placeBlock.apply {
                    this.lore = listOf("상태: ${setting.placeBlock}")
                    inv.setItem(11, this)
                }
            }
            12 -> {
                setting.canHurt = !setting.canHurt
                canHurt.apply {
                    this.lore = listOf("상태: ${setting.canHurt}")
                    inv.setItem(12, this)
                }
            }
            13 -> {
                setting.pickupExp = !setting.pickupExp
                pickupExp.apply {
                    this.lore = listOf("상태: ${setting.pickupExp}")
                    inv.setItem(13, this)
                }
            }
            14 -> {
                setting.pickupItem = !setting.pickupItem
                pickupItem.apply {
                    this.lore = listOf("상태: ${setting.pickupItem}")
                    inv.setItem(14, this)
                }
            }
            15 -> {
                setting.dropItem = !setting.dropItem
                dropItem.apply {
                    this.lore = listOf("상태: ${setting.dropItem}")
                    inv.setItem(15, this)
                }
            }
            16 -> {
                setting.openChest = !setting.openChest
                openChest.apply {
                    this.lore = listOf("상태: ${setting.openChest}")
                    inv.setItem(16, this)
                }
            }
            17 -> {
                setting.eatCake = !setting.eatCake
                eatCake.apply {
                    this.lore = listOf("상태: ${setting.eatCake}")
                    inv.setItem(17, this)
                }
            }
            //Second row
            18 -> {
                setting.useCircuit = !setting.useCircuit
                useCircuit.apply {
                    this.lore = listOf("상태: ${setting.useCircuit}")
                    inv.setItem(18, this)
                }
            }
            19 -> {
                setting.useLever = !setting.useLever
                useLever.apply {
                    this.lore = listOf("상태: ${setting.useLever}")
                    inv.setItem(19, this)
                }
            }
            20 -> {
                setting.useButton = !setting.useButton
                useButton.apply {
                    this.lore = listOf("상태: ${setting.useButton}")
                    inv.setItem(20, this)
                }
            }
            21 -> {
                setting.usePressureSensor = !setting.usePressureSensor
                usePressureSensor.apply {
                    this.lore = listOf("상태: ${setting.usePressureSensor}")
                    inv.setItem(21, this)
                }
            }
            22 -> {
                setting.useDoor = !setting.useDoor
                useDoor.apply {
                    this.lore = listOf("상태: ${setting.useDoor}")
                    inv.setItem(22, this)
                }
            }
            23 -> {
                setting.useTrapdoor = !setting.useTrapdoor
                useTrapdoor.apply {
                    this.lore = listOf("상태: ${setting.useTrapdoor}")
                    inv.setItem(23, this)
                }
            }
            24 -> {
                setting.useFenceDoor = !setting.useFenceDoor
                useFenceDoor.apply {
                    this.lore = listOf("상태: ${setting.useFenceDoor}")
                    inv.setItem(24, this)
                }
            }
            25 -> {
                setting.useHopper = !setting.useHopper
                useHopper.apply {
                    this.lore = listOf("상태: ${setting.useHopper}")
                    inv.setItem(25, this)
                }
            }
            26 -> {
                setting.useDispenserAndDropper = !setting.useDispenserAndDropper
                useDispenserAndDropper.apply {
                    this.lore = listOf("상태: ${setting.useDispenserAndDropper}")
                    inv.setItem(26, this)
                }
            }
            //Third row
            27 -> {
                setting.useCraftTable = !setting.useCraftTable
                useCraftTable.apply {
                    this.lore = listOf("상태: ${setting.useCraftTable}")
                    inv.setItem(27, this)
                }
            }
            28 -> {
                setting.useFurnace = !setting.useFurnace
                useFurnace.apply {
                    this.lore = listOf("상태: ${setting.useFurnace}")
                    inv.setItem(28, this)
                }
            }
            29 -> {
                setting.useBed = !setting.useBed
                useBed.apply {
                    this.lore = listOf("상태: ${setting.useBed}")
                    inv.setItem(29, this)
                }
            }
            30 -> {
                setting.useEnchantingTable = !setting.useEnchantingTable
                useEnchantingTable.apply {
                    this.lore = listOf("상태: ${setting.useEnchantingTable}")
                    inv.setItem(30, this)
                }
            }
            31 -> {
                setting.useAnvil = !setting.useAnvil
                useAnvil.apply {
                    this.lore = listOf("상태: ${setting.useAnvil}")
                    inv.setItem(31, this)
                }
            }
            32 -> {
                setting.useCauldron = !setting.useCauldron
                useCauldron.apply {
                    this.lore = listOf("상태: ${setting.useCauldron}")
                    inv.setItem(32, this)
                }
            }
            33 -> {
                setting.useBrewingStand = !setting.useBrewingStand
                useBrewingStand.apply {
                    this.lore = listOf("상태: ${setting.useBrewingStand}")
                    inv.setItem(33, this)
                }
            }
            34 -> {
                setting.useBeacon = !setting.useBeacon
                useBeacon.apply {
                    this.lore = listOf("상태: ${setting.useBeacon}")
                    inv.setItem(34, this)
                }
            }
            35 -> {
                setting.useArmorStand = !setting.useArmorStand
                useArmorStand.apply {
                    this.lore = listOf("상태: ${setting.useArmorStand}")
                    inv.setItem(35, this)
                }
            }
            //Fourth row
            36 -> {
                setting.canSow = !setting.canSow
                canSow.apply {
                    this.lore = listOf("상태: ${setting.canSow}")
                    inv.setItem(36, this)
                }
            }
            37 -> {
                setting.canHarvest = !setting.canHarvest
                canHarvest.apply {
                    this.lore = listOf("상태: ${setting.canHarvest}")
                    inv.setItem(37, this)
                }
            }
            38 -> {
                setting.canFeed = !setting.canFeed
                canFeed.apply {
                    this.lore = listOf("상태: ${setting.canFeed}")
                    inv.setItem(38, this)
                }
            }
            39 -> {
                setting.useBucket = !setting.useBucket
                useBucket.apply {
                    this.lore = listOf("상태: ${setting.useBucket}")
                    inv.setItem(39, this)
                }
            }
            40 -> {
                setting.useMilk = !setting.useMilk
                useMilk.apply {
                    this.lore = listOf("상태: ${setting.useMilk}")
                    inv.setItem(40, this)
                }
            }
            41 -> {
                setting.throwEgg = !setting.throwEgg
                throwEgg.apply {
                    this.lore = listOf("상태: ${setting.throwEgg}")
                    inv.setItem(41, this)
                }
            }
            42 -> {
                setting.useShears = !setting.useShears
                useShears.apply {
                    this.lore = listOf("상태: ${setting.useShears}")
                    inv.setItem(42, this)
                }
            }
            43 -> {
                setting.useFlintAndSteel = !setting.useFlintAndSteel
                useFlintAndSteel.apply {
                    this.lore = listOf("상태: ${setting.useFlintAndSteel}")
                    inv.setItem(43, this)
                }
            }
            44 -> {
                setting.canRuinFarmland = !setting.canRuinFarmland
                canRuinFarmland.apply {
                    this.lore = listOf("상태: ${setting.canRuinFarmland}")
                    inv.setItem(44, this)
                }
            }
            //Fifth row
            45 -> {
                setting.useMinecart = !setting.useMinecart
                useMinecart.apply {
                    this.lore = listOf("상태: ${setting.useMinecart}")
                    inv.setItem(45, this)
                }
            }
            46 -> {
                setting.canFishing = !setting.canFishing
                canFishing.apply {
                    this.lore = listOf("상태: ${setting.canFishing}")
                    inv.setItem(46, this)
                }
            }
            47 -> {
                setting.useBoat = !setting.useBoat
                useBoat.apply {
                    this.lore = listOf("상태: ${setting.useBoat}")
                    inv.setItem(47, this)
                }
            }
            48 -> {
                setting.canRiding = !setting.canRiding
                canRiding.apply {
                    this.lore = listOf("상태: ${setting.canRiding}")
                    inv.setItem(48, this)
                }
            }
            49 -> {
                setting.useChestedHorse = !setting.useChestedHorse
                useChestedHorse.apply {
                    this.lore = listOf("상태: ${setting.useChestedHorse}")
                    inv.setItem(49, this)
                }
            }
            50 -> {
                setting.useLead = !setting.useLead
                useLead.apply {
                    this.lore = listOf("상태: ${setting.useLead}")
                    inv.setItem(50, this)
                }
            }
            51 -> {
                setting.breakItemFrame = !setting.breakItemFrame
                breakItemFrame.apply {
                    this.lore = listOf("상태: ${setting.breakItemFrame}")
                    inv.setItem(51, this)
                }
            }
            52 -> {
                setting.useNoteBlock = !setting.useNoteBlock
                useNoteBlock.apply {
                    this.lore = listOf("상태: ${setting.useNoteBlock}")
                    inv.setItem(52, this)
                }
            }
            53 -> {
                setting.useJukebox = !setting.useJukebox
                useJukebox.apply {
                    this.lore = listOf("상태: ${setting.useJukebox}")
                    inv.setItem(53, this)
                }
            }
        }
    }

    override fun onDrag(event: InventoryDragEvent) {
        event.isCancelled = true
    }

    override fun onClose(event: InventoryCloseEvent) {
        guiManager.guiMap.remove(event.view)
    }
}

fun Player.openMemberSettingGui(rank: Rank) {
    val inventory = Bukkit.createInventory(null, 54, "멤버 설정")
    val gui = MemberSettingGui(this, rank)
    gui.render(inventory)

    val view = this.openInventory(inventory)!!
    guiManager.guiMap[view] = gui
}