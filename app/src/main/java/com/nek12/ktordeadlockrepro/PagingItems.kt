package com.nek12.ktordeadlockrepro

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey

@OptIn(ExperimentalFoundationApi::class)
fun <T : Any> LazyListScope.pagingItems(
    items: LazyPagingItems<T>,
    key: ((T) -> Any)?,
    localError: @Composable LazyItemScope.(Exception?) -> Unit,
    emptyView: @Composable LazyItemScope.() -> Unit,
    remoteErrorHeader: @Composable (Exception?) -> Unit,
    loadingView: @Composable LazyItemScope.() -> Unit = { CircularProgressIndicator() },
    header: @Composable LazyItemScope.() -> Unit = { },
    appendIndicator: @Composable LazyItemScope.() -> Unit = { CircularProgressIndicator(strokeCap = StrokeCap.Round) },
    placeholder: (@Composable LazyItemScope.(i: Int) -> Unit)? = null,
    item: @Composable LazyItemScope.(i: Int, it: T) -> Unit,
) {
    val refresh = items.loadState.refresh
    val source = items.loadState.source.refresh
    val remote = items.loadState.mediator?.refresh
    when {
         refresh is LoadState.Loading -> item {
            loadingView()
        }
        items.itemCount == 0 && refresh is LoadState.NotLoading -> item {
            emptyView()
        }
        source is LoadState.Error -> item {
            localError(source.error as? Exception)
        }
        else -> {
            if (remote is LoadState.Error) stickyHeader {
                remoteErrorHeader(remote.error as? Exception)
            }
            item {
                header()
            }
            items(
                count = items.itemCount,
                key = items.itemKey(key),
                contentType = items.itemContentType()
            ) { index ->
                val it = items[index]
                if (it == null) placeholder?.invoke(this, index) ?: return@items else item(index, it)
            }
            if (items.loadState.append is LoadState.Loading) item {
                Box(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.TopCenter,
                ) {
                    appendIndicator()
                }
            }
        }
    }
}
