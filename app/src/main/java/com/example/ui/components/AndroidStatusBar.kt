package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.StatusGreen

@Composable
fun AndroidStatusBar(
    isDarkText: Boolean = true,
    isPrivateMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    val contentColor = if (isDarkText) Color(0xFF1F2937) else Color.White

    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(28.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left: Android Clock
        Text(
            text = "9:41",
            color = contentColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.sp
        )

        // Center: Android Camera Cutout & Privacy Indicator
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(9.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0F172A))
            )

            AnimatedVisibility(
                visible = isPrivateMode,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(StatusGreen)
                    )
                }
            }
        }

        // Right: Status Icons (5G, Wi-Fi, Battery)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "5G",
                color = contentColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )

            // Wi-Fi glyph circle
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(contentColor.copy(alpha = 0.85f))
            )

            // Battery pill with level indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .width(20.dp)
                    .height(10.dp)
                    .clip(CircleShape)
                    .background(contentColor.copy(alpha = 0.25f))
                    .padding(1.5.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(7.dp)
                        .clip(CircleShape)
                        .background(contentColor)
                )
            }
        }
    }
}
