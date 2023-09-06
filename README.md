# TunaLands
## Developing environment
* IntelliJ IDEA 2023.2.1
* Runtime version: 17.0.8+7-b1000.8 x86_64
* Kotlin 1.8.20
* Gradle 7.3.2

## Testing environment
* Windows 10 Home
* [Purpur-1.20.1-R0.1](https://github.com/pl3xgaming/Purpur)
* [openJDK 17](https://jdk.java.net/17/)
* [MockK 1.13.5](https://github.com/mockk/mockk)
* [MockBukkit 1.19-SNAPSHOT](https://github.com/MockBukkit/MockBukkit)
* JVM Memory 512MB ~ 2GB (No GUI)

## Dependencies
* kotlin-stdlib
* [adventure-text-minimessage.4.12.0](https://docs.adventure.kyori.net/minimessage) (For Purpur)
* [Purpur-1.20.1-R0.1-SNAPSHOT](https://github.com/pl3xgaming/Purpur)
* [Vault 1.7.3](https://github.com/MilkBowl/Vault)
* BukkitLinkedAPI
* Metamorphosis

# Data format
## Configuration format(YMAL)
```
serverName: "lobby"

#All time units are in 'tick' units.
maxMemberLimit: 10
maxAlbaLimit: 10

protect:
  #Material type https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html
  coreBlock: "DIAMOND_BLOCK"
  replaceCoreBlockPrice: 50
  createPrice: 10
  limitOccupied:
    - numOfMembers: 1
      chunk: 10

    - numOfMembers: 3
      chunk: 30

    - numOfMembers: 5
      chunk: 50

    - numOfMembers: 7
      chunk: 70

farm:
  limitOccupied: 10

flag:
  price:
    takeProtectFlagPrice: 10
    releaseProtectFlagPrice: 5
    activePrice:
      - chunk: 0
        price: 0

      - chunk: 1
        price: 10

      - chunk: 5
        price: 15

      - chunk: 10
        price: 30

    takeFarmFlagPrice: 10
    releaseFarmFlagPrice: 5

# Time unit: seconds 86400 = 1 day
fuel:
  defaultFuel: 259200
  price: 10.0
  fuelAddAmount:
    - numOfMembers: 1
      addAmount: 86400

    - numOfMembers: 2
      addAmount: 75000

    - numOfMembers: 5
      addAmount: 55000

    - numOfMembers: 7
      addAmount: 43200

recommend:
  reset: 1728000 #1d
  cooldown: 1728000 #1d

command:
  cooldown:
    rejoin: 5184000 #3d
    # Teleport effect cooldown
    visit: 100 #5s
    spawn: 100 #5s
  price:
    setSpawnPrice: 10
    renamePrice: 10

limitWorld:
  - world_nether
  - world_the_end

ignoreWorld:
  - spawn
```

## Save format(Json)
```
{
    "ownerName": "Salk_Coding",
    "ownerUUID": "24c186ac-b905-4b6e-9f96-0ad0744df901",
    "fuelLeft": 86400,
    "enable": true,
    "open": false,
    "recommend": 0,
    "landsName": "Salk_Coding의 땅"
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

# [More information](https://www.notion.so/TunaLands-f59f1a4d81284124b6af32ff5aa6fc2a)
