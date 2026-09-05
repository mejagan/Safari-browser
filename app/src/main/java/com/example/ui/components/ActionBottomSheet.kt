package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PrivacyReport
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.liquidGlass

@Composable
fun ActionBottomSheet(
    url: String,
    isDesktopMode: Boolean,
    privacyReport: PrivacyReport,
    onDismiss: () -> Unit,
    onCopyLink: () -> Unit,
    onShare: () -> Unit,
    onAddBookmark: () -> Unit,
    onToggleDesktop: () -> Unit,
    onOpenBookmarks: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenDownloads: () -> Unit,
    onOpenExtensions: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .liquidGlass(
                cornerRadius = 28.dp,
                surfaceColor = Color(0xF7FFFFFF),
                borderHighlightColor = Color(0x99FFFFFF),
                borderWidth = 1.dp
            )
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Drag Handle
            Box(
                modifier = Modifier
                    .size(width = 40.dp, height = 5.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFFCBD5E1))
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Current URL Header with Lock Indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF1F5F9))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Encrypted Connection",
                    tint = StatusGreen,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = url,
                    color = Color(0xFF1E293B),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f),
                    maxLines = 1
                )
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onDismiss),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Actions Group 1: Sharing & Bookmarking
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0x80FFFFFF))
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    ActionRow(
                        icon = Icons.Default.Star,
                        title = "Add to Bookmarks",
                        subtitle = "Save page for quick access",
                        tint = Color(0xFFFF9500),
                        onClick = {
                            onAddBookmark()
                            onDismiss()
                        }
                    )
                    HorizontalDivider(color = Color(0x15000000), modifier = Modifier.padding(start = 56.dp))
                    ActionRow(
                        icon = Icons.Default.Share,
                        title = "Share Page",
                        subtitle = "Send link to apps and contacts",
                        tint = ElectricBlue,
                        onClick = {
                            onShare()
                            onDismiss()
                        }
                    )
                    HorizontalDivider(color = Color(0x15000000), modifier = Modifier.padding(start = 56.dp))
                    ActionRow(
                        icon = Icons.Default.ContentCopy,
                        title = "Copy URL",
                        subtitle = "Copy link to system clipboard",
                        onClick = {
                            onCopyLink()
                            onDismiss()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Actions Group 2: Desktop Mode & Extensions
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0x80FFFFFF))
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Desktop site toggle row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onToggleDesktop)
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF64748B).copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DesktopWindows,
                                contentDescription = null,
                                tint = Color(0xFF334155),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Request Desktop Website",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = if (isDesktopMode) "Currently in Desktop View" else "Currently in Mobile View",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                        Switch(
                            checked = isDesktopMode,
                            onCheckedChange = { onToggleDesktop() },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = ElectricBlue)
                        )
                    }

                    HorizontalDivider(color = Color(0x15000000), modifier = Modifier.padding(start = 56.dp))

                    ActionRow(
                        icon = Icons.Default.Extension,
                        title = "Manifest V3 Extensions",
                        subtitle = "Liquid Guard MV3 active (${privacyReport.totalTrackersBlocked} blocked)",
                        tint = ElectricBlue,
                        onClick = {
                            onOpenExtensions()
                            onDismiss()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Actions Group 3: Library & Settings
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0x80FFFFFF))
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    ActionRow(
                        icon = Icons.Default.BookmarkBorder,
                        title = "Bookmarks Library",
                        subtitle = "View and manage saved bookmarks",
                        onClick = {
                            onOpenBookmarks()
                            onDismiss()
                        }
                    )
                    HorizontalDivider(color = Color(0x15000000), modifier = Modifier.padding(start = 56.dp))
                    ActionRow(
                        icon = Icons.Default.History,
                        title = "Browsing History",
                        subtitle = "Visited pages and search log",
                        onClick = {
                            onOpenHistory()
                            onDismiss()
                        }
                    )
                    HorizontalDivider(color = Color(0x15000000), modifier = Modifier.padding(start = 56.dp))
                    ActionRow(
                        icon = Icons.Default.Download,
                        title = "Downloads",
                        subtitle = "Downloaded files and documents",
                        onClick = {
                            onOpenDownloads()
                            onDismiss()
                        }
                    )
                    HorizontalDivider(color = Color(0x15000000), modifier = Modifier.padding(start = 56.dp))
                    ActionRow(
                        icon = Icons.Default.Settings,
                        title = "Browser Settings",
                        subtitle = "Search engine, privacy, cache & cookies",
                        tint = Color(0xFF475569),
                        onClick = {
                            onOpenSettings()
                            onDismiss()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun ActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    tint: Color = Color(0xFF334155),
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = tint,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF0F172A)
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color(0xFF64748B)
            )
        }
    }
}
