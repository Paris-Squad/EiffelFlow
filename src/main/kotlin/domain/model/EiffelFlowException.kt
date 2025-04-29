package org.example.domain.model

abstract class EiffelFlowException(message: String?) : Exception(message) {
    class ElementNotFoundException(message: String) : EiffelFlowException(message)
}