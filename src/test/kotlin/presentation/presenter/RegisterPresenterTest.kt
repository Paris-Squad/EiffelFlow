package presentation.presenter


import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.example.domain.exception.EiffelFlowException
import org.example.domain.usecase.auth.RegisterUseCase
import org.example.presentation.presenter.RegisterPresenter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.UserMock

class RegisterPresenterTest {
    private val registerUseCase: RegisterUseCase = mockk(relaxed = true)
    private lateinit var registerPresenter: RegisterPresenter
    private val user = UserMock

    @BeforeEach
    fun setup() {
        registerPresenter = RegisterPresenter(registerUseCase)

    }

    @Test
    fun `should return success when registration is successful`() {
        try {

        every { registerUseCase.register(user.validUser.username, user.validUser.password, user.validUser.role
        ) }returns Result.success(user.validUser)

        val result = registerPresenter.register(user.validUser.username, user.validUser.password, user.validUser.role)

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(user)

        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not implement yet")
        }
    }

    @Test
    fun `should return failure when registration fails`() {
        try {

        val exception = EiffelFlowException.AuthorizationException("Not allowed")

        every { registerUseCase.register(user.validUser.username, user.validUser.password, user.validUser.role
        ) } returns Result.failure(exception)

        val result = registerPresenter.register(user.validUser.username, user.validUser.password, user.validUser.role)

        assertThat(result.isSuccess).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(exception)

        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not implement yet")
        }
    }
}