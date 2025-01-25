package com.example

import com.example.dao.UserDataSourceImp
import com.example.security.connect.DataBaseFactory
import com.example.security.hashing.SHA256HashingService
import com.example.security.token.JwtTokenService
import com.example.security.token.TokenConfig
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {

    DataBaseFactory.init()

    val tokenService = JwtTokenService()
    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        expiresIn = 365L * 1000L * 60L * 60L * 24L,
        secret = "adshjdshjsd"

    )

    val hashingService = SHA256HashingService()
    val userDataSource = UserDataSourceImp(hashingService)


    configureSecurity(tokenConfig)
    configureSerialization()
    configureRouting(
        hashingService,
        userDataSource,
        tokenService = tokenService,
        tokenConfig = tokenConfig
    )

}
