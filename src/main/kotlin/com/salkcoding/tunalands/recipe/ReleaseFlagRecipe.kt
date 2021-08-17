package com.salkcoding.tunalands.recipe


import com.salkcoding.tunalands.tunaLands
import com.salkcoding.tunalands.util.times
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ShapedRecipe

object ReleaseFlagRecipe : RecipeRegister {

    private const val key = "release_flag"

    val releaseFlag = (Material.RED_BANNER * 1).apply {
        this.setDisplayName("${ChatColor.RED}제거 ${ChatColor.WHITE}깃발")
        this.lore = listOf(
            "${ChatColor.WHITE}제거하고 싶은 지역에 설치하여 제거할 수 있는 깃발입니다."
        )
    }

    override fun registerRecipe() {
        val recipe = ShapedRecipe(NamespacedKey(tunaLands, key), releaseFlag)

        recipe.shape("RRR", "RTR", "RRR")

        recipe.setIngredient('R', Material.REDSTONE)
        recipe.setIngredient('T', Material.TNT)

        Bukkit.addRecipe(recipe)
    }

    override fun unregisterRecipe() {
        tunaLands.server.removeRecipe(NamespacedKey(tunaLands, key))
    }
}