package pl.marcin.malocha.salesmanapplication.core.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.marcin.malocha.salesmanapplication.core.di.providers.DefaultDispatchers
import pl.marcin.malocha.salesmanapplication.core.di.providers.DispatcherProvider
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class DispatcherProviderModule {
    @Binds
    @Singleton
    abstract fun bindDispatcherModule(defaultDispatchers: DefaultDispatchers): DispatcherProvider
}