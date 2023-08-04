package com.salkcoding.tunalands.listener.land.protect

import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.LandType
import com.salkcoding.tunalands.util.errorFormat
import com.salkcoding.tunalands.util.sendErrorTipMessage
import com.salkcoding.tunalands.util.toQuery
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDropItemEvent

class BlockDropItemListener : Listener {

    @EventHandler(priority = EventPriority.HIGH)
    fun onHarvestPrevent(event: BlockDropItemEvent) {
        if (event.isCancelled) return
        if (event.player.isOp) return

        val player = event.player
        val lands = landManager.getLandsWithChunk(player.chunk) ?: return
        if (!lands.enable) {
            player.sendMessage("땅을 다시 활성화 해야합니다!".errorFormat())
            event.isCancelled = true
            return
        }

        val type = lands.landMap[player.chunk.toQuery()] ?: return
        if (type == LandType.NORMAL) {
            val items = event.items
            var isRemoved = false
            items.removeAll {
                //Have a "s" thing such as BEETROOTS, CARROTS is correct material type
                when (it.itemStack.type) {
                    Material.WHEAT, Material.WHEAT_SEEDS,
                    Material.BEETROOTS, Material.BEETROOT_SEEDS,
                    Material.CARROTS,
                    Material.POTATOES,
                    Material.NETHER_WART,
                    Material.PUMPKIN, Material.PUMPKIN_SEEDS,
                    Material.MELON_SLICE, Material.MELON_SEEDS,
                    Material.COCOA_BEANS,
                    Material.CACTUS,
                    Material.SUGAR_CANE,
                    Material.BROWN_MUSHROOM, Material.BROWN_MUSHROOM_BLOCK,
                    Material.RED_MUSHROOM, Material.RED_MUSHROOM_BLOCK,
                    Material.CHORUS_FRUIT,
                    Material.SWEET_BERRIES,
                    Material.KELP,
                    Material.SEAGRASS,
                    Material.GLOW_BERRIES,
                    Material.GLOW_LICHEN,
                    -> {
                        isRemoved = true
                        true
                    }
                    //Ignored
                    else -> false
                }
            }
            if (isRemoved)
                player.sendErrorTipMessage("${ChatColor.RED}일반 보호 구역에서 수확한 작물은 아이템이 드랍되지 않습니다.")
        }
    }
}