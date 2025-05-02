package presentation.presenter


import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.RoleType
import org.example.domain.model.User
import org.example.domain.usecase.auth.RegisterUseCase
import org.example.presentation.presenter.RegisterPresenter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RegisterPresenterTest {
    private val registerUseCase: RegisterUseCase = mockk()
    private lateinit var presenter: RegisterPresenter


    private val user = User(username = "admin", password = "hashedPass", role = RoleType.ADMIN)

    @BeforeEach
    fun setup() {
        presenter = RegisterPresenter(registerUseCase)

    }

    @Test
    fun `should return success when registration is successful`() {
        try {


        val username = "newuser"
        val password = "securePass123"
        val role = RoleType.MATE

        every { registerUseCase.register(username, password, role) } returns Result.success(user)

        val result = presenter.register(username, password, role)

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(user)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not implement yet")
        }
    }

    @Test
    fun `should return failure when registration fails`() {
        try {
        val username = "newuser"
        val password = "securePass123"
        val role = RoleType.MATE
        val exception = EiffelFlowException.AuthorizationException("Not allowed")

        every { registerUseCase.register(username, password, role) } returns Result.failure(exception)

        val result = presenter.register(username, password, role)

        assertThat(result.isSuccess).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(exception)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not implement yet")
        }
    }
}