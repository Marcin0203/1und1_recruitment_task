package pl.marcin.malocha.salesmanapplication.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import pl.marcin.malocha.salesmanapplication.ui.screens.salesmanlist.SalesmanListScreen
import pl.marcin.malocha.salesmanapplication.ui.theme.SalesmanApplicationTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SalesmanApplicationTheme {
                SalesmanListScreen()
            }
        }
    }
}