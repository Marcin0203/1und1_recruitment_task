package pl.marcin.malocha.salesmanapplication.data.repository

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SalesmanRepositoryTest {
    private val salesmanRepository = SalesmanRepositoryImpl()

    @Test
    fun getSalesmenTest() = runTest {
        val salesmen = salesmanRepository.getSalesmen().first()

        assertThat(salesmen.size).isEqualTo(4)

        assertThat(salesmen[0].name).isEqualTo("Artem Titarenko")
        assertThat(salesmen[0].areas).isEqualTo(listOf("76133"))

        assertThat(salesmen[1].name).isEqualTo("Bernd Schmitt")
        assertThat(salesmen[1].areas).isEqualTo(listOf("7619*"))

        assertThat(salesmen[2].name).isEqualTo("Chris Krapp")
        assertThat(salesmen[2].areas).isEqualTo(listOf("762*"))

        assertThat(salesmen[3].name).isEqualTo("Alex Uber")
        assertThat(salesmen[3].areas).isEqualTo(listOf("86*"))
    }
}