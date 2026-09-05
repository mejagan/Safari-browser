package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.liquidGlassCapsule

@Composable
fun LiquidOmnibar(
    domain: String,
    canGoBack: Boolean,
    isLoading: Boolean,
    tabCount: Int,
    isPrivate: Boolean,
    onBackClick: () -> Unit,
    onOmnibarClick: () -> Unit,
    onTabsClick: () -> Unit,
    onReloadClick: () -> Unit,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Leading Glass Orb: Back Button
        Box(
            modifier = Modifier
                .size(48.dp)
                .liquidGlassCapsule(
                    surfaceColor = Color(0x66FFFFFF),
                    borderColor = Color(0x80FFFFFF)
                )
                .clip(CircleShape)
                .clickable(
                    enabled = canGoBack,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onBackClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = if (canGoBack) Color(0xFF1E293B) else Color(0x401E293B),
                modifier = Modifier.size(20.dp)
            )
        }

        // Center Floating Liquid Glass Capsule
        Box(
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .liquidGlassCapsule(
                    surfaceColor = if (isPrivate) Color(0x992B3444) else Color(0xCCFFFFFF),
                    borderColor = if (isPrivate) Color(0x40FFFFFF) else Color(0x99FFFFFF)
                )
                .clip(RoundedCornerShape(24.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onOmnibarClick
                )
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Leading Tabs icon inside capsule with count badge
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onTabsClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Layers,
                        contentDescription = "Tabs Overview",
                        tint = if (isPrivate) Color.White else Color(0xFF334155),
                        modifier = Modifier.size(20.dp)
                    )
                    // Badge for tab count
                    if (tabCount > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(if (isPrivate) Color(0xFF94A3B8) else ElectricBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (tabCount > 9) "9+" else tabCount.toString(),
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Centered Domain Name & Security Icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (isPrivate) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Private",
                            tint = StatusGreen,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    } else if (domain != "New Tab" && domain.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Secure",
                            tint = StatusGreen,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }

                    Text(
                        text = domain.ifEmpty { "Search or type URL" },
                        color = if (isPrivate) Color.White else Color(0xFF1E293B),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Trailing Refresh / Stop Icon inside capsule
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onReloadClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isLoading) Icons.Default.Close else Icons.Default.Refresh,
                        contentDescription = if (isLoading) "Stop" else "Reload",
                        tint = if (isPrivate) Color(0xFFCBD5E1) else Color(0xFF475569),
                        modifier = Modifier.size(19.dp)
                    )
                }
            }
        }

        // Trailing Glass Orb: More Actions (...)
        Box(
            modifier = Modifier
                .size(48.dp)
                .liquidGlassCapsule(
                    surfaceColor = Color(0x66FFFFFF),
                    borderColor = Color(0x80FFFFFF)
                )
                .clip(CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onMoreClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.MoreHoriz,
                contentDescription = "Actions",
                tint = Color(0xFF1E293B),
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
