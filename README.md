# TunaLands

## Developing environment
* Kotlin 1.4.10 + Gradle

## Testing environment
* [purpurclip-1.16.4-950](https://github.com/pl3xgaming/Purpur)
* [GraalVM CE 20.3.0 (openjdk 11.0.9)](https://www.graalvm.org)
* JVM Memory 512MB ~ 2GB

## Dependencies
* kotlin-stdlib
* [purpurclip-1.16.4-950](https://github.com/pl3xgaming/Purpur)
* [kotlinbukkitapi 0.2.0-SNAPSHOT](http://jenkins.devsrsouza.com.br/job/KotlinBukkitAPI/)

# File format

## Configuration format(YMAL)
```
serverName: "server"

protect:
  coreBlock: DIAMOND_BLOCK #Not ID, Material
  createPrice: 10
  baseMaxExtendCount: 5
  baseLimitExtendPrice: 5

flag:
	takeFlagPrice: 10
  removeFlagPrice: 5

command:
  cooldown: #Tick
    rejoin: 5184000 #3d
    visit: 200 #10s
    spawn: 200 #10s
  price:
    setSpawnPrice: 10

limitWorld:
    - world_nether
    - world_the_end
```

## Save format(Json)
```
{
    "ownerName": "Salk_Coding",
    "ownerUUID": "24c186ac-b905-4b6e-9f96-0ad0744df901",
    "expiredMillisecond": 1609665486838,
    "enable": true,
    "open": false,
    "landList": [
        "1:1",
        "1:2"
    ],
    "landHistory": {
        "visitorCount": 0,
        "createdMillisecond": 1609665486838
    },
    "core": {
        "world": "world",
        "x": 271,
        "y": 75,
        "z": 15
    },
    "memberSpawn": {
        "world": "world",
        "x": 271,
        "y": 75,
        "z": 15
    },
    "visitorSpawn": {
        "world": "world",
        "x": 271,
        "y": 75,
        "z": 15
    },
    "lore": [
        "Salk_Coding의 땅입니다.",
        "ㅎㅇ",
        "ㅂㅇ"
    ],
    "welcomeMessage": [
        "Salk_Coding의 땅입니다.",
        "ㅎㅇ",
        "ㅂㅇ"
    ],
    "memberMap": [{
        "uuid": "24c186ac-b905-4b6e-9f96-0ad0744df901",
        "rank": "OWNER",
        "joined": 1609665486838,
        "lastLogin": 1609665486838
    }],
    "banMap": [{
        "uuid": "532127a2-8d7a-48b1-9719-12ad8032d8a0",
        "banned": 1609665486838
    }],
    "visitorSetting": {
        "canPVP": false,
        "breakBlock": false,
        "placeBlock": false,
        "canHurt": false,
        "pickupExp": false,
        "pickupItem": false,
        "dropItem": false,
        "openChest": false,
        "eatCake": false,
        "useCircuit": false,
        "useLever": false,
        "useButton": false,
        "usePressureSensor": false,
        "useDoor": false,
        "useTrapdoor": false,
        "useFenceGate": false,
        "useHopper": false,
        "useDispenserAndDropper": false,
        "useCraftTable": false,
        "useFurnace": false,
        "useBed": false,
        "useEnchantingTable": false,
        "useAnvil": false,
        "useCauldron": false,
        "useBrewingStand": false,
        "useBeacon": false,
        "useArmorStand": false,
        "canSow": false,
        "canHarvest": false,
        "canBreed": false,
        "useBucket": false,
        "useMilk": false,
        "throwEgg": false,
        "useShears": false,
        "useFlintAndSteel": false,
        "canRuinFarmland": false,
        "useMinecart": false,
        "canFishing": false,
        "useBoat": false,
        "canRiding": false,
        "useChestedHorse": false,
        "useLead": false,
        "breakItemFrame": false,
        "useNoteBlock": false,
        "useJukebox": false
    },
    "partTimeJobSetting": {
        "canPVP": false,
        "breakBlock": false,
        "placeBlock": false,
        "canHurt": false,
        "pickupExp": false,
        "pickupItem": false,
        "dropItem": false,
        "openChest": false,
        "eatCake": false,
        "useCircuit": false,
        "useLever": false,
        "useButton": false,
        "usePressureSensor": false,
        "useDoor": false,
        "useTrapdoor": false,
        "useFenceGate": false,
        "useHopper": false,
        "useDispenserAndDropper": false,
        "useCraftTable": false,
        "useFurnace": false,
        "useBed": false,
        "useEnchantingTable": false,
        "useAnvil": false,
        "useCauldron": false,
        "useBrewingStand": false,
        "useBeacon": false,
        "useArmorStand": false,
        "canSow": false,
        "canHarvest": false,
        "canBreed": false,
        "useBucket": false,
        "useMilk": false,
        "throwEgg": false,
        "useShears": false,
        "useFlintAndSteel": false,
        "canRuinFarmland": false,
        "useMinecart": false,
        "canFishing": false,
        "useBoat": false,
        "canRiding": false,
        "useChestedHorse": false,
        "useLead": false,
        "breakItemFrame": false,
        "useNoteBlock": false,
        "useJukebox": false
    },
    "memberSetting": {
        "canPVP": false,
        "breakBlock": false,
        "placeBlock": false,
        "canHurt": false,
        "pickupExp": false,
        "pickupItem": false,
        "dropItem": false,
        "openChest": false,
        "eatCake": false,
        "useCircuit": false,
        "useLever": false,
        "useButton": false,
        "usePressureSensor": false,
        "useDoor": false,
        "useTrapdoor": false,
        "useFenceGate": false,
        "useHopper": false,
        "useDispenserAndDropper": false,
        "useCraftTable": false,
        "useFurnace": false,
        "useBed": false,
        "useEnchantingTable": false,
        "useAnvil": false,
        "useCauldron": false,
        "useBrewingStand": false,
        "useBeacon": false,
        "useArmorStand": false,
        "canSow": false,
        "canHarvest": false,
        "canBreed": false,
        "useBucket": false,
        "useMilk": false,
        "throwEgg": false,
        "useShears": false,
        "useFlintAndSteel": false,
        "canRuinFarmland": false,
        "useMinecart": false,
        "canFishing": false,
        "useBoat": false,
        "canRiding": false,
        "useChestedHorse": false,
        "useLead": false,
        "breakItemFrame": false,
        "useNoteBlock": false,
        "useJukebox": false
    },
    "delegatorSetting": {
        "canSetVisitorSetting": false,
        "canSetPartTimeJobSetting": false,
        "canSetMemberSetting": false,
        "canSetSpawn": false,
        "canBan": false,
        "canSetRegionSetting": false
    }
}
```

# [More informations](https://www.notion.so/TunaLands-f59f1a4d81284124b6af32ff5aa6fc2a)
