package com.salkcoding.tunalands.util

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

operator fun Material.times(i: Int): ItemStack {
    return ItemStack(this, i)
}
/*
fun ItemStack.setDisplayName(name: String) {
    val meta = this.itemMeta
    meta.setDisplayName(name)
    this.itemMeta = meta
}*/