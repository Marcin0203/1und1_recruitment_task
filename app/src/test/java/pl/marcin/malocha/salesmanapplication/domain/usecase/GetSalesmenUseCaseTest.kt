package pl.marcin.malocha.salesmanapplication.domain.usecase

import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import pl.marcin.malocha.salesmanapplication.domain.repository.SalesmanRepository

class GetSalesmenUseCaseTest {
    private val salesmanRepositoryMock = mockk<SalesmanRepository>(relaxed = true)

    @Test
    fun getSalesmenUseCaseTest() {
        GetSalesmenUseCase(salesmanRepository = salesmanRepositoryMock).invoke()

        verify {
            salesmanRepositoryMock.getSalesmen()
        }
    }
}