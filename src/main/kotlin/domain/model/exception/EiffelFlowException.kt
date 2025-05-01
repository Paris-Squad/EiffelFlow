package org.example.domain.model.exception

import org.example.common.ValidationMessages

abstract class EiffelFlowException(message: String?) : Exception(message) {

    class UserCreationException(message: String? = null) : EiffelFlowException(message ?: "Failed to create user")

    class UserStorageException(message: String? = null) :
        EiffelFlowException(message ?: "User storage operation failed")

    class PasswordValidationException(errors: Set<ValidationMessages.ValidationRule>) :
        EiffelFlowException("Password validation failed: ${errors.joinToString(", ") { it.message }}")

    class UserNameValidationException(errors: Set<ValidationMessages.ValidationRule>) :
        EiffelFlowException("Username validation failed: ${errors.joinToString(", ") { it.message }}")

    class UsernameAlreadyExistsException : EiffelFlowException("Username already exists")

    class UnauthorizedRegistrationException : EiffelFlowException("Only admins can register new users")

    class ProjectNotFoundException(message: String?=null) : EiffelFlowException("Project not found")

    class ProjectUpdateException(message: String?) : EiffelFlowException(message ?: "Failed to update project")

    class TaskNotFoundException(message: String? = null) :
        EiffelFlowException(message ?: "Task not found")

    class NoChangesException(message: String? = null) :
        EiffelFlowException(message ?: "No changes detected")
    class ElementNotFoundException(message: String) : RuntimeException(message)
}

