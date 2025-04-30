package org.example.domain.usecase.auth

import java.math.BigInteger
import java.security.MessageDigest

class HashPasswordUseCase {
    fun hashPassword(password: String): String {
        val md = MessageDigest.getInstance(HASH_ALGORITHM)
        return BigInteger(SIGNUM, md.digest(password.toByteArray()))
            .toString(HEX_RADIX)
            .padStart(PADDING_LENGTH, '0')
    }

    companion object {
        private const val HASH_ALGORITHM = "MD5"
        private const val HEX_RADIX = 16
        private const val PADDING_LENGTH = 32
        private const val SIGNUM = 1
    }
}