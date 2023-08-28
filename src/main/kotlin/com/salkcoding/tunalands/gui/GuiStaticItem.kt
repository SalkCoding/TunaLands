package com.salkcoding.tunalands.gui

import com.salkcoding.tunalands.util.times
import org.bukkit.ChatColor
import org.bukkit.DyeColor
import org.bukkit.Material
import org.bukkit.block.banner.Pattern
import org.bukkit.block.banner.PatternType
import org.bukkit.inventory.meta.BannerMeta

//Back button
val backButton = (Material.FEATHER * 1).apply {
    this.setDisplayName("뒤로가기")
}

//Background decoration
val blackPane = (Material.BLACK_STAINED_GLASS_PANE * 1).apply {
    this.setDisplayName(" ")
}

//Paging button
val nextPageButton = (Material.LIME_STAINED_GLASS_PANE * 1).apply {
    this.setDisplayName("다음 페이지")
}

//Paging button
val previousPageButton = (Material.LIME_STAINED_GLASS_PANE * 1).apply {
    this.setDisplayName("이전 페이지")
}

//Shop static items
val fuelItem = (Material.PAPER * 1).apply {
    this.setDisplayName("${ChatColor.WHITE}연료")
    this.lore = listOf(
        "${ChatColor.WHITE}마을의 규모에 따라 시간적 가치가 변하는 연료이다."
    )
}

val takeProtectFlagItem = (Material.GREEN_BANNER * 1).apply {
    this.setDisplayName("${ChatColor.GREEN}점유 ${ChatColor.WHITE}깃발")
    this.lore = listOf(
        "${ChatColor.WHITE}늘리고 싶은 지역에 설치하여 점유할 수 있는 깃발입니다.",
    )
}

val releaseProtectFlagItem = (Material.GREEN_BANNER * 1).apply {
    val bannerMeta = this.itemMeta as BannerMeta
    bannerMeta.patterns.add(Pattern(DyeColor.RED, PatternType.STRAIGHT_CROSS))
    bannerMeta.setDisplayName("${ChatColor.RED}제거 ${ChatColor.WHITE}깃발")
    bannerMeta.lore = listOf(
        "${ChatColor.WHITE}제거하고 싶은 지역에 설치하여 제거할 수 있는 깃발입니다."
    )
    this.itemMeta = bannerMeta
}

val takeFarmFlagItem = (Material.BROWN_BANNER * 1).apply {
    this.setDisplayName("${ChatColor.replaceHex("#964b00")}농작지 ${ChatColor.WHITE}깃발")
    this.lore = listOf(
        "${ChatColor.WHITE}점유한 지역의 용도를 농작지로 변경합니다."
    )
}

val releaseFarmFlagItem = (Material.BROWN_BANNER * 1).apply {
    val bannerMeta = this.itemMeta as BannerMeta
    bannerMeta.patterns.add(Pattern(DyeColor.RED, PatternType.STRAIGHT_CROSS))
    bannerMeta.setDisplayName("${ChatColor.replaceHex("#964b00")}농작지 ${ChatColor.RED}제거 ${ChatColor.WHITE}깃발")
    bannerMeta.lore = listOf(
        "${ChatColor.WHITE}농작지를 일반 영토로 변경합니다."
    )
    this.itemMeta = bannerMeta
}

//Setting gui static items
val settingButton = (Material.BONE * 1).apply {
    this.setDisplayName("${ChatColor.WHITE}지역 관리")
    this.lore = listOf(
        "${ChatColor.WHITE}지역의 세부 설정을 변경합니다."
    )
}

val shopButton = (Material.HEART_OF_THE_SEA * 1).apply {
    this.setDisplayName("${ChatColor.WHITE}지역 상점")
    this.lore = listOf(
        "${ChatColor.WHITE}지역에 관련된 물품을 구매할 수 있습니다."
    )
}

val userListButton = (Material.WRITABLE_BOOK * 1).apply {
    this.setDisplayName("${ChatColor.WHITE}사용자 목록")
    this.lore = listOf(
        "${ChatColor.WHITE}지역의 사용자 목록을 확인합니다."
    )
}

val banListButton = (Material.CRIMSON_SIGN * 1).apply {
    this.setDisplayName("${ChatColor.WHITE}밴 목록")
    this.lore = listOf(
        "${ChatColor.WHITE}밴 목록을 확인합니다."
    )
}

//TODO 해당 연료 데이터 전달 받으면 이걸 통해 main gui에서 연료 추가 해줄 것
//소규모 마을 전용 연료
val smallTownFuel = (Material.PAPER * 1).apply {
    this.setDisplayName("${ChatColor.WHITE}소규모 마을 전용 연료")
    this.lore = listOf(
        "${ChatColor.WHITE}마을 인원 ~명, 점유한 지역이 ~개",
        "${ChatColor.WHITE}이하일 때만 사용 가능합니다.",
    )
}