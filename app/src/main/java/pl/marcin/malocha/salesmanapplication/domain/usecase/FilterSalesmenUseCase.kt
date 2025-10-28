package pl.marcin.malocha.salesmanapplication.domain.usecase

import kotlinx.coroutines.withContext
import pl.marcin.malocha.salesmanapplication.core.di.providers.DispatcherProvider
import pl.marcin.malocha.salesmanapplication.data.model.Salesman
import javax.inject.Inject

class FilterSalesmenUseCase @Inject constructor(
   private val dispatcherProvider: DispatcherProvider
) {
    suspend operator fun invoke(salesmen: List<Salesman>, query: String): List<Salesman> {
        return withContext(dispatcherProvider.default) {
            val raw = query.trim()
            if (raw.isBlank()) return@withContext salesmen

            val exact = Regex("^\\d{5}$")           // e.g. 76133
            val starPrefix = Regex("^\\d{1,4}\\*$") // e.g. 761*, 86*
            val barePrefix = Regex("^\\d{1,4}$")     // e.g. 76 (treat as 76*)

            // normalize user input into a comparable form
            val expr = when {
                raw.matches(barePrefix) -> "$raw*" // implicit wildcard while typing
                else -> raw
            }

            fun covers(areaExp: String, code5: String): Boolean {
                val a = areaExp.trim()
                return when {
                    a.matches(exact) -> a == code5
                    a.matches(starPrefix) -> code5.startsWith(a.dropLast(1))
                    else -> false
                }
            }

            return@withContext when {
                // User typed an exact 5-digit code
                expr.matches(exact) -> {
                    salesmen.filter { s -> s.areas.any { areaExp -> covers(areaExp, expr) } }
                }
                // User typed a prefix expression like "762*" or bare digits treated as prefix
                expr.matches(starPrefix) -> {
                    val inPref = expr.dropLast(1)
                    salesmen.filter { s ->
                        s.areas.any { areaExp ->
                            when {
                                areaExp.matches(exact) -> areaExp.startsWith(inPref)
                                areaExp.matches(starPrefix) -> {
                                    val aPref = areaExp.dropLast(1)
                                    aPref.startsWith(inPref) || inPref.startsWith(aPref)
                                }

                                else -> false
                            }
                        }
                    }
                }
                // Invalid expression
                else -> emptyList()
            }
        }
    }
}