package com.salkcoding.tunalands.util


import org.bukkit.Material
import org.bukkit.inventory.ItemStack

operator fun Material.times(i: Int): ItemStack {
    return ItemStack(this, i)
}

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