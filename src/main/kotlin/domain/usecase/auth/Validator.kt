package org.example.domain.usecase.auth

object Validator {

    private const val MIN_PASSWORD_LENGTH = 8
    private val SPECIAL_CHAR_REGEX = ".*[!@#$%^&*()\\-_+={}\\[\\]|\\\\:;\"'<>,.?/].*".toRegex()
    private const val MIN_USERNAME_LENGTH = 3
    private const val MAX_USERNAME_LENGTH = 30
    private val USERNAME_REGEX = "^[a-zA-Z0-9_]+$".toRegex()

    fun validatePassword(password: String): MutableSet<String> {
        val errors = mutableSetOf<String>().apply {
            if (password.length < MIN_PASSWORD_LENGTH) add("Password must be at least $MIN_PASSWORD_LENGTH characters long")

            if (!password.any { it.isUpperCase() }) add("Password must contain at least one uppercase letter")

            if (!password.any { it.isLowerCase() }) add("Password must contain at least one lowercase letter")

            if (!password.any { it.isDigit() }) add("Password must contain at least one digit")

            if (!SPECIAL_CHAR_REGEX.matches(password)) add("Password must contain at least one special character")
        }
        return errors
    }

    fun validateUserName(userName: String): MutableSet<String> {
        val errors = mutableSetOf<String>().apply {
            if (userName.length < MIN_USERNAME_LENGTH) add("Username must be at least $MIN_USERNAME_LENGTH characters long")

            if (userName.length > MAX_USERNAME_LENGTH) add("Username must be at most $MAX_USERNAME_LENGTH characters long")

            if (!USERNAME_REGEX.matches(userName)) add("Username must contain only letters, numbers, and underscores")
        }
        return errors
    }
}