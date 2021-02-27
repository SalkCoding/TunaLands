package com.salkcoding.tunalands.util

import com.salkcoding.tunalands.tunaLands
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitTask
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object TeleportCooltime {

    private val timerMap = ConcurrentHashMap<UUID, CoolTimer>()

    fun addPlayer(player: Player, to: Location, cooldownTick: Long, callback: Runnable?, isAsync: Boolean) {
        player.closeInventory()

        var timer = timerMap[player.uniqueId]
        timer?.stop()
        timer = CoolTimer(player, to, cooldownTick, callback, isAsync)

        timerMap.remove(player.uniqueId)

        timerMap[player.uniqueId] = timer
        timer.task = Bukkit.getScheduler().runTaskTimerAsynchronously(tunaLands, timer, 1, 2)
    }

    internal class CoolTimer(
        private val player: Player,
        private val to: Location,
        private var cooldownTick: Long,
        private val callback: Runnable?,
        private val isAsync: Boolean
    ) : Runnable {
        private val last: Location = player.location
        lateinit var task: BukkitTask

        init {
            if (player.isOnline && !player.isOp) player.addPotionEffect(
                PotionEffect(
                    PotionEffectType.SLOW,
                    (cooldownTick + 10).toInt(),
                    100,
                    false,
                    false,
                    false
                )
            )
        }

        override fun run() {
            if (!player.isOnline || player.isDead) {
                stop()
                return
            }
            if (cooldownTick < 0 || player.isOp) {
                //player.sendMessage("이동중입니다.".infoFormat())
                //player.world.playSound(player.location, Sound.BLOCK_PORTAL_TRAVEL, 0.15f, 1f)
                player.sendTitle("\ue405", "", 30, 10, 30)
                Bukkit.getScheduler().runTaskLaterAsynchronously(tunaLands, Runnable {
                    Bukkit.getScheduler().runTask(tunaLands, Runnable {
                        player.teleportAsync(to, PlayerTeleportEvent.TeleportCause.COMMAND)
                    })
                    if (callback != null) {
                        if (isAsync) callback.run()//Already in async thread
                        else Bukkit.getScheduler().runTask(tunaLands, callback)
                    }
                }, 30)
                stop()
                return
            } else {
                player.sendTitle(
                    "${ChatColor.GOLD}텔레포트 중입니다...",
                    "${ChatColor.GRAY}이동 ${String.format("%.1f", cooldownTick / 20f)}초 전...",
                    0,
                    20,
                    10
                )
                player.world.spawnParticle(Particle.PORTAL, player.location, 5)
            }

            if (last.world.name != player.location.world.name) {
                stop()
                return
            }

            if (last.distance(player.location) > 1) {
                player.sendMessage("텔레포트 중 이동하셔서 텔레포트가 취소됩니다.".warnFormat())
                stop()
                return
            }
            cooldownTick -= 2
        }

        fun stop() {
            task.cancel()
            timerMap.remove(player.uniqueId)
            if (player.isOnline) Bukkit.getScheduler().runTask(tunaLands, Runnable {
                player.removePotionEffect(
                    PotionEffectType.SLOW
                )
            })
        }
    }
}
