package com.example.security.hashing

interface HashingService {

    fun generateSaltedHash(value: String, saltLength: Int = 16): SaltedHash
    fun verify(value: String, saltedHash: SaltedHash): Boolean
}