package com.salkcoding.tunalands.lands

data class LandSetting(
    //First row
    var canPVP: Boolean = false,
    var breakBlock: Boolean = false,
    var placeBlock: Boolean = false,
    var canHurt: Boolean = false,
    var pickupExp: Boolean = false,
    var pickupItem: Boolean = false,
    var dropItem: Boolean = false,
    var openChest: Boolean = false,
    var eatCake: Boolean = false,

    //Second row
    var useCircuit: Boolean = false,
    var useLever: Boolean = false,
    var useButton: Boolean = false,
    var usePressureSensor: Boolean = false,
    var useDoor: Boolean = false,
    var useTrapdoor: Boolean = false,
    var useFenceDoor: Boolean = false,
    var useHopper: Boolean = false,
    var useDispenserAndDropper: Boolean = false,

    //Third row
    var useCraftTable: Boolean = false,
    var useFurnace: Boolean = false,
    var useBed: Boolean = false,
    var useEnchantingTable: Boolean = false,
    var useAnvil: Boolean = false,
    var useCauldron: Boolean = false,
    var useBrewingStand: Boolean = false,
    var useBeacon: Boolean = false,
    var useArmorStand: Boolean = false,

    //Fourth row
    var canSow: Boolean = false,
    var canHarvest: Boolean = false,
    var canFeed: Boolean = false,
    var useBucket: Boolean = false,
    var useMilk: Boolean = false,
    var throwEgg: Boolean = false,
    var useShears: Boolean = false,
    var useFlintAndSteel: Boolean = false,
    var canRuinFarmland: Boolean = false,

    //Fifth row
    var useMinecart: Boolean = false,
    var canFishing: Boolean = false,
    var useBoat: Boolean = false,
    var canRiding: Boolean = false,
    var useChestedHorse: Boolean = false,
    var useLead: Boolean = false,
    var breakItemFrame: Boolean = false,
    var useNoteBlock: Boolean = false,
    var useJukebox: Boolean = false
)