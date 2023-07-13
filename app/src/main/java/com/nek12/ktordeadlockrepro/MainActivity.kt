package com.nek12.ktordeadlockrepro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.mikepenz.iconics.compose.IconicsConfig
import com.mikepenz.iconics.compose.Image
import com.mikepenz.iconics.typeface.library.fontawesome.FontAwesome
import com.nek12.ktordeadlockrepro.network.BrewApi
import com.nek12.ktordeadlockrepro.network.paged
import com.nek12.ktordeadlockrepro.ui.theme.KtorDeadlockReproTheme
import kotlinx.coroutines.Dispatchers
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

    @OptIn(ExperimentalLayoutApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KtorDeadlockReproTheme {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FlowRow {
                        repeat(10) {
                            var selected by remember { mutableStateOf(false) }
                            CutChip(
                                selected = selected,
                                onClick = { selected = !selected },
                            )
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CutChip(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FilterChip(
        modifier = modifier.padding(6.dp),
        leadingIcon = {
            AnimatedContent(selected) { isSelected ->
                val icon = if (isSelected) FontAwesome.Icon.faw_toggle_off else FontAwesome.Icon.faw_toggle_on
                Image(
                    asset = icon,
                    // without this - doesn't reproduce
                    // uncomment to get a reproducer
                    modifier = Modifier.requiredSizeIn(maxWidth = 20.dp, maxHeight = 20.dp).padding(4.dp),
                    alignment = Alignment.Center,
                    contentScale = ContentScale.Fit,
                    iconicsConfig = IconicsConfig(
                        respectFontBounds = false,
                        iconBrush = SolidColor(LocalContentColor.current),
                        paddingDp = 1,
                    ),
                )
            }
        },
        label = {
            Text(
                "Long text ".repeat(if (selected) 2 else 1),
                maxLines = 1,
                softWrap = false,
                modifier = Modifier.animateContentSize()
            )
        },
        onClick = onClick,
        selected = selected
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Preview
@Composable
fun CutChipPreview() {
    KtorDeadlockReproTheme {
        FlowRow {
            repeat(10) {
                var selected by remember { mutableStateOf(false) }
                CutChip(
                    selected = selected,
                    onClick = { selected = !selected },
                )
            }
        }
    }
}
