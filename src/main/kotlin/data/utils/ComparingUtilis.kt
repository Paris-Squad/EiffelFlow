package org.example.data.utils

import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

// Requires kotlin-reflect dependency
fun <T : Any> T.getChangedFields(new: T): Map<String, Pair<Any?, Any?>> {
    return this::class.memberProperties
        .associate { prop ->
            val oldValue = (prop as KProperty1<T, *>).get(this)
            val newValue = prop.get(new)
            prop.name to (oldValue to newValue)
        }
        .filter { (_, values) -> values.first != values.second }
}