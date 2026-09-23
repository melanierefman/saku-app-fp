package com.example.saku.app.core.ui.components.card

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saku.app.ui.theme.Warning
import com.example.saku.app.ui.theme.Warning0
import com.example.saku.app.ui.theme.Warning80
import com.composables.icons.lucide.CircleAlert
import com.composables.icons.lucide.Lucide

@Composable
fun WarningCalloutCard(
    modifier: Modifier = Modifier,
    title: String? = null,
    message: String,
    icon: ImageVector = Lucide.CircleAlert
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Warning0)
            .border(1.dp, Warning.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Warning80,
                modifier = Modifier
                    .size(18.dp)
                    .offset(y = 1.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                if (!title.isNullOrBlank()) {
                    Text(
                        text = title,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Warning80
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                }
                Text(
                    text = message,
                    fontSize = 11.5.sp,
                    color = Warning80.copy(alpha = 0.95f),
                    lineHeight = 16.5.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
