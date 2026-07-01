package com.allterra.presentation.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.allterra.presentation.common.model.NotificationUiModel
import com.allterra.presentation.localization.appStrings
import com.allterra.presentation.theme.AllterraTheme

@Composable
fun NotificationsScreen(
    items: List<NotificationUiModel>,
    isLoading: Boolean,
    onBack: () -> Unit,
    onOpen: (String) -> Unit,
) {
    val strings = appStrings()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .background(AllterraTheme.colors.bg)
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = null,
                    tint = AllterraTheme.colors.ink,
                )
            }
            Text(
                text = strings.notificationsTitle,
                style = AllterraTheme.typography.displayM,
                color = AllterraTheme.colors.ink,
            )
        }

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = AllterraTheme.categorical.social.color)
            }
            return
        }

        if (items.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = strings.notificationsEmptyTitle,
                    style = AllterraTheme.typography.title,
                    color = AllterraTheme.colors.ink,
                )
                Text(
                    text = strings.notificationsEmptyBody,
                    style = AllterraTheme.typography.body,
                    color = AllterraTheme.colors.muted,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
            return
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(items, key = { it.id }) { item ->
                NotificationCard(
                    item = item,
                    onOpen = { onOpen(item.id) },
                )
            }
        }
    }
}

@Composable
private fun NotificationCard(
    item: NotificationUiModel,
    onOpen: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (item.read) AllterraTheme.colors.surface else Color.White,
                shape = RoundedCornerShape(18.dp),
            )
            .clickable { onOpen() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .background(
                    color = if (item.read) AllterraTheme.colors.line2 else AllterraTheme.categorical.social.color,
                    shape = CircleShape,
                )
                .padding(5.dp),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = item.title,
                style = AllterraTheme.typography.bodyStrong,
                color = AllterraTheme.colors.ink,
            )
            if (item.body.isNotBlank()) {
                Text(
                    text = item.body,
                    style = AllterraTheme.typography.body,
                    color = AllterraTheme.colors.ink2,
                )
            }
            Text(
                text = item.createdAt,
                style = AllterraTheme.typography.small,
                color = AllterraTheme.colors.muted,
            )
        }
    }
}
