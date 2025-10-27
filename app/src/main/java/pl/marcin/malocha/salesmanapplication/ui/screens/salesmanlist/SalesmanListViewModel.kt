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
import pl.marcin.malocha.salesmanapplication.data.repository.SalesmanRepository

@HiltViewModel
class SalesmanListViewModel @Inject constructor(
    private val salesmanRepository: SalesmanRepository,
    private val dispatchers: DispatcherProvider
) : ViewModel() {
    private val _state = MutableStateFlow(SalesmanListUiState())
    val state: StateFlow<SalesmanListUiState> = _state

    private val intents = MutableSharedFlow<SalesmanListIntent>(extraBufferCapacity = 64)

    init {
        bindIntents()
        observerForSalesmen()
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
                filterSalesmen(value)
            }
            .flowOn(dispatchers.default)
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

    private fun filterSalesmen(query: String) {

    }

    private fun observerForSalesmen() {
        viewModelScope.launch(dispatchers.main) {
            salesmanRepository
                .getSalesmen()
                .map { salesmen ->
                    salesmen.mapIndexed { index, salesman ->
                        SalesmanUiState(
                            id = index,
                            avatarText = salesman.name.first().uppercase(),
                            name = salesman.name,
                            isExpanded = false,
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