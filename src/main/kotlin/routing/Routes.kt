package com.example.routing

import com.example.dao.UserDataSource
import com.example.model.data.User
import com.example.security.hashing.HashingService
import com.example.security.hashing.SaltedHash
import com.example.security.token.TokenClaim
import com.example.security.token.TokenConfig
import com.example.security.token.TokenService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.signup(
    hashingService: HashingService,
    userDataSource: UserDataSource
) {
    post("signup") {
        try {
            val request = call.receive<AuthRequest>()

            if (request == null) {
                call.respondText("Invalid request format.", status = HttpStatusCode.BadRequest)
                return@post
            }

            val areFieldsBlank = request.username.isBlank() || request.password.isBlank()
            val isPwIsShort = request.password.length < 8

            if (areFieldsBlank || isPwIsShort) {
                println("Fields are blank or password is too short.")
                call.respond(
                    HttpStatusCode.Conflict,
                    "Password is too short or some fields are blank. Please check your input."
                )
                return@post
            }

            val saltedHash = hashingService.generateSaltedHash(request.password, 12)
            println("Generated salted hash: ${saltedHash.hash}")

            val user = User(
                userName = request.username,
                password = saltedHash.hash, // Store the hash in the database
                userId = "user_${System.currentTimeMillis()}"
            )

            val wasAcknowledged = userDataSource.insertUser(user)
            if (!wasAcknowledged) {
                println("User could not be created.")
                call.respond(HttpStatusCode.Conflict, "User could not be created. Please try again.")
            }
            println("User successfully created: $user")

            call.respond(HttpStatusCode.OK, "User successfully created!")

        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "An error occurred: ${e.message}")
            application.log.error("Signup error", e)
        }
    }
}
fun Route.signIn(
    hashingService: HashingService,
    userDataSource: UserDataSource,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    post("signIn") {
        try {
            val request = call.receive<AuthRequest>()

            val storedUser = userDataSource.getUserByUsername(request.username)
            if (storedUser == null) {
                println("User not found.")
                call.respond(HttpStatusCode.Conflict, "Invalid credentials.")
                return@post
            }

            println("Retrieved user: $storedUser")

            val storedSaltedHash = SaltedHash(hash = storedUser.password)
            println("Stored salted hash: ${storedSaltedHash.hash}")

            val isPasswordCorrect = hashingService.verify(request.password, storedSaltedHash)
            println("Password verification result: $isPasswordCorrect")

            if (isPasswordCorrect) {
                // Generate JWT token
                val token = tokenService.generate(
                    config = tokenConfig,
                    TokenClaim("userId", storedUser.userId)
                )
                call.respond(HttpStatusCode.OK, AuthResponse(token = token))
            } else {
                // Password does not match, return error
                call.respond(HttpStatusCode.Conflict, "Invalid credentials.")
            }

        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "An error occurred: ${e.message}")
            application.log.error("Signin error", e)
        }
    }
}



fun Route.authenticate(){
    authenticate {
        get("authenticate") {

            call.respond(HttpStatusCode.OK)
        }
    }
}

fun Route.getSecretInfo(){
    authenticate{
        get("secret") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId",String::class)
            call.respond(HttpStatusCode.OK,"Your userId is $userId")
        }
    }
}