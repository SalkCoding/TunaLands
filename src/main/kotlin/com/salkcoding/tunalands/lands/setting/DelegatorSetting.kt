package com.salkcoding.tunalands.lands.setting

data class DelegatorSetting(
    var canSetVisitorSetting: Boolean = false,
    var canSetPartTimeJobSetting: Boolean = false,
    var canSetMemberSetting: Boolean = false,
    var canSetSpawn: Boolean = false,
    var canBan: Boolean = false,
    //For update
    var canSetRegionSetting: Boolean = false
)