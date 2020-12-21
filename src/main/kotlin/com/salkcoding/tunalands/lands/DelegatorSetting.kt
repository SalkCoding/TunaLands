package com.salkcoding.tunalands.lands

data class DelegatorSetting(
    var setSpawnVisitor: Boolean = false,
    var setPartTimeJobSetting: Boolean = false,
    var setMemberSetting: Boolean = false,
    var setVisitorSetting: Boolean = false,
    var setRegionSetting: Boolean = false,
    var setVisitorBan: Boolean = false,
    var setRegionSpawn: Boolean = false
)