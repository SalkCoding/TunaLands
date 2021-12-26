package com.salkcoding.tunalands.database

import com.salkcoding.tunalands.configuration
import com.salkcoding.tunalands.lands.Lands
import com.salkcoding.tunalands.tunaLands
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.bukkit.Bukkit
import java.sql.SQLException
import java.util.*

class Database {

    private val hikari: HikariDataSource

    init {
        val hikariConfig = HikariConfig()
        hikariConfig.driverClassName = "com.mysql.jdbc.Driver"
        hikariConfig.jdbcUrl =
            "jdbc:mysql://${configuration.dataBase.ip}:${configuration.dataBase.port}/${configuration.dataBase.name}?useUnicode=yes&characterEncoding=${configuration.dataBase.encoding}"
        hikariConfig.username = configuration.dataBase.username
        hikariConfig.password = configuration.dataBase.password

        hikari = HikariDataSource(hikariConfig)

        //Create table
        val query = "CREATE TABLE IF NOT EXISTS tunalands_landlist (" +
                "`uuid` VARCHAR(36) NOT NULL," +
                "`name` VARCHAR(16) NOT NULL," +
                "`x` INT(10) NOT NULL," +
                "`z` INT(10) NOT NULL" +
                ")" +
                "COMMENT='Data set of Chunk coordinate x, z.'" +
                "COLLATE='utf8_general_ci'" +
                "ENGINE=InnoDB" +
                ";"
        val connection = hikari.connection
        try {
            val prestate = connection.prepareStatement(query)
            try {
                prestate.executeUpdate()
            } catch (e: SQLException) {
                e.printStackTrace()
            } finally {
                prestate.close()
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            connection.close()
        }
    }

    fun insert(chunkInfo: Lands.ChunkInfo) {
        Bukkit.getScheduler().runTaskAsynchronously(tunaLands, Runnable {
            val connection = hikari.connection
            try {
                val prestate = connection.prepareStatement("INSERT INTO tunalands_landlist VALUES(?, ?, ?, ?)")
                try {
                    prestate.setString(1, chunkInfo.ownerUUID.toString())
                    prestate.setString(2, chunkInfo.ownerName)
                    prestate.setInt(3, chunkInfo.xChunk)
                    prestate.setInt(4, chunkInfo.zChunk)
                    prestate.executeUpdate()
                } catch (e: SQLException) {
                    e.printStackTrace()
                } finally {
                    prestate.close()
                }
            } catch (e: SQLException) {
                e.printStackTrace()
            } finally {
                connection.close()
            }
        })
    }

    fun delete(chunkInfo: Lands.ChunkInfo) {
        Bukkit.getScheduler().runTaskAsynchronously(tunaLands, Runnable {
            val connection = hikari.connection
            try {
                val prestate =
                    connection.prepareStatement("DELETE FROM tunalands_landlist WHERE `uuid`=? AND `name`=? AND `x`=? AND `z`=?")
                try {
                    prestate.setString(1, chunkInfo.ownerUUID.toString())
                    prestate.setString(2, chunkInfo.ownerName)
                    prestate.setInt(3, chunkInfo.xChunk)
                    prestate.setInt(4, chunkInfo.zChunk)
                    prestate.executeUpdate()
                } catch (e: SQLException) {
                    e.printStackTrace()
                } finally {
                    prestate.close()
                }
            } catch (e: SQLException) {
                e.printStackTrace()
            } finally {
                connection.close()
            }
        })
    }

    fun deleteAll(uuid: UUID, name: String) {
        Bukkit.getScheduler().runTaskAsynchronously(tunaLands, Runnable {
            val connection = hikari.connection
            try {
                val prestate = connection.prepareStatement("DELETE FROM tunalands_landlist WHERE `uuid`=? AND `name`=?")
                try {
                    prestate.setString(1, uuid.toString())
                    prestate.setString(2, name)
                    prestate.executeUpdate()
                } catch (e: SQLException) {
                    e.printStackTrace()
                } finally {
                    prestate.close()
                }
            } catch (e: SQLException) {
                e.printStackTrace()
            } finally {
                connection.close()
            }
        })
    }

    fun replaceAll(oldUUID: UUID, oldName: String, newUUID: UUID, newName: String) {
        Bukkit.getScheduler().runTaskAsynchronously(tunaLands, Runnable {
            val connection = hikari.connection
            try {
                val prestate =
                    connection.prepareStatement("UPDATE tunalands_landlist SET `uuid`=?, `name`=? WHERE `uuid`=? AND name`=?")
                try {
                    prestate.setString(1, newUUID.toString())
                    prestate.setString(2, newName)
                    prestate.setString(3, oldUUID.toString())
                    prestate.setString(4, oldName)
                    prestate.executeUpdate()
                } catch (e: SQLException) {
                    e.printStackTrace()
                } finally {
                    prestate.close()
                }
            } catch (e: SQLException) {
                e.printStackTrace()
            } finally {
                connection.close()
            }
        })
    }

    fun dispose() {
        hikari.close()
    }

}