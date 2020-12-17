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