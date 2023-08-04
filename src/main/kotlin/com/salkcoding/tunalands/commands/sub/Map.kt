package com.salkcoding.tunalands.commands.sub

import com.salkcoding.tunalands.landManager
import com.salkcoding.tunalands.lands.LandType
import com.salkcoding.tunalands.lands.Lands
import com.salkcoding.tunalands.lands.Rank
import com.salkcoding.tunalands.util.errorFormat
import com.salkcoding.tunalands.util.splitQuery
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import kotlin.math.abs

class Map : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (label == "map" && args.isEmpty()) {
            val player = sender as? Player
            if (player != null) {
                val uuid = player.uniqueId
                val lands = landManager.getPlayerLands(uuid, Rank.OWNER, Rank.DELEGATOR, Rank.MEMBER)
                if (lands != null) {
                    val data = lands.memberMap[uuid]!!

                    if (data.rank != Rank.OWNER && data.rank != Rank.DELEGATOR && data.rank != Rank.MEMBER) {
                        player.sendMessage("해당 명령어는 땅의 소유자, 관리 대리인, 멤버만 사용가능합니다.".errorFormat())
                        return true
                    }

                    val width = 25
                    val height = 9

                    val halfWidth = width / 2
                    val halfHeight = height / 2

                    val centerX = player.location.chunk.x
                    val centerZ = player.location.chunk.z

                    val owned = lands.landMap
                        .map { it.key.splitQuery() }
                        .filter { abs(it.first - centerX) <= width && abs(it.second - centerZ) <= height }
                        .toHashSet()

                    val messages = mutableListOf<Component>()
                    val margin = Component.text("   ")
                    val compassWidth = 18


                    for (offsetZ in -halfHeight..halfHeight) {
                        var row = Component.text("")
                        var rowWith = 0
                        val dir = getDirectionFromPlayerYaw(player.location.yaw)

                        // Get Compass Text
                        var compass = when (offsetZ) {
                            -halfHeight + 2 -> {
                                val NW = colorStringIfEquals(dir, "NW")
                                val N = colorStringIfEquals(dir, "N")
                                val NE = colorStringIfEquals(dir, "NE")
                                rowWith = 14
                                margin.append(NW).append(margin).append(N).append(margin).append(NE)
                            }

                            -halfHeight + 3 -> {

                                when (dir) {
                                    "NW" -> {
                                        rowWith = 9
                                        margin.append(margin)
                                            .append(Component.text("↖  ", NamedTextColor.RED))
                                    }

                                    "N" -> {
                                        rowWith = 9
                                        margin.append(margin)
                                            .append(Component.text("  ⬆", NamedTextColor.RED))
                                    }

                                    "NE" -> {
                                        rowWith = 11
                                        margin.append(margin)
                                            .append(Component.text("    ↗", NamedTextColor.RED))
                                    }

                                    else -> {
                                        rowWith = 0
                                        Component.text("")
                                    }
                                }
                            }

                            -halfHeight + 4 -> {
                                val W = colorStringIfEquals(dir, "W")
                                val C = Component.text(" +", NamedTextColor.RED)
                                val E = colorStringIfEquals(dir, " E")

                                when (dir) {
                                    "W" -> {
                                        rowWith = 14
                                        margin.append(W).append(Component.text(" ⬅", NamedTextColor.RED)).append(C)
                                            .append(margin).append(E)
                                    }

                                    "E" -> {
                                        rowWith = 14
                                        margin.append(W).append(margin).append(C)
                                            .append(Component.text(" ➡", NamedTextColor.RED)).append(E)
                                    }

                                    else -> {
                                        rowWith = 14
                                        margin.append(W).append(margin).append(C).append(margin).append(E)
                                    }
                                }
                            }

                            -halfHeight + 5 -> {
                                when (dir) {
                                    "SW" -> {
                                        rowWith = 9
                                        margin.append(margin).append(Component.text("↙  ", NamedTextColor.RED))
                                    }

                                    "S" -> {
                                        rowWith = 9
                                        margin.append(margin)
                                            .append(Component.text("  ⬇", NamedTextColor.RED))
                                    }

                                    "SE" -> {
                                        rowWith = 11
                                        margin.append(margin)
                                            .append(Component.text("    ↘", NamedTextColor.RED))
                                    }

                                    else -> {
                                        rowWith = 0
                                        Component.text("")
                                    }
                                }
                            }

                            -halfHeight + 6 -> {
                                val SW = colorStringIfEquals(dir, "SW")
                                val S = colorStringIfEquals(dir, "S")
                                val SE = colorStringIfEquals(dir, "SE")

                                rowWith = 14
                                margin.append(SW).append(margin).append(S).append(margin).append(SE)
                            }

                            else -> {
                                rowWith = 0
                                Component.empty()
                            }
                        }

                        // Add padding
                        compass = compass.append(
                            Component.text(
                                getStringSpace(
                                    compassWidth -
                                            //2,4,6번째 줄은 2칸 앞으로 당겨서 출력
                                            (rowWith + when (offsetZ) {
                                                -halfHeight + 2,
                                                -halfHeight + 4,
                                                -halfHeight + 6 -> 2

                                                else -> 0
                                            })
                                )
                            )
                        )

                        // Add Compass Text to row
                        row = row.append(compass)

                        // Get Chunk Map
                        for (offsetX in -halfWidth..halfWidth) {
                            val x = centerX + offsetX
                            val z = centerZ + offsetZ

                            val landAtXZ = landManager.getLandsWithChunkQuery(player.location.world.name, "$x:$z")

                            var icon: String
                            var color: NamedTextColor

                            if (lands.upCoreLocation.chunk.x == x && lands.upCoreLocation.chunk.z == z) {
                                icon = "+"
                                color = NamedTextColor.AQUA
                            } else if (owned.contains(Pair(x, z))) {
                                icon = "+"
                                val type = lands.landMap["$x:$z"]!!
                                color = when (type) {
                                    LandType.NORMAL -> NamedTextColor.GREEN
                                    LandType.FARM -> NamedTextColor.GOLD
                                }
                            } else if (landAtXZ != null && landAtXZ.ownerUUID != lands.ownerUUID) {
                                icon = "+"
                                color = NamedTextColor.YELLOW
                            } else {
                                icon = "-"
                                color = NamedTextColor.GRAY
                            }

                            if (x == player.location.chunk.x && z == player.location.chunk.z) {
                                icon = "H"
                            }
                            row = row.append(Component.text(icon, color))
                        }

                        val legend = when (offsetZ) {
                            -halfHeight + 2 -> Component.text("H", NamedTextColor.WHITE)
                                .append(Component.text(" : 현재 위치", NamedTextColor.WHITE))

                            -halfHeight + 3 -> Component.text("#", NamedTextColor.AQUA)
                                .append(Component.text(" : 코어", NamedTextColor.WHITE))

                            -halfHeight + 4 -> Component.text("#", NamedTextColor.GREEN)
                                .append(Component.text(" : 일반 땅", NamedTextColor.WHITE))

                            -halfHeight + 5 -> Component.text("#", NamedTextColor.GOLD)
                                .append(Component.text(" : 농지", NamedTextColor.WHITE))

                            -halfHeight + 6 -> Component.text("#", NamedTextColor.YELLOW)
                                .append(Component.text(" : 타지역", NamedTextColor.WHITE))

                            -halfHeight + 7 -> Component.text("#", NamedTextColor.GRAY)
                                .append(Component.text(" : 미점유", NamedTextColor.WHITE))

                            else -> Component.empty()
                        }

                        row = row.append(margin).append(legend)

                        messages.add(row)
                    }

                    val title = "현재 위치 중심의 지역 지도"
                    player.sendMessage("${getStringSpace((width + title.length) / 2)}$title")
                    messages.forEach {
                        player.sendMessage(it)
                    }
                    player.sendMessage("")

                } else player.sendMessage("해당 명령어는 땅의 소유자, 관리 대리인, 멤버만 사용가능합니다.".errorFormat())
            } else sender.sendMessage("콘솔에서는 사용할 수 없는 명령어입니다.".errorFormat())
            return true
        }
        return false
    }


    private fun colorStringIfEquals(a: String, b: String): Component {
        return if (a == b) {
            Component.text(a, NamedTextColor.RED)
        } else {
            Component.text(b, NamedTextColor.WHITE)
        }
    }

    private fun getDirectionFromPlayerYaw(yaw: Float): String {
        val degree = (yaw + 360) % 360
        return if (degree <= 22.5) {
            "S"
        } else if (degree <= 67.5) {
            "SW"
        } else if (degree <= 112.5) {
            "W"
        } else if (degree <= 157.5) {
            "NW"
        } else if (degree <= 202.5) {
            "N"
        } else if (degree <= 247.5) {
            "NE"
        } else if (degree <= 292.5) {
            "E"
        } else if (degree <= 337.5) {
            "SE"
        } else {
            "S"
        }
    }

    private fun getStringSpace(length: Int): String {
        var res = ""
        for (i in 0 until length) {
            res += " "
        }
        return res
    }
}