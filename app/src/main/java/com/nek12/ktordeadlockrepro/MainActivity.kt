package com.nek12.ktordeadlockrepro

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.nek12.ktordeadlockrepro.network.BrewApi
import com.nek12.ktordeadlockrepro.network.paged
import com.nek12.ktordeadlockrepro.ui.theme.KtorDeadlockReproTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.cache
import kotlinx.coroutines.plus
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainActivity : ComponentActivity(), KoinComponent {

    val api by inject<BrewApi>()

    val data = Pager(
        PagingConfig(10, initialLoadSize = 30, enablePlaceholders = false),
        initialKey = 1,
        pagingSourceFactory = paged { page, pageSize -> api.getBrews(page, pageSize) }
    ).flow.cachedIn(lifecycleScope + Dispatchers.Default)

    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KtorDeadlockReproTheme {
                // A surface container using the 'background' color from the theme
                val items = data.collectAsLazyPagingItems()
                LazyColumn {
                    stickyHeader {
                        Button({
                            Log.i("Refresh", "Refresh")
                            items.refresh()
                        }) { Text("Refresh") }
                    }
                    pagingItems(
                        items,
                        key = { it.id },
                        localError = { Text(it.toString()) },
                        emptyView = { Text("Empty") },
                        remoteErrorHeader = { Text("Remote error: $it") },
                        item = { _, it -> Text(it.name, modifier = Modifier.padding(12.dp)) },
                    )
                }
            }
        }
    }
}
