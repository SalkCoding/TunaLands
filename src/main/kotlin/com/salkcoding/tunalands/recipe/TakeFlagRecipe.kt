package com.salkcoding.tunalands.recipe

import br.com.devsrsouza.kotlinbukkitapi.extensions.item.displayName
import com.salkcoding.tunalands.tunaLands
import com.salkcoding.tunalands.util.times
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ShapedRecipe

object TakeFlagRecipe : RecipeRegister {

    val takeFlag = (Material.GREEN_BANNER * 1).apply {
        this.displayName("${ChatColor.GREEN}점유 ${ChatColor.WHITE}깃발")
        this.lore = listOf(
            "${ChatColor.WHITE}늘리고 싶은 지역에 설치하여 점유할 수 있는 깃발입니다."
        )
    }

    override fun registerRecipe() {
        val recipe = ShapedRecipe(NamespacedKey(tunaLands, "take_flag"), takeFlag)

        recipe.shape("DDD", "DLD", "DDD")

        recipe.setIngredient('D', Material.DIAMOND)
        recipe.setIngredient('L', Material.LAPIS_LAZULI)

        Bukkit.addRecipe(recipe)
    }
}