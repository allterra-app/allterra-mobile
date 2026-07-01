package com.allterra.presentation.post

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.allterra.presentation.common.model.ActivityUiModel
import com.allterra.presentation.common.model.PostAudienceUi
import com.allterra.presentation.common.model.handle
import com.allterra.presentation.common.model.initials
import com.allterra.presentation.localization.appStrings
import com.allterra.presentation.pois.PoiPhotoImage
import com.allterra.presentation.theme.AllterraTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PostViewerScreen(
    item: ActivityUiModel,
    tripTitle: String?,
    routeTitle: String?,
    onBack: () -> Unit,
    onToggleLike: (String) -> Unit,
    onToggleBookmark: (String) -> Unit,
    onShare: () -> Unit,
) {
    val strings = appStrings()
    val categorical = AllterraTheme.categorical.social
    val pageCount = if (item.photoUris.isNotEmpty()) item.photoUris.size else 1
    val pagerState = rememberPagerState(pageCount = { pageCount })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .background(AllterraTheme.colors.bg)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AllterraTheme.colors.surface)
                    .border(1.dp, AllterraTheme.colors.line2, RoundedCornerShape(12.dp))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = null,
                    tint = AllterraTheme.colors.ink,
                )
            }

            IconButton(
                onClick = { /* TODO: More options */ },
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AllterraTheme.colors.surface)
                    .border(1.dp, AllterraTheme.colors.line2, RoundedCornerShape(12.dp))
            ) {
                Icon(
                    imageVector = Icons.Outlined.MoreHoriz,
                    contentDescription = null,
                    tint = AllterraTheme.colors.ink,
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Hero Section (Photos)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(AllterraTheme.colors.skySoft)
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                ) { page ->
                    if (item.photoUris.isNotEmpty()) {
                        PoiPhotoImage(source = item.photoUris[page], modifier = Modifier.fillMaxSize())
                    } else {
                        PostViewerFallback(item = item)
                    }
                }

                if (pageCount > 1) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp)
                            .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(999.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        repeat(pageCount) { index ->
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (index == pagerState.currentPage) Color.White else Color.White.copy(alpha = 0.5f)
                                    )
                            )
                        }
                    }
                }
            }

            // Author Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFC96F45)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = item.author.initials(),
                        style = AllterraTheme.typography.bodyStrong,
                        color = Color.White,
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.author,
                        style = AllterraTheme.typography.bodyStrong,
                        color = AllterraTheme.colors.ink,
                    )
                    Text(
                        text = "@${item.author.handle()} · ${item.addedAt}",
                        style = AllterraTheme.typography.small,
                        color = AllterraTheme.colors.muted,
                    )
                }
                AllterraMetaPill(label = item.audience.label(strings))
            }

            // Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (item.title.isNotBlank()) {
                    Text(
                        text = item.title,
                        style = AllterraTheme.typography.displayM,
                        color = AllterraTheme.colors.ink,
                    )
                }

                if (item.description.isNotBlank()) {
                    Text(
                        text = item.description,
                        style = AllterraTheme.typography.body,
                        color = AllterraTheme.colors.ink2,
                        lineHeight = AllterraTheme.typography.body.lineHeight * 1.2f
                    )
                }

                if (tripTitle != null || routeTitle != null) {
                    Row(
                        modifier = Modifier.padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        tripTitle?.let {
                            AllterraMetaPill(label = it, icon = Icons.Outlined.Link)
                        }
                        routeTitle?.let {
                            AllterraMetaPill(label = it, icon = Icons.Outlined.Map)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = AllterraTheme.colors.line2, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))

            // Reaction Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    PostStatButton(
                        icon = if (item.liked) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                        label = item.likeCount.takeIf { it > 0 }?.toString() ?: "",
                        tint = if (item.liked) AllterraTheme.colors.terra else AllterraTheme.colors.ink2,
                        onClick = { onToggleLike(item.id) },
                    )
                    PostStatButton(
                        icon = Icons.Outlined.ChatBubbleOutline,
                        label = "",
                        tint = AllterraTheme.colors.ink2,
                        onClick = { /* TODO: Open comments */ },
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    IconButton(onClick = { onToggleBookmark(item.id) }, modifier = Modifier.size(24.dp)) {
                        Icon(
                            imageVector = if (item.bookmarked) Icons.Outlined.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = null,
                            tint = if (item.bookmarked) categorical.color else AllterraTheme.colors.ink2,
                            modifier = Modifier.size(22.dp),
                        )
                    }
                    IconButton(onClick = onShare, modifier = Modifier.size(24.dp)) {
                        Icon(
                            imageVector = Icons.Outlined.Share,
                            contentDescription = null,
                            tint = AllterraTheme.colors.ink2,
                            modifier = Modifier.size(22.dp),
                        )
                    }
                }
            }

            // Comments placeholder
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = strings.postNoCommentsTitle,
                    style = AllterraTheme.typography.small,
                    color = AllterraTheme.colors.muted
                )
                Text(
                    text = strings.postNoCommentsBody,
                    style = AllterraTheme.typography.caption,
                    color = AllterraTheme.colors.muted2
                )
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun PostStatButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tint: Color,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier.clickable { onClick() },
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(20.dp),
        )
        Text(
            text = label,
            style = AllterraTheme.typography.bodyStrong,
            color = tint,
        )
    }
}

@Composable
private fun AllterraMetaPill(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(AllterraTheme.colors.surface2)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AllterraTheme.colors.muted,
                modifier = Modifier.size(14.dp),
            )
        }
        Text(
            text = label,
            style = AllterraTheme.typography.caption,
            color = AllterraTheme.colors.ink2,
        )
    }
}

@Composable
private fun PostViewerFallback(item: ActivityUiModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AllterraTheme.colors.surface2)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Map,
            contentDescription = null,
            tint = AllterraTheme.colors.muted2,
            modifier = Modifier.size(64.dp)
        )
    }
}

private fun PostAudienceUi.label(strings: com.allterra.presentation.localization.AppStrings): String {
    return when (this) {
        PostAudienceUi.PUBLIC -> strings.postAudiencePublic
        PostAudienceUi.FRIENDS -> strings.postAudienceFriends
        PostAudienceUi.CLUB -> strings.postAudienceClub
    }
}
