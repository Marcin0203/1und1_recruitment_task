package pl.marcin.malocha.salesmanapplication.domain.usecase

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Test
import pl.marcin.malocha.salesmanapplication.core.di.providers.UnconfinedTestDispatcher
import pl.marcin.malocha.salesmanapplication.data.model.Salesman

class FilterSalesmenUseCaseTest {
    private val filterSalesmenUseCase = FilterSalesmenUseCase(UnconfinedTestDispatcher())

    @Test
    fun filterSalesmenUseCaseTest_emptyQuery_emptySalesmanList() = runTest {
        val filtered = filterSalesmenUseCase.invoke(salesmen = emptyList(), query = "")

        assertThat(filtered).isEmpty()
    }

    @Test
    fun filterSalesmenUseCaseTest_emptyQuery_salesmanListNotEmpty() = runTest {
        val filtered = filterSalesmenUseCase.invoke(
            salesmen = listOf(Salesman(name = "S1", areas = listOf("12345"))),
            query = ""
        )

        assertThat(filtered.map { it.name }).containsExactly("S1")
    }

    @Test
    fun filterSalesmenUseCaseTest_queryNotEmpty_salesmanListNotEmpty() = runTest {
        val filtered = filterSalesmenUseCase.invoke(
            salesmen = listOf(Salesman(name = "S1", areas = listOf("12345"))),
            query = "32"
        )

        assertThat(filtered).isEmpty()
    }

    @Test
    fun filterSalesmenUseCaseTest_queryNotEmpty_emptySalesmanList() = runTest {
        val filtered = filterSalesmenUseCase.invoke(
            salesmen = emptyList(),
            query = "32"
        )

        assertThat(filtered).isEmpty()
    }

    @Test
    fun filterSalesmenUseCaseTest_invalidQuery() = runTest {
        val filtered = filterSalesmenUseCase.invoke(
            salesmen = listOf(
                Salesman(name = "S1", areas = listOf("12345")),
                Salesman(name = "S2", areas = listOf("1234*")),
            ),
            query = "S"
        )

        assertThat(filtered).isEmpty()
    }

    @Test
    fun filterSalesmenUseCaseTest_exactQuery() = runTest {
        val filtered = filterSalesmenUseCase.invoke(
            salesmen = listOf(
                Salesman(name = "S1", areas = listOf("12345")),
                Salesman(name = "S2", areas = listOf("1235*")),
            ),
            query = "12345"
        )

        assertThat(filtered.map { it.name }).containsExactly("S1")
    }

    @Test
    fun filterSalesmenUseCaseTest_prefixWithStar_matchesExactAndPrefixAndBroaderPrefix() = runTest {
        val filtered = filterSalesmenUseCase.invoke(
            salesmen = listOf(
                Salesman("S1", listOf("12345")),
                Salesman("S2", listOf("123*")),
                Salesman("S3", listOf("12*")),
                Salesman("S4", listOf("999*"))
            ),
            query = "123*"
        )
        assertThat(filtered.map { it.name }).containsExactly("S1", "S2", "S3")
    }

    @Test
    fun filterSalesmenUseCaseTest_barePrefix_treatedAsStar() = runTest {
        val filtered = filterSalesmenUseCase.invoke(
            salesmen = listOf(
                Salesman("S1", listOf("12345")),
                Salesman("S2", listOf("123*")),
                Salesman("S3", listOf("12*")),
                Salesman("S4", listOf("999*"))
            ),
            query = "123"
        )
        assertThat(filtered.map { it.name }).containsExactly("S1", "S2", "S3")
    }

    @Test
    fun filterSalesmenUseCaseTest_exactQuery_shouldMatchPrefixAreaToo() = runTest {
        val filtered = filterSalesmenUseCase.invoke(
            salesmen = listOf(
                Salesman("S1", listOf("1234*")),
                Salesman("S2", listOf("99999"))
            ),
            query = "12345"
        )
        assertThat(filtered.map { it.name }).containsExactly("S1")
    }

    @Test
    fun filterSalesmenUseCaseTest_shouldMatchExactAndNarrowerPrefix() = runTest {
        val filtered = filterSalesmenUseCase.invoke(
            salesmen = listOf(
                Salesman("S1", listOf("12345")),
                Salesman("S2", listOf("123*")),
                Salesman("S3", listOf("12*"))
            ),
            query = "12"
        )
        assertThat(filtered.map { it.name }).containsExactly("S1", "S2", "S3")
    }

    @Test
    fun filterSalesmenUseCaseTest_broaderPrefix_shouldMatchExactAndNarrowerPrefix() = runTest {
        val filtered = filterSalesmenUseCase.invoke(
            salesmen = listOf(
                Salesman("S1", listOf("12345")),
                Salesman("S2", listOf("123*")),
                Salesman("S3", listOf("12*"))
            ),
            query = "12*"
        )
        assertThat(filtered.map { it.name }).containsExactly("S1", "S2", "S3")
    }

    @Test
    fun filterSalesmenUseCaseTest_whitespaceIsTrimmed() = runTest {
        val filtered = filterSalesmenUseCase.invoke(
            salesmen = listOf(
                Salesman("S1", listOf("12345")),
                Salesman("S2", listOf("123*"))
            ),
            query = "  12345  "
        )
        assertThat(filtered.map { it.name }).containsExactly("S1", "S2")
    }

    @Test
    fun filterSalesmenUseCaseTest_invalidTooLongWithStar_returnsEmpty() = runTest {
        val filtered = filterSalesmenUseCase.invoke(
            salesmen = listOf(
                Salesman("S1", listOf("12345")),
                Salesman("S2", listOf("1234*"))
            ),
            query = "12345*"
        )
        assertThat(filtered).isEmpty()
    }

    @Test
    fun filterSalesmenUseCaseTest_oneDigitBarePrefix() = runTest {
        val filtered = filterSalesmenUseCase.invoke(
            salesmen = listOf(
                Salesman("S1", listOf("12345")),
                Salesman("S2", listOf("22345"))
            ),
            query = "1"
        )
        assertThat(filtered.map { it.name }).containsExactly("S1")
    }

    @Test
    fun filterSalesmenUseCaseTest_fourDigitBarePrefix() = runTest {
        val filtered = filterSalesmenUseCase.invoke(
            salesmen = listOf(
                Salesman("S1", listOf("12345")),
                Salesman("S2", listOf("1234*")),
                Salesman("S3", listOf("1299*"))
            ),
            query = "1234"
        )
        assertThat(filtered.map { it.name }).containsExactly("S1", "S2")
    }

    @Test
    fun filterSalesmenUseCaseTest_salesmanWithMultipleAreas_anyMatchShouldInclude() = runTest {
        val filtered = filterSalesmenUseCase.invoke(
            salesmen = listOf(
                Salesman("S1", listOf("12345", "86*")),
                Salesman("S2", listOf("99999"))
            ),
            query = "86*"
        )
        assertThat(filtered.map { it.name }).containsExactly("S1")
    }

    @Test
    fun filterSalesmenUseCaseTest_exactQuery_noMatch() = runTest {
        val filtered = filterSalesmenUseCase.invoke(
            salesmen = listOf(
                Salesman("S1", listOf("1234*")),
                Salesman("S2", listOf("99999"))
            ),
            query = "88888"
        )
        assertThat(filtered).isEmpty()
    }
}