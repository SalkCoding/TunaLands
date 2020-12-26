package com.salkcoding.tunalands.util

import br.com.devsrsouza.kotlinbukkitapi.extensions.item.displayName
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

operator fun Material.times(i: Int): ItemStack {
    return ItemStack(this, i)
}

//Back button
val backButton = (Material.FEATHER * 1).apply {
    this.displayName("뒤로가기")
}

//Background decoration
val blackPane = (Material.BLACK_STAINED_GLASS_PANE * 1).apply {
    this.displayName(" ")
}

//Paging button
val nextPageButton = (Material.LIME_STAINED_GLASS_PANE * 1).apply {
    this.displayName("다음 페이지")
}

//Paging button
val previousPageButton = (Material.LIME_STAINED_GLASS_PANE * 1).apply {
    this.displayName("이전 페이지")
}