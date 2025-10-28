package pl.marcin.malocha.salesmanapplication.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import pl.marcin.malocha.salesmanapplication.core.di.providers.DispatcherProvider
import pl.marcin.malocha.salesmanapplication.data.model.Salesman
import pl.marcin.malocha.salesmanapplication.data.repository.SalesmanRepositoryImpl
import pl.marcin.malocha.salesmanapplication.domain.repository.SalesmanRepository
import javax.inject.Inject

class GetSalesmenUseCase @Inject constructor(
    private val salesmanRepository: SalesmanRepository
) {
    operator fun invoke(): Flow<List<Salesman>> = salesmanRepository.getSalesmen()
}