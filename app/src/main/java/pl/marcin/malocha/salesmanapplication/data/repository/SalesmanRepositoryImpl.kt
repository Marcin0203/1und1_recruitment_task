package pl.marcin.malocha.salesmanapplication.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import pl.marcin.malocha.salesmanapplication.data.model.Salesman
import pl.marcin.malocha.salesmanapplication.domain.repository.SalesmanRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SalesmanRepositoryImpl @Inject constructor(): SalesmanRepository {
    override fun getSalesmen(): Flow<List<Salesman>> {
        return flow {
            emit(
                listOf(
                    Salesman(name = "Artem Titarenko", areas = listOf("76133")),
                    Salesman(name = "Bernd Schmitt", areas = listOf("7619*")),
                    Salesman(name = "Chris Krapp", areas = listOf("762*")),
                    Salesman(name = "Alex Uber", areas = listOf("86*")),
                )
            )
        }
    }
}