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
                "INSERT INTO tunalands_landlist VALUES('${chunkInfo.ownerUUID}', '${chunkInfo.ownerName}', ${chunkInfo.xChunk}, ${chunkInfo.zChunk})"
            val prestate = hikari.connection.prepareStatement(query)
            prestate.executeUpdate()
        })
    }

    fun delete(chunkInfo: Lands.ChunkInfo) {
        Bukkit.getScheduler().runTaskAsynchronously(tunaLands, Runnable {
            val query =
                "DELETE FROM tunalands_landlist WHERE `uuid`='${chunkInfo.ownerUUID}' AND `name`='${chunkInfo.ownerName}' AND `x`=${chunkInfo.xChunk} AND `z`=${chunkInfo.zChunk}"
            val prestate = hikari.connection.prepareStatement(query)
            prestate.executeUpdate()
        })
    }

    fun deleteAll(uuid: UUID, name: String) {
        Bukkit.getScheduler().runTaskAsynchronously(tunaLands, Runnable {
            val query =
                "DELETE FROM tunalands_landlist WHERE `uuid`='${uuid}' AND `name`='${name}'"
            val prestate = hikari.connection.prepareStatement(query)
            prestate.executeUpdate()
        })
    }

    fun replaceAll(oldUUID: UUID, oldName: String, newUUID: UUID, newName: String) {
        Bukkit.getScheduler().runTaskAsynchronously(tunaLands, Runnable {
            val query =
                "UPDATE tunalands_landlist " +
                        "SET `uuid`='${newUUID}', `name`='${newName}' " +
                        "WHERE `uuid`='${oldUUID}' AND `name`='${oldName}'"
            val prestate = hikari.connection.prepareStatement(query)
            prestate.executeUpdate()
        })
    }

    fun close() {
        hikari.close()
    }

}