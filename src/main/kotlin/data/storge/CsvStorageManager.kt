package org.example.data.storge

import java.io.File
import java.io.FileNotFoundException

class CsvStorageManager(
    private val file: File
) {

    @Throws(FileNotFoundException::class)
    fun readLinesFromFile(): List<String> {
        when {
            file.exists().not() -> {
                throw FileNotFoundException("File ${file.path} not found.")
            }

            file.isFile.not() -> {
                throw FileNotFoundException("${file.path} Is a directory")
            }

            else -> {
                return file.readLines()
            }
        }
    }


    fun writeLinesToFile(input: String) {
        file.appendText(input)
    }
}