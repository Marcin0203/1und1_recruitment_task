package pl.marcin.malocha.salesmanapplication.ui.screens.salesmanlist

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.res.painterResource
import pl.marcin.malocha.salesmanapplication.R
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import pl.marcin.malocha.salesmanapplication.ui.theme.Grey999
import pl.marcin.malocha.salesmanapplication.ui.theme.GreyC6
import pl.marcin.malocha.salesmanapplication.ui.theme.White

sealed class SalesmanListIntent {
    data class QueryChanged(val value: TextFieldValue) : SalesmanListIntent()
    data class SalesmanClicked(val id: Int) : SalesmanListIntent()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesmanListScreen(
    viewModel: SalesmanListViewModel = viewModel<SalesmanListViewModel>()
) {
    val uiState = viewModel.state.collectAsStateWithLifecycle().value

    SalesmanListLayout(
        salesmen = uiState.salesmen,
        query = uiState.query,
        onQueryChange = {
            viewModel.send(SalesmanListIntent.QueryChanged(it))
        },
        onSalesmanClick = {
            viewModel.send(SalesmanListIntent.SalesmanClicked(it))
        }
    )
}

@Composable
private fun SalesmanListLayout(
    salesmen: List<SalesmanUiState> = emptyList(),
    query: TextFieldValue = TextFieldValue(""),
    onQueryChange: (TextFieldValue) -> Unit = {},
    onSalesmanClick: (Int) -> Unit = {}
) {
    Scaffold(
        topBar = { AppBar() }
    ) { innerPadding ->
        Content(
            modifier = Modifier.padding(innerPadding),
            query = query,
            onQueryChange = onQueryChange,
            salesmen = salesmen,
            onSalesmanClick = onSalesmanClick
        )
    }
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    salesmen: List<SalesmanUiState>,
    query: TextFieldValue,
    onQueryChange: (TextFieldValue) -> Unit,
    onSalesmanClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .then(modifier)
    ) {
        SearchInput(query = query, onQueryChange = onQueryChange)

        LazyColumn {
            items(items = salesmen, key = { it.id }) {
                SalesmanItem(
                    it,
                    onClick = { onSalesmanClick(it.id) }
                )
            }
        }
    }
}

@Composable
private fun SearchInput(
    query: TextFieldValue,
    onQueryChange: (TextFieldValue) -> Unit
) {
    BasicTextField(
        modifier = Modifier
            .padding(top = 24.dp, bottom = 32.dp, start = 16.dp, end = 16.dp)
            .fillMaxWidth()
            .shadow(elevation = 2.dp),
        textStyle = MaterialTheme.typography.bodyLarge,
        value = query,
        onValueChange = onQueryChange,
        singleLine = true,
    ) { innerTextField ->
        Row(
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = Icons.Filled.Search,
                tint = MaterialTheme.colorScheme.secondary,
                contentDescription = "search"
            )

            Box(
                modifier = Modifier.padding(start = 7.dp).weight(1f),
            ) {
                if (query.text.isEmpty()) {
                    Text(
                        text = stringResource(R.string.search_input_placeholder),
                        style = MaterialTheme.typography.bodyLarge.copy(color = Grey999)
                    )
                }

                innerTextField()
            }

            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = R.drawable.icon_mic),
                tint = MaterialTheme.colorScheme.secondary,
                contentDescription = "mic"
            )
        }
    }
}

@Composable
fun SalesmanItem(
    item: SalesmanUiState,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color = GreyC6.copy(alpha = 0.25f))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.avatarText,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Column(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .weight(1f)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 20.sp)
                )

                if (item.isExpanded) {
                    Text(
                        modifier = Modifier.padding(top = 3.dp),
                        text = item.areas,
                        style = MaterialTheme.typography.bodyMedium.copy(color = Grey999)
                    )
                }
            }

            Icon(
                imageVector = if (item.isExpanded) Icons.Default.KeyboardArrowDown else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Expand",
                tint = MaterialTheme.colorScheme.outline
            )
        }

        HorizontalDivider(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
            thickness = DividerDefaults.Thickness,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun AppBar() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(R.string.top_bar_title),
                style = MaterialTheme.typography.labelSmall.copy(color = White, lineHeight = 19.sp)
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
        )
    )
}

@Preview
@Composable
fun SalesmanListLayoutPreview() {
    SalesmanListLayout()
}

@Preview
@Composable
fun SalesmanListLayoutPreview_SearchValue() {
    SalesmanListLayout(
        query = TextFieldValue("PreviewQuery")
    )
}

@Preview
@Composable
fun SalesmanListLayoutPreview_Items() {
    SalesmanListLayout(
        salesmen = listOf(
            SalesmanUiState(
                id = 1,
                avatarText = "A",
                name = "A Preview Name",
                isExpanded = false,
                areas = "73133, 76131"
            ),
            SalesmanUiState(
                id = 2,
                avatarText = "B",
                name = "B Preview Name",
                isExpanded = true,
                areas = "731*, 76131"
            ),
            SalesmanUiState(
                id = 3,
                avatarText = "C",
                name = "C Preview Very Very Very Very Very Very Very Very Very Very Very Long Name",
                isExpanded = false,
                areas = "731*, 76131"
            ),
            SalesmanUiState(
                id = 4,
                avatarText = "D",
                name = "D Preview Name many areas",
                isExpanded = true,
                areas = "731*, 76131, 731*, 76131, 731*, 76131, 731*, 76131, 731*, 76131, 731*, 76131, 731*, 76131, 731*, 76131,"
            ),
        )
    )
}