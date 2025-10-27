package pl.marcin.malocha.salesmanapplication.core.di.providers

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import javax.inject.Inject

interface DispatcherProvider {
    val main: CoroutineDispatcher
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
}

class DefaultDispatchers @Inject constructor() : DispatcherProvider {
    override val main: CoroutineDispatcher
        get() = Dispatchers.Main
    override val io: CoroutineDispatcher
        get() = Dispatchers.IO
    override val default: CoroutineDispatcher
        get() = Dispatchers.Default
}

abstract class TestDispatchers(val testDispatchers: TestDispatcher) : DispatcherProvider {
    override val main: CoroutineDispatcher
        get() = testDispatchers
    override val io: CoroutineDispatcher
        get() = testDispatchers
    override val default: CoroutineDispatcher
        get() = testDispatchers
}

class StandardTestDispatcher(scheduler: TestCoroutineScheduler? = null) :
    TestDispatchers(StandardTestDispatcher(scheduler = scheduler))

@OptIn(ExperimentalCoroutinesApi::class)
class UnconfinedTestDispatcher(scheduler: TestCoroutineScheduler? = null) :
    TestDispatchers(UnconfinedTestDispatcher(scheduler = scheduler))