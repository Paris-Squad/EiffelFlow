package org.example.data.storge

import java.util.*

class CsvDataSource<T>(
    private val filePath: String,
    private val mapper: CsvMapper<T>
) : DataSource<T> {
    override fun getAll(): List<T> {
        TODO("Not yet implemented")
    }

    override fun getItemById(itemID: UUID): List<T> {
        TODO("Not yet implemented")
    }

    override fun saveAll(items: List<T>) {
        TODO("Not yet implemented")
    }
}