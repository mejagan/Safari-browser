package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.BookmarkEntity
import com.example.model.PrivacyReport
import com.example.model.QuickShortcut
import com.example.model.WallpaperTheme
import com.example.model.WebTab
import com.example.ui.theme.DarkGlassBackground
import com.example.ui.theme.DeepDarkGradient
import com.example.ui.theme.DuskSandGradient
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.IceBlueGradient
import com.example.ui.theme.PineGreenGradient
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.liquidGlass
import com.example.ui.theme.liquidGlassCapsule

@Composable
fun NewTabHomeScreen(
    wallpaper: WallpaperTheme,
    shortcuts: List<QuickShortcut>,
    bookmarks: List<BookmarkEntity>,
    recentlyClosed: List<WebTab>,
    privacyReport: PrivacyReport,
    onNavigate: (String) -> Unit,
    onSearchClick: () -> Unit,
    onSelectWallpaper: (WallpaperTheme) -> Unit,
    onRestoreTab: () -> Unit,
    onOpenBookmarks: () -> Unit,
    onOpenHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundBrush = when (wallpaper) {
        WallpaperTheme.ICE_BLUE -> IceBlueGradient
        WallpaperTheme.DUSK_SAND -> DuskSandGradient
        WallpaperTheme.PINE_GREEN -> PineGreenGradient
        WallpaperTheme.DEEP_DARK -> DeepDarkGradient
    }

    val isDark = wallpaper == WallpaperTheme.DEEP_DARK || wallpaper == WallpaperTheme.PINE_GREEN
    val textColor = if (isDark) Color.White else Color(0xFF0F172A)
    val subtitleColor = if (isDark) Color(0xFFCBD5E1) else Color(0xFF64748B)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(brush = backgroundBrush)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Liquid Browser Hero Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(ElectricBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Public,
                        contentDescription = "Liquid Browser",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Liquid Browser",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "Chromium Engine • Liquid Glass Design",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = subtitleColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Central Floating Search Omnibar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .liquidGlassCapsule(
                        surfaceColor = if (isDark) Color(0x992B3444) else Color(0xD9FFFFFF),
                        borderColor = if (isDark) Color(0x40FFFFFF) else Color(0xB3FFFFFF),
                        borderWidth = 1.2.dp,
                        elevation = 4.dp
                    )
                    .clip(RoundedCornerShape(27.dp))
                    .clickable(onClick = onSearchClick)
                    .padding(horizontal = 18.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = ElectricBlue,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Search or enter website name",
                        color = subtitleColor,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Privacy Shield Status Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlass(
                        cornerRadius = 20.dp,
                        surfaceColor = if (isDark) Color(0x44FFFFFF) else Color(0x88FFFFFF),
                        borderHighlightColor = Color(0x66FFFFFF),
                        borderWidth = 1.dp
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(StatusGreen.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Shield,
                            contentDescription = "Shield",
                            tint = StatusGreen,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Liquid Guard MV3",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(StatusGreen.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "ACTIVE",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StatusGreen
                                )
                            }
                        }
                        Text(
                            text = "${privacyReport.totalTrackersBlocked} ads & trackers blocked",
                            fontSize = 12.sp,
                            color = subtitleColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Quick Shortcuts / Favorites
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "FAVORITES",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = subtitleColor,
                    letterSpacing = 0.6.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Shortcuts Grid (2 rows of 3)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                shortcuts.chunked(3).forEach { rowShortcuts ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowShortcuts.forEach { item ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .liquidGlass(
                                        cornerRadius = 18.dp,
                                        surfaceColor = if (isDark) Color(0x40FFFFFF) else Color(0xA6FFFFFF),
                                        borderHighlightColor = Color(0x80FFFFFF),
                                        borderWidth = 1.dp
                                    )
                                    .clip(RoundedCornerShape(18.dp))
                                    .clickable { onNavigate(item.url) }
                                    .padding(vertical = 12.dp, horizontal = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.9f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = item.title.take(1),
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ElectricBlue
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = item.title,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = textColor,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = item.category,
                                        fontSize = 10.sp,
                                        color = subtitleColor
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Pinned Bookmarks Section
            if (bookmarks.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "BOOKMARKS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = subtitleColor,
                        letterSpacing = 0.6.sp
                    )
                    Text(
                        text = "View All",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ElectricBlue,
                        modifier = Modifier.clickable(onClick = onOpenBookmarks)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .liquidGlass(
                            cornerRadius = 20.dp,
                            surfaceColor = if (isDark) Color(0x35FFFFFF) else Color(0x99FFFFFF),
                            borderHighlightColor = Color(0x66FFFFFF),
                            borderWidth = 1.dp
                        )
                ) {
                    bookmarks.take(4).forEachIndexed { index, bookmark ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigate(bookmark.url) }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFF9500),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = bookmark.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = textColor,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = bookmark.domain,
                                    fontSize = 11.sp,
                                    color = subtitleColor
                                )
                            }
                        }
                    }
                }
            }

            // Recently Closed Tab Restore Pill
            if (recentlyClosed.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .liquidGlassCapsule(
                            surfaceColor = if (isDark) Color(0x30FFFFFF) else Color(0x80FFFFFF),
                            borderColor = Color(0x60FFFFFF)
                        )
                        .clickable(onClick = onRestoreTab)
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = ElectricBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Reopen Closed Tab: \"${recentlyClosed.first().title}\"",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = textColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Wallpaper Switcher Pills
            Text(
                text = "WALLPAPER THEME",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = subtitleColor,
                letterSpacing = 0.6.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                WallpaperTheme.entries.forEach { theme ->
                    val isSelected = theme == wallpaper
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (isSelected) ElectricBlue else if (isDark) Color(0x40FFFFFF) else Color(0x80FFFFFF)
                            )
                            .clickable { onSelectWallpaper(theme) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = theme.displayName,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else textColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp)) // padding for bottom omnibar
        }
    }
}
