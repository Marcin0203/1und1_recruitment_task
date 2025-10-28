package pl.marcin.malocha.salesmanapplication.ui.screens.salesmanlist

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import pl.marcin.malocha.salesmanapplication.core.di.providers.DispatcherProvider
import pl.marcin.malocha.salesmanapplication.data.model.Salesman
import pl.marcin.malocha.salesmanapplication.domain.usecase.FilterSalesmenUseCase
import pl.marcin.malocha.salesmanapplication.domain.usecase.GetSalesmenUseCase
import kotlin.collections.joinToString

@HiltViewModel
class SalesmanListViewModel @Inject constructor(
    private val dispatchers: DispatcherProvider,
    private val getSalesmenUseCase: GetSalesmenUseCase,
    private val filterSalesmenUseCase: FilterSalesmenUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(SalesmanListUiState())
    val state: StateFlow<SalesmanListUiState> = _state

    private val intents = MutableSharedFlow<SalesmanListIntent>(extraBufferCapacity = 64)
    private val salesmenState = MutableStateFlow(SalesmenState())

    init {
        bindIntents()
        observerForSalesmen()
        observerForUiState()
    }

    fun send(intent: SalesmanListIntent) {
        intents.tryEmit(intent)
    }

    private fun bindIntents() {
        intents
            .filterIsInstance<SalesmanListIntent.QueryChanged>()
            .map { it.value }
            .onEach { value ->
                _state.update { it.copy(query = value) }
            }
            .flowOn(dispatchers.main)
            .launchIn(viewModelScope)

        intents
            .filterIsInstance<SalesmanListIntent.QueryChanged>()
            .map { it.value.text }
            .debounce(1000)
            .distinctUntilChanged()
            .onEach { value ->
                salesmenState.update {
                    it.copy(
                        filteredSalesmen = filterSalesmenUseCase(
                            salesmen = salesmenState.value.salesmen,
                            query = value
                        )
                    )
                }
            }
            .flowOn(dispatchers.main)
            .launchIn(viewModelScope)

        intents
            .filterIsInstance<SalesmanListIntent.SalesmanClicked>()
            .map { it.id }
            .onEach { id ->
                _state.update { state ->
                    state.copy(salesmen = state.salesmen.map {
                        val isClickedItem = it.id == id
                        val isExpanded = isClickedItem && !it.isExpanded

                        it.copy(isExpanded = isExpanded)
                    })
                }
            }
            .flowOn(dispatchers.main)
            .launchIn(viewModelScope)
    }

    private fun observerForSalesmen() {
        viewModelScope.launch(dispatchers.main) {
            getSalesmenUseCase().collectLatest { salesmen ->
                salesmenState.update {
                    it.copy(
                        salesmen = salesmen,
                        filteredSalesmen = filterSalesmenUseCase(salesmen = salesmen, query = state.value.query.text)
                    )
                }
            }
        }
    }

    private fun observerForUiState() {
        viewModelScope.launch(dispatchers.main) {
            salesmenState
                .map { salesmenState ->
                    salesmenState.filteredSalesmen.mapIndexed { index, salesman ->
                        val id = salesman.hashCode()
                        val isExpanded = _state.value.salesmen.firstOrNull { it.id == id }?.isExpanded ?: false

                        SalesmanUiState(
                            id = id,
                            avatarText = salesman.name.first().uppercase(),
                            name = salesman.name,
                            isExpanded = isExpanded,
                            areas = salesman.areas.joinToString(", ")
                        )
                    }
                }
                .collectLatest { mappedSalesmen ->
                    _state.update { it.copy(salesmen = mappedSalesmen) }
                }
        }
    }
}

data class SalesmanListUiState(
    val query: TextFieldValue = TextFieldValue(""),
    val salesmen: List<SalesmanUiState> = emptyList()
)

data class SalesmanUiState(
    val id: Int,
    val avatarText: String,
    val name: String,
    val isExpanded: Boolean,
    val areas: String
)

data class SalesmenState(
    val salesmen: List<Salesman> = emptyList(),
    val filteredSalesmen: List<Salesman> = emptyList()
)