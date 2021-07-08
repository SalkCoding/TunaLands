# TunaLands

## Devloping environment
* IntelliJ IDEA 2021.1.2 (Ultimate Edition)
* Runtime version: 11.0.9.1+11-b1145.63 amd64
* Kotlin 1.5.20
* Gradle 7.0

## Testing environment
* Windows 10 Home
* [purpurclip-1.16.4-950](https://github.com/pl3xgaming/Purpur)
* [GraalVM CE 20.3.0 (openjdk 11.0.9)](https://www.graalvm.org)
* JVM Memory 512MB ~ 2GB (No GUI)

## Dependencies
* kotlin-stdlib
* [purpurclip-1.16.5-latest](https://github.com/pl3xgaming/Purpur)
* [kotlinbukkitapi 0.2.0-SNAPSHOT](https://github.com/DevSrSouza/KotlinBukkitAPI)
* [Vault 1.7.3](https://github.com/MilkBowl/Vault)
* [HikariCP 3.4.5](https://github.com/brettwooldridge/HikariCP)
* BukkitLinkedAPI
* Metamorphosis

# Data format

## Database

```
CREATE TABLE `tunalands_landlist` (
	`uuid` VARCHAR(36) NOT NULL COLLATE 'utf8_general_ci',
	`name` VARCHAR(16) NOT NULL COLLATE 'utf8_general_ci',
	`x` INT(10) NOT NULL,
	`z` INT(10) NOT NULL
)
COMMENT='Data set of Chunk coordinate x, y.'
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;
```

## Configuration format(YMAL)
```
serverName: "lobby"

database:
  name: 'test'
  ip: 'localhost'
  port: 3306
  username: 'root'
  password: 'test'
  encoding: 'utf-8'

protect:
  coreBlock: "DIAMOND_BLOCK" #Not ID, But Material
  createPrice: 10
  baseMaxExtendCount: 5
  baseLimitExtendPrice: 5

flag:
  takeFlagPrice: 10
  releaseFlagPrice: 5

fuel:
  m30: 30
  h1: 50
  h6: 290
  h12: 570
  h24: 1100

command:
  cooldown: #Tick
    rejoin: 5184000 #3d
    visit: 100 #5s
    spawn: 100 #5s
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
