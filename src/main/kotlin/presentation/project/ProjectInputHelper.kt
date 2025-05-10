package org.example.presentation.project

import org.example.data.utils.SessionManger
import org.example.domain.model.Project
import org.example.presentation.io.InputReader
import org.example.presentation.io.Printer
import java.util.UUID

object ProjectInputHelper {

    fun collectProjectInput(inputReader: InputReader, printer: Printer): Project? {
        printer.displayLn("Enter project name:")
        val name = inputReader.readString()
        if (name.isNullOrBlank()) {
            printer.displayLn("Project name cannot be empty.")
            return null
        }

        printer.displayLn("Enter project description:")
        val description = inputReader.readString()
        if (description.isNullOrBlank()) {
            printer.displayLn("Project description cannot be empty.")
            return null
        }


        return Project(
            projectName = name,
            projectDescription = description,
            adminId = SessionManger.getUser().userId
        )
    }
}