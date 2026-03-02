package com.allterra.presentation.common.components.cards

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.allterra.presentation.common.model.ActivityUiModel
import com.allterra.presentation.localization.AppStrings
import com.allterra.presentation.pois.PoiPhotoImage

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ActivityCard(
    item: ActivityUiModel,
    strings: AppStrings,
    modifier: Modifier = Modifier,
    onToggleLike: (String) -> Unit,
    onToggleBookmark: (String) -> Unit,
    onOpen: (String) -> Unit,
) {
    val fallbackSlides = remember {
        listOf(
            listOf(Color(0xFF74A8C7), Color(0xFF9DD1CF)),
            listOf(Color(0xFF3B7C8F), Color(0xFF8AB96D)),
            listOf(Color(0xFF5A89A8), Color(0xFFC3D0DB)),
        )
    }
    val pageCount = if (item.photoUris.isNotEmpty()) item.photoUris.size else fallbackSlides.size
    val pagerState = rememberPagerState(pageCount = { pageCount })

    Card(modifier = modifier.clickable { onOpen(item.id) }) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                ) { page ->
                    if (item.photoUris.isNotEmpty()) {
                        PoiPhotoImage(
                            source = item.photoUris[page],
                            modifier = Modifier.fillMaxSize(),
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Brush.linearGradient(fallbackSlides[page])),
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0x770A1B24))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = item.title,
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                if (pageCount > 1) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        repeat(pageCount) { index ->
                            Box(
                                modifier = Modifier
                                    .size(if (index == pagerState.currentPage) 7.dp else 6.dp)
                                    .background(
                                        color = if (index == pagerState.currentPage) Color.White else Color.White.copy(alpha = 0.45f),
                                        shape = CircleShape,
                                    ),
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomEnd,
                ) {
                    Text(
                        text = item.addedAt,
                        color = Color.White.copy(alpha = 0.82f),
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.End,
                        modifier = Modifier.padding(8.dp),
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp, Alignment.End),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButtonResource(
                    icon = if (item.liked) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                    tint = if (item.liked) Color(0xFF0B7A74) else Color(0xFF6CB3AF),
                    onClick = { onToggleLike(item.id) },
                )
                IconButtonResource(
                    icon = Icons.Outlined.BookmarkBorder,
                    tint = if (item.bookmarked) Color(0xFF0B7A74) else Color(0xFF6CB3AF),
                    onClick = { onToggleBookmark(item.id) },
                )
                IconButtonResource(
                    icon = Icons.Outlined.ChatBubbleOutline,
                    tint = Color(0xFF6CB3AF),
                    onClick = {},
                )
            }

            Text(
                text = item.description,
                modifier = Modifier.padding(horizontal = 12.dp),
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = strings.cardMore,
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .clickable { onOpen(item.id) },
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun IconButtonResource(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(34.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(18.dp),
        )
    }
}
