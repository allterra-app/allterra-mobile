package com.allterra.presentation.feed

import allterra.composeapp.generated.resources.Res
import allterra.composeapp.generated.resources.allterra_text_logo
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.allterra.presentation.common.components.cards.ActivityCard
import com.allterra.presentation.localization.appStrings
import org.jetbrains.compose.resources.painterResource

@Composable
fun FeedScreen(
    viewModel: FeedViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val strings = appStrings()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0A7DAF), Color(0xFFC2C7CC))
                )
            ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF0A7DAF), Color(0xFF79A9C2))
                    )
                )
                .padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
            androidx.compose.foundation.Image(
                painter = painterResource(Res.drawable.allterra_text_logo),
                contentDescription = strings.appName,
                modifier = Modifier.size(width = 150.dp, height = 42.dp),
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (state.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 14.dp),
                        contentAlignment = androidx.compose.ui.Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            state.loadError?.let { error ->
                item {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = 6.dp),
                    )
                }
            }

            items(state.activities, key = { it.id }) { item ->
                ActivityCard(
                    item = item,
                    strings = strings,
                    onToggleLike = viewModel::toggleLike,
                    onToggleBookmark = viewModel::toggleBookmark,
                    onOpen = {},
                )
            }
        }
    }
}
