package com.example.dao

import com.example.model.data.User

interface UserDataSource {

    suspend fun getUserByUsername(username:String):User?
    suspend fun insertUser(user: User):Boolean

}