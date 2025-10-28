package pl.marcin.malocha.salesmanapplication.domain.repository

import kotlinx.coroutines.flow.Flow
import pl.marcin.malocha.salesmanapplication.data.model.Salesman

interface SalesmanRepository {
    fun getSalesmen(): Flow<List<Salesman>>
}