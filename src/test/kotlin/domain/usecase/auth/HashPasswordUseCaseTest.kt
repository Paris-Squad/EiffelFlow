package domain.usecase.auth

import org.example.domain.usecase.auth.HashPasswordUseCase
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class HashPasswordUseCaseTest {

    private lateinit var hashPasswordUseCase: HashPasswordUseCase

    @BeforeEach
    fun setUp() {
        hashPasswordUseCase = HashPasswordUseCase()
    }

    @Test
    fun `hashPassword should produce consistent hashes for the same input`() {
        val password = "SecurePassword123!"

        val firstHash = hashPasswordUseCase.hashPassword(password)
        val secondHash = hashPasswordUseCase.hashPassword(password)

        assertEquals(firstHash, secondHash)
    }

    @Test
    fun `hashPassword should produce different hashes for different inputs`() {
        val password1 = "SecurePassword123!"
        val password2 = "SecurePassword123"

        val hash1 = hashPasswordUseCase.hashPassword(password1)
        val hash2 = hashPasswordUseCase.hashPassword(password2)

        assertNotEquals(hash1, hash2)
    }

    @Test
    fun `hashPassword should generate a hash with correct length`() {
        val password = "password"

        val hash = hashPasswordUseCase.hashPassword(password)

        assertEquals(32, hash.length)
    }

    @Test
    fun `hashPassword should handle empty strings`() {
        val emptyPassword = ""

        val hash = hashPasswordUseCase.hashPassword(emptyPassword)

        assertEquals(32, hash.length)
    }

    @Test
    fun `hashPassword should handle special characters`() {
        val specialCharsPassword = "!@#$%^&*()_+{}|:<>?~"

        val hash = hashPasswordUseCase.hashPassword(specialCharsPassword)

        assertEquals(32, hash.length)
    }

}