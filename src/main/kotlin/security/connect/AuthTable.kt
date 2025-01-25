package com.example.security.connect

import org.jetbrains.exposed.sql.Table

object AuthTable:Table("authTable") {
    val username =varchar("username",256).uniqueIndex()
    val password = varchar("password",512)
    val userId = varchar("user_id",256).uniqueIndex()

    override val primaryKey: PrimaryKey = PrimaryKey(userId)

}