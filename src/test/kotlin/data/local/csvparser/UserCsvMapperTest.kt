package data.local.csvparser

import com.google.common.truth.Truth.assertThat
import org.example.data.local.parser.UserCsvParser
import org.example.domain.model.RoleType
import org.example.domain.model.User
import java.util.*
import kotlin.test.Test

class UserCsvParserTest {

    private val userCsvParser = UserCsvParser()

    @Test
    fun `should map CSV line to User entity correctly`() {

        //Given / When
        val result = userCsvParser.parseCsvLine(CSV_STRING_LINE)

        // Then
        assertThat(result).isEqualTo(USER)

    }

    @Test
    fun `should map User entity to CSV line correctly`() {

        //Given / When
        val result = userCsvParser.serialize(USER)

        // Then
        assertThat(result).isEqualTo(CSV_STRING_LINE)
    }

    companion object {
        private val USER = User(
            userId = UUID.fromString("02ad4499-5d4c-4450-8fd1-8294f1bb5748"),
            username = "username",
            password = "password",
            role = RoleType.ADMIN
        )

        private const val CSV_STRING_LINE = "02ad4499-5d4c-4450-8fd1-8294f1bb5748,username,password,ADMIN"
    }
}
