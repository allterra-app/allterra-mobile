package com.allterra.presentation.feed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.allterra.presentation.common.model.ActivityTypeUi
import com.allterra.presentation.common.model.ActivityUiModel
import com.allterra.presentation.common.model.PostAudienceUi
import com.allterra.presentation.common.model.PostTypeUi
import com.allterra.presentation.common.model.handle
import com.allterra.presentation.common.model.initials
import com.allterra.presentation.localization.appStrings
import com.allterra.presentation.pois.PoiPhotoImage
import com.allterra.presentation.theme.AllterraTheme

private enum class FeedTab {
    ALL, FRIENDS, CLUBS, DISCOVER
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    viewModel: FeedViewModel,
    onCreatePost: () -> Unit,
    onOpenPost: (String) -> Unit,
    onOpenNotifications: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val strings = appStrings()
    val shareLauncher = rememberPostShareLauncher()
    var selectedTab by remember { mutableIntStateOf(0) }
    var searchVisible by remember { mutableStateOf(false) }
    var filterVisible by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.refresh(isManual = false)
    }

    val filteredItems = remember(state.activities, selectedTab, state.selectedPostTypes, state.selectedActivityTypes, searchQuery) {
        state.activities
            .filter { activity -> activity.matchesTab(FeedTab.entries[selectedTab]) }
            .filter { activity ->
                if (state.selectedPostTypes.isEmpty()) true else activity.type in state.selectedPostTypes
            }
            .filter { activity ->
                if (state.selectedActivityTypes.isEmpty()) true else activity.activity in state.selectedActivityTypes
            }
            .filter { activity ->
                val query = searchQuery.trim()
                if (query.isBlank()) true else activity.matchesQuery(query)
            }
    }

    PullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh = { viewModel.refresh(isManual = true) },
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .background(AllterraTheme.colors.bg),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 108.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                FeedTopBar(
                    hasUnreadNotifications = !state.notificationsSeen && state.notifications.any { !it.read },
                    onSearchClick = { searchVisible = !searchVisible },
                    onFilterClick = { filterVisible = !filterVisible },
                    onNotificationsClick = {
                        onOpenNotifications()
                        viewModel.onNotificationsOpened()
                    },
                    onCreatePost = onCreatePost,
                )
            }

            if (searchVisible) {
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                            viewModel.clearActionError()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text(strings.feedSearchPlaceholder) },
                    )
                }
            }

            item {
                AnimatedVisibility(
                    visible = filterVisible,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    FeedFilterSection(
                        selectedPostTypes = state.selectedPostTypes,
                        selectedActivityTypes = state.selectedActivityTypes,
                        onPostTypeToggle = viewModel::togglePostTypeFilter,
                        onActivityTypeToggle = viewModel::toggleActivityTypeFilter,
                        onClear = viewModel::clearFilters,
                    )
                }
            }

            item {
                FeedTabs(
                    selectedTab = FeedTab.entries[selectedTab],
                    onSelect = { selectedTab = it.ordinal },
                )
            }

            state.loadError?.let { error ->
                item {
                    FeedNoticeCard(title = error, body = "")
                }
            }

            state.actionError?.let { error ->
                item {
                    FeedNoticeCard(title = error, body = "")
                }
            }

            if (state.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 28.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(color = AllterraTheme.categorical.social.color)
                    }
                }
            }

            if (!state.isLoading && filteredItems.isEmpty()) {
                item {
                    FeedNoticeCard(
                        title = strings.feedEmptyTitle,
                        body = if (searchQuery.isBlank()) strings.feedEmptyBody else strings.feedSearchPlaceholder,
                    )
                }
            }

            items(filteredItems, key = { it.id }) { item ->
                FeedPostCard(
                    item = item,
                    onToggleLike = viewModel::toggleLike,
                    onToggleBookmark = viewModel::toggleBookmark,
                    onShare = { shareLauncher.share(item.title, item.description) },
                    onOpen = onOpenPost,
                )
            }

            if (state.hasNextPage && searchQuery.isBlank()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (state.isLoadingMore) {
                            CircularProgressIndicator(color = AllterraTheme.categorical.social.color)
                        } else {
                            TextButton(onClick = viewModel::loadMore) {
                                Text(
                                    text = strings.feedLoadMoreAction,
                                    color = AllterraTheme.categorical.social.color,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FeedTopBar(
    hasUnreadNotifications: Boolean,
    onSearchClick: () -> Unit,
    onFilterClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onCreatePost: () -> Unit,
) {
    val strings = appStrings()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = strings.feedTitle,
                style = AllterraTheme.typography.displayM,
                color = AllterraTheme.colors.ink,
            )
            Text(
                text = "${strings.feedTabFriends.lowercase()} · ${strings.feedTabClubs.lowercase()} · ${strings.feedTabDiscover.lowercase()}",
                style = AllterraTheme.typography.small,
                color = AllterraTheme.colors.muted,
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FeedToolbarButton(icon = Icons.Outlined.Search, onClick = onSearchClick)
            FeedToolbarButton(icon = Icons.Outlined.Tune, onClick = onFilterClick)
            Box {
                FeedToolbarButton(
                    icon = Icons.Outlined.NotificationsNone,
                    onClick = onNotificationsClick,
                )
                if (hasUnreadNotifications) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 6.dp, end = 6.dp)
                            .size(9.dp)
                            .clip(CircleShape)
                            .background(AllterraTheme.colors.crimson),
                    )
                }
            }
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(AllterraTheme.categorical.social.color)
                    .clickable { onCreatePost() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

@Composable
private fun FeedToolbarButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    highlighted: Boolean = false,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(AllterraTheme.colors.surface)
            .border(1.dp, AllterraTheme.colors.line2, RoundedCornerShape(14.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (highlighted) AllterraTheme.categorical.social.color else AllterraTheme.colors.ink2,
            modifier = Modifier.size(18.dp),
        )
    }
}

@Composable
private fun FeedFilterSection(
    selectedPostTypes: Set<PostTypeUi>,
    selectedActivityTypes: Set<ActivityTypeUi>,
    onPostTypeToggle: (PostTypeUi) -> Unit,
    onActivityTypeToggle: (ActivityTypeUi) -> Unit,
    onClear: () -> Unit,
) {
    val strings = appStrings()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AllterraTheme.radius.lg))
            .background(AllterraTheme.colors.surface)
            .border(1.dp, AllterraTheme.colors.line, RoundedCornerShape(AllterraTheme.radius.lg))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = strings.feedFilterTitle, style = AllterraTheme.typography.bodyStrong)
            Text(
                text = strings.feedFilterClear,
                style = AllterraTheme.typography.small,
                color = AllterraTheme.colors.terra,
                modifier = Modifier.clickable { onClear() }
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = strings.postTypeLabel, style = AllterraTheme.typography.caption, color = AllterraTheme.colors.muted)
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PostTypeUi.entries.forEach { type ->
                    val selected = type in selectedPostTypes
                    FilterChip(
                        selected = selected,
                        onClick = { onPostTypeToggle(type) },
                        label = { Text(type.label(strings)) }
                    )
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = strings.postActivityLabel, style = AllterraTheme.typography.caption, color = AllterraTheme.colors.muted)
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ActivityTypeUi.entries.forEach { activity ->
                    val selected = activity in selectedActivityTypes
                    FilterChip(
                        selected = selected,
                        onClick = { onActivityTypeToggle(activity) },
                        label = { Text(activity.label(strings)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FeedTabs(
    selectedTab: FeedTab,
    onSelect: (FeedTab) -> Unit,
) {
    val strings = appStrings()
    val tabs = listOf(
        FeedTab.ALL to strings.feedTabAll,
        FeedTab.FRIENDS to strings.feedTabFriends,
        FeedTab.CLUBS to strings.feedTabClubs,
        FeedTab.DISCOVER to strings.feedTabDiscover,
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(AllterraTheme.colors.surface2)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        tabs.forEach { (tab, title) ->
            val selected = tab == selectedTab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (selected) AllterraTheme.colors.surface else Color.Transparent)
                    .clickable { onSelect(tab) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = title,
                    style = AllterraTheme.typography.bodyStrong,
                    color = if (selected) AllterraTheme.colors.ink else AllterraTheme.colors.muted,
                    maxLines = 1,
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FeedPostCard(
    item: ActivityUiModel,
    onToggleLike: (String) -> Unit,
    onToggleBookmark: (String) -> Unit,
    onShare: () -> Unit,
    onOpen: (String) -> Unit,
) {
    val strings = appStrings()
    val social = AllterraTheme.categorical.social
    val pageCount = if (item.photoUris.isNotEmpty()) item.photoUris.size else 1
    val pagerState = rememberPagerState(pageCount = { pageCount })

    Card(
        shape = RoundedCornerShape(22.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen(item.id) },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(AllterraTheme.colors.surface)
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFC96F45)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = item.author.initials(),
                            style = AllterraTheme.typography.smallStrong,
                            color = Color.White,
                        )
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
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
                }
                IconButton(
                    onClick = { onOpen(item.id) },
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AllterraTheme.colors.surface2),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.MoreHoriz,
                        contentDescription = null,
                        tint = AllterraTheme.colors.muted,
                    )
                }
            }

            FeedMetaRow(item = item, strings = strings)

            if (item.title.isNotBlank()) {
                Text(
                    text = item.title,
                    style = AllterraTheme.typography.title,
                    color = AllterraTheme.colors.ink,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(182.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(AllterraTheme.colors.skySoft),
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                ) { page ->
                    if (item.photoUris.isNotEmpty()) {
                        PoiPhotoImage(source = item.photoUris[page], modifier = Modifier.fillMaxSize())
                    } else {
                        FeedPostFallback(item = item)
                    }
                }

                if (pageCount > 1) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                    ) {
                        repeat(pageCount) { index ->
                            Box(
                                modifier = Modifier
                                    .size(if (index == pagerState.currentPage) 7.dp else 6.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (index == pagerState.currentPage) Color.White else Color.White.copy(alpha = 0.45f)
                                    )
                            )
                        }
                    }
                }
            }

            if (item.description.isNotBlank()) {
                Text(
                    text = item.description,
                    style = AllterraTheme.typography.body,
                    color = AllterraTheme.colors.ink2,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                    FeedStatButton(
                        icon = if (item.liked) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                        label = item.likeCount.takeIf { it > 0 }?.toString() ?: "",
                        tint = if (item.liked) AllterraTheme.colors.terra else AllterraTheme.colors.ink2,
                        onClick = { onToggleLike(item.id) },
                    )
                    FeedStatButton(
                        icon = Icons.Outlined.ChatBubbleOutline,
                        label = "",
                        tint = AllterraTheme.colors.ink2,
                        onClick = { onOpen(item.id) },
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    IconButton(onClick = { onToggleBookmark(item.id) }, modifier = Modifier.size(24.dp)) {
                        Icon(
                            imageVector = if (item.bookmarked) Icons.Outlined.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = null,
                            tint = if (item.bookmarked) social.color else AllterraTheme.colors.ink2,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                    IconButton(onClick = onShare, modifier = Modifier.size(24.dp)) {
                        Icon(
                            imageVector = Icons.Outlined.Share,
                            contentDescription = null,
                            tint = AllterraTheme.colors.ink2,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FeedMetaRow(item: ActivityUiModel, strings: com.allterra.presentation.localization.AppStrings) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FeedMetaPill(label = item.audience.label(strings))
        item.tripId?.let { FeedMetaPill(label = strings.feedPostTripLabel, icon = Icons.Outlined.Link) }
        item.routeId?.let { FeedMetaPill(label = strings.feedPostRouteLabel, icon = Icons.Outlined.Map) }
    }
}

@Composable
private fun FeedMetaPill(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(AllterraTheme.colors.surface2)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AllterraTheme.colors.ink2,
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
private fun FeedPostFallback(item: ActivityUiModel) {
    val strings = appStrings()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AllterraTheme.colors.surface2)
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = item.title.ifBlank { item.author },
                style = AllterraTheme.typography.title,
                color = AllterraTheme.colors.ink,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            if (item.description.isNotBlank()) {
                Text(
                    text = item.description,
                    style = AllterraTheme.typography.body,
                    color = AllterraTheme.colors.ink2,
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            if (item.tripId != null) {
                FeedMetaPill(label = strings.feedPostTripLabel, icon = Icons.Outlined.Link)
            }
            if (item.routeId != null) {
                FeedMetaPill(label = strings.feedPostRouteLabel, icon = Icons.Outlined.Map)
            }
        }
    }
}

@Composable
private fun FeedStatButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tint: Color,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier.clickable { onClick() },
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(18.dp),
        )
        Text(
            text = label,
            style = AllterraTheme.typography.smallStrong,
            color = tint,
        )
    }
}

@Composable
private fun FeedNoticeCard(
    title: String,
    body: String,
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(AllterraTheme.colors.surface)
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = title,
                style = AllterraTheme.typography.title,
                color = AllterraTheme.colors.ink,
            )
            if (body.isNotBlank()) {
                Text(
                    text = body,
                    style = AllterraTheme.typography.body,
                    color = AllterraTheme.colors.muted,
                )
            }
        }
    }
}

private fun ActivityUiModel.matchesTab(tab: FeedTab): Boolean {
    return when (tab) {
        FeedTab.ALL -> true
        FeedTab.FRIENDS -> audience == PostAudienceUi.FRIENDS
        FeedTab.CLUBS -> audience == PostAudienceUi.CLUB
        FeedTab.DISCOVER -> audience == PostAudienceUi.PUBLIC
    }
}

private fun ActivityUiModel.matchesQuery(query: String): Boolean {
    val normalized = query.trim().lowercase()
    return title.lowercase().contains(normalized)
        || description.lowercase().contains(normalized)
        || author.lowercase().contains(normalized)
}

private fun PostAudienceUi.label(strings: com.allterra.presentation.localization.AppStrings): String {
    return when (this) {
        PostAudienceUi.PUBLIC -> strings.postAudiencePublic
        PostAudienceUi.FRIENDS -> strings.postAudienceFriends
        PostAudienceUi.CLUB -> strings.postAudienceClub
    }
}

private fun PostTypeUi.label(strings: com.allterra.presentation.localization.AppStrings): String {
    return when (this) {
        PostTypeUi.CHECK_IN -> strings.postTypeCheckIn
        PostTypeUi.ROUTE -> strings.postTypeRoute
        PostTypeUi.NOTE -> strings.postTypeNote
        PostTypeUi.GEAR -> strings.postTypeGear
    }
}

private fun ActivityTypeUi.label(strings: com.allterra.presentation.localization.AppStrings): String {
    return when (this) {
        ActivityTypeUi.HIKE -> strings.feedChipHike
        ActivityTypeUi.BIKEPACKING -> strings.feedChipBikepacking
        ActivityTypeUi.ALPINISM -> strings.feedChipClimb
        ActivityTypeUi.TREK -> strings.feedChipTrek
        ActivityTypeUi.CLIMB -> strings.feedChipClimbing
    }
}
