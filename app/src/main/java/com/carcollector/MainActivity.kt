package com.carcollector

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

data class LotItem(val title: String, val url: String)

data class UiState(
    val query: String = "",
    val isLoading: Boolean = true,
    val error: String? = null,
    val lots: List<LotItem> = emptyList()
)

class LotesViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    init {
        loadLots()
    }

    fun onQueryChanged(query: String) {
        _uiState.update { it.copy(query = query) }
    }

    private fun loadLots() {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                val baseUrl = "https://t-hunted.blogspot.com"
                val doc = Jsoup.connect("$baseUrl/p/lotes-hot-wheels.html")
                    .userAgent("Mozilla/5.0")
                    .get()

                doc.select("a")
                    .mapNotNull { el ->
                        val name = el.text().trim()
                        val href = el.absUrl("href")
                        if (name.isNotBlank() && href.contains("t-hunted.blogspot.com") && !href.endsWith("lotes-hot-wheels.html")) {
                            LotItem(name, href)
                        } else {
                            null
                        }
                    }
                    .distinctBy { it.url }
                    .sortedBy { it.title }
            }.onSuccess { lots ->
                _uiState.update { it.copy(isLoading = false, lots = lots, error = null) }
            }.onFailure { ex ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "No se pudieron cargar los lotes: ${ex.message}",
                        lots = emptyList()
                    )
                }
            }
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val vm = ViewModelProvider(this)[LotesViewModel::class.java]
            CarCollectorApp(vm)
        }
    }
}

@Composable
fun CarCollectorApp(vm: LotesViewModel) {
    val navController = rememberNavController()
    val state by vm.uiState.collectAsState()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            LotsScreen(
                state = state,
                onQueryChanged = vm::onQueryChanged,
                onOpenLot = { lotUrl ->
                    val encoded = URLEncoder.encode(lotUrl, StandardCharsets.UTF_8.toString())
                    navController.navigate("detail/$encoded")
                }
            )
        }
        composable(
            route = "detail/{lotUrl}",
            arguments = listOf(navArgument("lotUrl") { type = NavType.StringType })
        ) { backStackEntry ->
            val lotUrl = backStackEntry.arguments?.getString("lotUrl") ?: return@composable
            WebLotScreen(url = java.net.URLDecoder.decode(lotUrl, StandardCharsets.UTF_8.toString()))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LotsScreen(
    state: UiState,
    onQueryChanged: (String) -> Unit,
    onOpenLot: (String) -> Unit
) {
    val filtered = state.lots.filter {
        it.title.contains(state.query, ignoreCase = true)
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Hot Wheels - Lotes") }) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = state.query,
                onValueChange = onQueryChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Buscar lote") },
                singleLine = true
            )

            when {
                state.isLoading -> CircularProgressIndicator()
                state.error != null -> Text(
                    text = state.error,
                    color = MaterialTheme.colorScheme.error
                )
                filtered.isEmpty() -> Text("No se encontraron lotes con ese filtro.")
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(filtered) { lot ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOpenLot(lot.url) }
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(lot.title, fontWeight = FontWeight.SemiBold)
                                Text(lot.url, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebLotScreen(url: String) {
    Scaffold(topBar = { TopAppBar(title = { Text("Detalle del lote") }) }) { padding ->
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    webViewClient = WebViewClient()
                    loadUrl(url)
                }
            },
            update = { it.loadUrl(url) }
        )
    }
}
