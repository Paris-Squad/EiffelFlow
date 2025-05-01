package org.example.common

object Constants {
    enum class ValidationRule(val message: String) {
        USERNAME_TOO_SHORT(message = "Username must be at least 3 characters long"),
        USERNAME_TOO_LONG(message = "Username must be at most 30 characters long"),
        USERNAME_INVALID_CHARACTERS(message = "Username must contain only letters, numbers, and underscores"),
        INVALID_USERNAME(message = "Invalid userName"),
        PASSWORD_TOO_SHORT(message = "Password must be at least 8 characters long"),
        PASSWORD_NO_UPPERCASE(message = "Password must contain at least one uppercase letter"),
        PASSWORD_NO_LOWERCASE(message = "Password must contain at least one lowercase letter"),
        PASSWORD_NO_DIGIT(message = "Password must contain at least one digit"),
        PASSWORD_NO_SPECIAL_CHAR(message = "Password must contain at least one special character"),
       INVALID_PASSWORD(message = "Invalid password"),
    }
    
    enum class TaskField(val displayName: String) {
        TITLE("title"),
        DESCRIPTION("description"),
        ASSIGNEE("assignee"),
        STATE("state"),
        ROLE("role"),
        PROJECT("project"),
    }
}