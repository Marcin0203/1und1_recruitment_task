package pl.marcin.malocha.salesmanapplication.screens.salesmanlist

import androidx.compose.ui.text.input.TextFieldValue
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import pl.marcin.malocha.salesmanapplication.core.di.providers.TestDispatchers
import pl.marcin.malocha.salesmanapplication.core.di.providers.UnconfinedTestDispatcher
import pl.marcin.malocha.salesmanapplication.core.rules.MainDispatcherRule
import pl.marcin.malocha.salesmanapplication.data.model.Salesman
import pl.marcin.malocha.salesmanapplication.domain.usecase.FilterSalesmenUseCase
import pl.marcin.malocha.salesmanapplication.domain.usecase.GetSalesmenUseCase
import pl.marcin.malocha.salesmanapplication.ui.screens.salesmanlist.SalesmanListIntent
import pl.marcin.malocha.salesmanapplication.ui.screens.salesmanlist.SalesmanListViewModel

class SalesmanListViewModelTest {
    private val getSalesmenUseCaseMock = mockk<GetSalesmenUseCase>(relaxed = true)
    private val filterSalesmenUseCaseMock = mockk<FilterSalesmenUseCase>(relaxed = true)

    private val defaultSalesmanList = listOf(
        Salesman("S1", listOf("12345", "12346")),
        Salesman("S2", listOf("1234*")),
        Salesman("S3 Test", listOf("1299*"))
    )

    lateinit var viewModel: SalesmanListViewModel
    private val scheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(scheduler = scheduler)

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(testDispatcher)

    @Before
    fun setUp() {
        every { getSalesmenUseCaseMock.invoke() } returns flow { emit(defaultSalesmanList) }
        coEvery { filterSalesmenUseCaseMock.invoke(any(), any()) } returns defaultSalesmanList

        viewModel = createViewModel()
    }

    private fun createViewModel(testDispatchers: TestDispatchers = UnconfinedTestDispatcher()): SalesmanListViewModel {
        return SalesmanListViewModel(
            dispatchers = testDispatchers,
            getSalesmenUseCase = getSalesmenUseCaseMock,
            filterSalesmenUseCase = filterSalesmenUseCaseMock
        )
    }

    @Test
    fun observerForSalesmenTest() = runTest {
        viewModel.state.test {
            val state = awaitItem()

            assertThat(state.salesmen.size).isEqualTo(3)
            assertThat(state.query.text).isEmpty()

            assertThat(state.salesmen[0].name).isEqualTo("S1")
            assertThat(state.salesmen[0].areas).isEqualTo("12345, 12346")
            assertThat(state.salesmen[0].id).isEqualTo(defaultSalesmanList[0].hashCode())
            assertThat(state.salesmen[0].isExpanded).isFalse()
            assertThat(state.salesmen[0].avatarText).isEqualTo("S")

            assertThat(state.salesmen[1].name).isEqualTo("S2")
            assertThat(state.salesmen[1].areas).isEqualTo("1234*")
            assertThat(state.salesmen[1].id).isEqualTo(defaultSalesmanList[1].hashCode())
            assertThat(state.salesmen[1].isExpanded).isFalse()
            assertThat(state.salesmen[1].avatarText).isEqualTo("S")

            assertThat(state.salesmen[2].name).isEqualTo("S3 Test")
            assertThat(state.salesmen[2].areas).isEqualTo("1299*")
            assertThat(state.salesmen[2].id).isEqualTo(defaultSalesmanList[2].hashCode())
            assertThat(state.salesmen[2].isExpanded).isFalse()
            assertThat(state.salesmen[2].avatarText).isEqualTo("S")
        }

        coVerify {
            filterSalesmenUseCaseMock.invoke(salesmen = defaultSalesmanList, query = "")
        }
    }

    @Test
    fun intentsTest_QueryChanged() = runTest {
        viewModel.send(SalesmanListIntent.QueryChanged(TextFieldValue("129")))
        scheduler.runCurrent()

        assertThat(viewModel.state.value.query.text).isEqualTo("129")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun intentsTest_QueryChanged_debounceDelay() = runTest {
        clearAllMocks()

        viewModel.send(SalesmanListIntent.QueryChanged(TextFieldValue("1")))
        scheduler.advanceTimeBy(1000)

        coVerify(exactly = 0) {
            filterSalesmenUseCaseMock.invoke(any(), any())
        }

        scheduler.advanceTimeBy(1)

        coVerify(exactly = 1) {
            filterSalesmenUseCaseMock.invoke(
                salesmen = any(),
                query = "1"
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun intentsTest_QueryChanged_isExpanded() = runTest {
        coEvery { filterSalesmenUseCaseMock.invoke(any(), any()) } returns listOf(defaultSalesmanList[0])

        viewModel.send(SalesmanListIntent.SalesmanClicked(defaultSalesmanList[0].hashCode()))
        viewModel.send(SalesmanListIntent.QueryChanged(TextFieldValue("12345")))
        scheduler.advanceTimeBy(1500)


        assertThat(viewModel.state.value.query.text).isEqualTo("12345")

        assertThat(viewModel.state.value.salesmen.size).isEqualTo(1)
        assertThat(viewModel.state.value.salesmen[0].id).isEqualTo(defaultSalesmanList[0].hashCode())
        assertThat(viewModel.state.value.salesmen[0].isExpanded).isTrue()
    }

    @Test
    fun intentsTest_SalesmanClicked() = runTest {
        viewModel.send(SalesmanListIntent.SalesmanClicked(defaultSalesmanList[0].hashCode()))

        assertThat(viewModel.state.value.salesmen[0].isExpanded).isTrue()
        assertThat(viewModel.state.value.salesmen[1].isExpanded).isFalse()
        assertThat(viewModel.state.value.salesmen[2].isExpanded).isFalse()
    }
}