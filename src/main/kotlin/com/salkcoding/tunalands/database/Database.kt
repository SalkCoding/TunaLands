package com.salkcoding.tunalands.database

import com.salkcoding.tunalands.configuration
import com.salkcoding.tunalands.lands.Lands
import com.salkcoding.tunalands.tunaLands
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.bukkit.Bukkit
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
        val prestate = hikari.connection.prepareStatement(query)
        prestate.executeUpdate()
    }

    fun insert(chunkInfo: Lands.ChunkInfo) {
        Bukkit.getScheduler().runTaskAsynchronously(tunaLands, Runnable {
            val query =
                "INSERT INTO tunalands_landlist VALUES(?, ?, ?, ?)"
            val prestate = hikari.connection.prepareStatement(query)
            prestate.setString(1, chunkInfo.ownerUUID.toString())
            prestate.setString(2, chunkInfo.ownerName)
            prestate.setInt(3, chunkInfo.xChunk)
            prestate.setInt(4, chunkInfo.zChunk)
            prestate.executeUpdate()
        })
    }

    fun delete(chunkInfo: Lands.ChunkInfo) {
        Bukkit.getScheduler().runTaskAsynchronously(tunaLands, Runnable {
            val query =
                "DELETE FROM tunalands_landlist WHERE `uuid`=? AND `name`=? AND `x`=? AND `z`=?"
            val prestate = hikari.connection.prepareStatement(query)
            prestate.setString(1, chunkInfo.ownerUUID.toString())
            prestate.setString(2, chunkInfo.ownerName)
            prestate.setInt(3, chunkInfo.xChunk)
            prestate.setInt(4, chunkInfo.zChunk)
            prestate.executeUpdate()
        })
    }

    fun deleteAll(uuid: UUID, name: String) {
        Bukkit.getScheduler().runTaskAsynchronously(tunaLands, Runnable {
            val query =
                "DELETE FROM tunalands_landlist WHERE `uuid`=? AND `name`=?"
            val prestate = hikari.connection.prepareStatement(query)
            prestate.setString(1, uuid.toString())
            prestate.setString(2, name)
            prestate.executeUpdate()
        })
    }

    fun replaceAll(oldUUID: UUID, oldName: String, newUUID: UUID, newName: String) {
        Bukkit.getScheduler().runTaskAsynchronously(tunaLands, Runnable {
            val query =
                "UPDATE tunalands_landlist " +
                        "SET `uuid`=?, `name`=? " +
                        "WHERE `uuid`=? AND name`=?"
            val prestate = hikari.connection.prepareStatement(query)
            prestate.setString(1, newUUID.toString())
            prestate.setString(2, newName)
            prestate.setString(3, oldUUID.toString())
            prestate.setString(4, oldName)
            prestate.executeUpdate()
        })
    }

    fun close() {
        hikari.close()
    }

}