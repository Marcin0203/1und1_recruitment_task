package pl.marcin.malocha.salesmanapplication.core.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.marcin.malocha.salesmanapplication.data.repository.SalesmanRepositoryImpl
import pl.marcin.malocha.salesmanapplication.domain.repository.SalesmanRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindSalesmanRepository(
        impl: SalesmanRepositoryImpl
    ): SalesmanRepository
}