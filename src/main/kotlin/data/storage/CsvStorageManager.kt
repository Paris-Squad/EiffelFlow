package org.example.data.storage

import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

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
        file.appendText("$input\n")
    }

    fun updateLinesToFile(input: String, oldLine: String) {
        val lines = readLinesFromFile().toMutableList()
        val index = lines.indexOf(oldLine)

        if (index != -1) {
            lines[index] = input
            file.writeText(lines.joinToString("\n"))
        } else {
            throw IOException()
        }
    }
}