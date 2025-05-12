package org.example.presentation.project

import kotlinx.coroutines.runBlocking
import org.example.domain.model.Project
import org.example.domain.model.TaskState
import org.example.domain.usecase.project.GetProjectUseCase
import org.example.presentation.BaseCli
import org.example.presentation.io.Printer
import kotlin.math.max

class ShowProjectSwimLanesCLI(
    private val getProjectUseCase: GetProjectUseCase,
    private val printer: Printer
) : BaseCli(printer) {

    fun showProjectsBySwimLanes() {
        tryStartCli {
            val projects = getProjects()

            if (projects.isEmpty()) {
                printer.displayLn("No projects found.")
                return@tryStartCli
            }

            val projectsByState = getProjectUseCase.groupProjectsByState(projects)
            if (projectsByState.isEmpty()) {
                printer.displayLn("No task states found for projects.")
                return@tryStartCli
            }

            val columnWidths = calculateColumnWidths(projectsByState)

            renderSwimLanes(projectsByState, columnWidths)
        }
    }

    private fun calculateColumnWidths(projectsByState: Map<TaskState, List<Project>>): List<Int> {
        val widths = mutableListOf<Int>()
        projectsByState.forEach { (state, projects) ->
            val stateNameWidth = state.name.length
            val maxProjectNameWidth = projects.maxOfOrNull { it.projectName.length } ?: 0
            val columnWidth = max(
                max(stateNameWidth, maxProjectNameWidth.coerceAtMost(MAX_NAME_WIDTH)) + COLUMN_PADDING,
                MIN_COLUMN_WIDTH
            )
            widths.add(columnWidth)
        }
        return widths
    }

    private fun renderSwimLanes(
        projectsByState: Map<TaskState, List<Project>>,
        columnWidths: List<Int>
    ) {
        printer.displayLn("\n=== PROJECTS BY STATUS (SWIMMING LANES) ===\n")

        val states = projectsByState.keys.toList()
        val headerLine = createHeaderLine(states, columnWidths)
        val totalWidth = calculateTotalWidth(columnWidths)

        printer.displayLn("┌${"─".repeat(totalWidth)}┐")
        printer.displayLn("│$headerLine│")

        val separatorLine = createSeparatorLine(columnWidths)
        printer.displayLn("├$separatorLine┤")

        renderProjectCards(projectsByState, columnWidths)

        printer.displayLn("└${"─".repeat(totalWidth)}┘")
    }

    private fun calculateTotalWidth(columnWidths: List<Int>): Int {
        val columnsWidth = columnWidths.sum() + columnWidths.size * (CELL_PADDING * 2)
        val dividerCount = columnWidths.size - 1
        return columnsWidth + dividerCount
    }

    private fun createHeaderLine(states: List<TaskState>, columnWidths: List<Int>): String {
        val builder = StringBuilder()

        for (i in states.indices) {
            val state = states[i]
            val width = columnWidths[i]
            builder.append(" ${state.name.padEnd(width - CELL_PADDING)} ")
            if (i < states.size - 1) builder.append("│")
        }

        return builder.toString()
    }

    private fun createSeparatorLine(columnWidths: List<Int>): String {
        val builder = StringBuilder()
        for (i in columnWidths.indices) {
            val width = columnWidths[i]
            builder.append("─".repeat(width + CELL_PADDING * 2))
            if (i < columnWidths.size - 1) builder.append("+")
        }
        return builder.toString()
    }

    private fun renderProjectCards(
        projectsByState: Map<TaskState, List<Project>>,
        columnWidths: List<Int>
    ) {
        val states = projectsByState.keys.toList()
        val maxProjectsInLane = projectsByState.values.maxOfOrNull { it.size } ?: 0

        for (i in 0 until maxProjectsInLane) {
            val lineBuilder = StringBuilder()

            for (j in states.indices) {
                val projects = projectsByState[states[j]] ?: emptyList()
                val width = columnWidths[j]

                if (i < projects.size) {
                    val project = projects[i]
                    val displayName = formatProjectName(project.projectName, width)
                    lineBuilder.append(" ${displayName.padEnd(width - CELL_PADDING)} ")
                } else {
                    lineBuilder.append(" ".repeat(width + CELL_PADDING * 2))
                }

                if (j < states.size - 1) lineBuilder.append("│")
            }

            printer.displayLn("│$lineBuilder│")
        }
    }

    private fun formatProjectName(name: String, columnWidth: Int): String {
        return if (name.length > columnWidth - TRUNCATION_BUFFER) {
            name.take(columnWidth - (TRUNCATION_BUFFER + ELLIPSIS_LENGTH)) + "..."
        } else {
            name.padEnd(columnWidth - CELL_PADDING)
        }
    }

    private fun getProjects(): List<Project> {
        return runBlocking {
            getProjectUseCase.getProjects()
        }
    }

    companion object {
        private const val MIN_COLUMN_WIDTH = 20
        private const val MAX_NAME_WIDTH = 30
        private const val COLUMN_PADDING = 2
        private const val CELL_PADDING = 1
        private const val TRUNCATION_BUFFER = 4
        private const val ELLIPSIS_LENGTH = 3
    }
}