package data.storge.mapper

import com.google.common.truth.Truth.assertThat
import org.example.data.storge.mapper.UserCsvMapper
import org.example.domain.model.entities.RoleType
import org.example.domain.model.entities.User
import java.util.*
import kotlin.test.Test

class UserCsvMapperTest {

    private val userCsvMapper = UserCsvMapper()

    @Test
    fun `test mapFrom CSV to User`() {
        val csv = "123e4567-e89b-12d3-a456-426614174000,username,password,ADMIN"

        try {
            val user = userCsvMapper.mapFrom(csv)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }

    @Test
    fun `test mapTo User to CSV`() {
        val user = User(
            userId = UUID.randomUUID(),
            username = "username",
            password = "password",
            role = RoleType.ADMIN
        )


        try {
            val csv = userCsvMapper.mapTo(user)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not yet implemented")
        }
    }
}
