package data.storge.mapper

import com.google.common.truth.Truth.assertThat
import kotlinx.datetime.LocalDateTime
import org.example.data.storge.mapper.ProjectCsvMapper
import org.example.domain.model.entities.Project
import java.util.*
import kotlin.test.Test

class ProjectCsvMapperTest {

    private val projectCsvMapper = ProjectCsvMapper()

    @Test
    fun `should map CSV line to Project entity correctly`() {

        //Given / When
        val result = projectCsvMapper.mapFrom(CSV_STRING_LINE)

        // Then
        assertThat(result).isEqualTo(PROJECT)

    }

    @Test
    fun `should map Project entity to CSV line correctly`() {

        //Given / When
        val result = projectCsvMapper.mapTo(PROJECT)

        // Then
        assertThat(result).isEqualTo(CSV_STRING_LINE)
    }

    companion object {
        private val PROJECT = Project(
            projectId = UUID.fromString("02ad4499-5d4c-4450-8fd1-8294f1bb5748"),
            projectName = "Project1",
            projectDescription = "Description1",
            createdAt = LocalDateTime.parse("1999-08-07T22:22:22"),
            adminId = UUID.fromString("02ad4499-5d4c-4450-8fd1-8294f1bb5741"),
            states = emptyList()
        )

        private const val CSV_STRING_LINE =
            "02ad4499-5d4c-4450-8fd1-8294f1bb5748,Project1,Description1,1999-08-07T22:22:22,02ad4499-5d4c-4450-8fd1-8294f1bb5741"

    }
}
