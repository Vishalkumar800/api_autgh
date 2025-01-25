package com.example.dao

import com.example.model.data.User
import com.example.security.connect.AuthTable
import com.example.security.connect.DataBaseFactory
import com.example.security.hashing.HashingService
import org.jetbrains.exposed.sql.*

class UserDataSourceImp(private val hashingService: HashingService) : UserDataSource {
    override suspend fun insertUser(user: User): Boolean {

        return DataBaseFactory.dbQuery {
            val result = AuthTable.insert {
                it[username] = user.userName
                it[password] = user.password // Store the hashed password directly
                it[userId] = user.userId
            }
            println("Insert result: $result")
            result.insertedCount > 0
        }
    }

    override suspend fun getUserByUsername(username: String): User? {
        return DataBaseFactory.dbQuery {
            AuthTable.selectAll().where { AuthTable.username eq username }
                .mapNotNull {
                    rowToUser(it)
                }.singleOrNull()
        }
    }

    private fun rowToUser(row: ResultRow): User {
        return User(
            userId = row[AuthTable.userId],
            userName = row[AuthTable.username],
            password = row[AuthTable.password] // Retrieved hashed password
        )
    }
}
