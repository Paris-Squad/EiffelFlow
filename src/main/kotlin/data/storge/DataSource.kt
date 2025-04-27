package org.example.data.storge

import java.util.UUID

interface DataSource<T> {
    fun getAll(): List<T>
    fun getItemById(itemID: UUID): List<T>
    fun saveAll(items: List<T>)
}