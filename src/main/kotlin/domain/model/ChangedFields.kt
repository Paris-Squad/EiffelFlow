package org.example.domain.model

data class FieldChange(
    val fieldName: String,
    val oldValue: String?,
    val newValue: String?
)