package com.example.routing

import kotlinx.serialization.Serializable


@Serializable
data class AuthRequest(
    val username:String,
    val password:String
)


@Serializable
data class AuthResponse(
    val token: String
)