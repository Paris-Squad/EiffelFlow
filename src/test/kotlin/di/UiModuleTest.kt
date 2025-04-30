package di

import org.example.di.useCasesModule
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest

class UiModuleTest: KoinTest {

    @BeforeEach
    fun setUp() {
        startKoin {
            modules(useCasesModule)
        }
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
    }

}