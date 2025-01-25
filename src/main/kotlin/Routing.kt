package com.example

import com.example.dao.UserDataSource
import com.example.routing.authenticate
import com.example.routing.getSecretInfo
import com.example.routing.signIn
import com.example.routing.signup
import com.example.security.hashing.HashingService
import com.example.security.token.TokenConfig
import com.example.security.token.TokenService
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting(hashingService: HashingService,
                                 userDataSource: UserDataSource,
                                 tokenService: TokenService,
                                 tokenConfig: TokenConfig) {
    routing {

        signup(
            hashingService,
            userDataSource
        )

        signIn(
            hashingService,
            userDataSource,
            tokenService =tokenService,
            tokenConfig = tokenConfig
        )

        authenticate()
        getSecretInfo()
    }
}
