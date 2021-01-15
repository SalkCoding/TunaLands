package com.salkcoding.tunalands.display

import com.gmail.filoghost.holographicdisplays.api.Hologram
import org.bukkit.scheduler.BukkitTask

data class Display(
    val hologram: Hologram,
    val task: BukkitTask
)