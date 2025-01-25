package com.example.security.hashing

import at.favre.lib.crypto.bcrypt.BCrypt

class SHA256HashingService : HashingService {
    override fun generateSaltedHash(value: String, saltLength: Int): SaltedHash {
        val hash = BCrypt.withDefaults().hashToString(saltLength, value.toCharArray())
        return SaltedHash(
            hash = hash
        )
    }

    override fun verify(value: String, saltedHash: SaltedHash): Boolean {
        val result = BCrypt.verifyer().verify(value.toCharArray(), saltedHash.hash.toCharArray())
        return result.verified
    }

}
