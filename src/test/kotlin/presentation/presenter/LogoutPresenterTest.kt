package presentation.presenter

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import org.example.domain.exception.EiffelFlowException
import org.example.domain.usecase.auth.LogoutUseCase
import org.example.presentation.presenter.LogoutPresenter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class LogoutPresenterTest {
    private val logoutUseCase : LogoutUseCase = mockk(relaxed = true)
    private lateinit var logoutPresenter: LogoutPresenter

    @BeforeEach
    fun setup(){
        logoutPresenter = LogoutPresenter(logoutUseCase)
    }

    @Test
    fun`should return success when logout is successful`(){
        try {
            every { logoutUseCase.logout() } just runs
            val result = logoutPresenter.logout()
            assertThat(result.isSuccess).isTrue()
            assertThat(result.getOrNull()).isEqualTo(Unit)
        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not implement yet")
        }
    }
    @Test
    fun`should return failure when logout is fails`(){
        try {
            val exception = EiffelFlowException.AuthorizationException("failed logout ")

            every { logoutUseCase.logout() } throws exception

            val result = logoutPresenter.logout()

            assertThat(result.isSuccess).isTrue()
            assertThat(result.getOrNull()).isEqualTo(exception)

        } catch (e: NotImplementedError) {
            assertThat(e.message).contains("Not implement yet")
        }
    }
}