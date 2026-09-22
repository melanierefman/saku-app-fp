package com.example.saku.app.core.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.CircleAlert
import com.composables.icons.lucide.Lucide
import com.example.saku.app.ui.theme.Border
import com.example.saku.app.ui.theme.Error
import com.example.saku.app.ui.theme.Error0
import com.example.saku.app.ui.theme.Error70
import com.example.saku.app.ui.theme.Neutral0
import com.example.saku.app.ui.theme.Neutral20
import com.example.saku.app.ui.theme.Neutral40
import com.example.saku.app.ui.theme.Neutral60
import com.example.saku.app.ui.theme.Primary
import com.example.saku.app.ui.theme.Primary0
import com.example.saku.app.ui.theme.Primary60
import com.example.saku.app.ui.theme.Success
import com.example.saku.app.ui.theme.Success0
import com.example.saku.app.ui.theme.Success70
import com.example.saku.app.ui.theme.Surface
import com.example.saku.app.ui.theme.TextMuted
import com.example.saku.app.ui.theme.TextPrimary
import com.example.saku.app.ui.theme.TextSecondary
import androidx.compose.runtime.getValue

enum class TimelineStepState {
    COMPLETED,
    ACTIVE,
    PENDING,
    ERROR
}

data class TimelineStepItem(
    val title: String,
    val description: String? = null,
    val timestamp: String? = null,
    val state: TimelineStepState = TimelineStepState.PENDING,
    val icon: ImageVector? = null,
    val remark: String? = null,
    val tag: String? = null
)

@Composable
fun StatusTimelineCard(
    title: String,
    steps: List<TimelineStepItem>,
    modifier: Modifier = Modifier,
    referenceId: String? = null,
    statusBadgeText: String? = null,
    statusBadgeVariant: BadgeVariant = BadgeVariant.Primary,
    actionButtonText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, Border)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Row: Title & Subtitle on Left, 1-Line Badge on Right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f, fill = false)) {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        lineHeight = 19.sp
                    )
                    if (!referenceId.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = referenceId,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Primary60,
                            lineHeight = 16.sp
                        )
                    }
                }

                if (statusBadgeText != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Badge(
                        text = statusBadgeText,
                        variant = statusBadgeVariant,
                        size = BadgeSize.SM,
                        dot = true,
                        modifier = Modifier.wrapContentWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Border, thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))

            // Vertical Timeline Items
            VerticalTimeline(steps = steps)

            // Optional Action Button
            if (actionButtonText != null && onActionClick != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    text = actionButtonText,
                    onClick = onActionClick,
                    variant = ButtonVariant.Secondary,
                    size = ButtonSize.SM,
                    fullWidth = true
                )
            }
        }
    }
}

@Composable
fun VerticalTimeline(
    steps: List<TimelineStepItem>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        steps.forEachIndexed { index, step ->
            val isLast = index == steps.lastIndex
            VerticalTimelineStepRow(
                step = step,
                isLast = isLast
            )
        }
    }
}

@Composable
private fun VerticalTimelineStepRow(
    step: TimelineStepItem,
    isLast: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Left Column: Node Icon + Connecting Line
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(32.dp)
        ) {
            // Node Bubble
            when (step.state) {
                TimelineStepState.COMPLETED -> {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(Success0)
                            .border(1.5.dp, Success, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = step.icon ?: Lucide.Check,
                            contentDescription = null,
                            tint = Success70,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                TimelineStepState.ACTIVE -> {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Primary0.copy(alpha = pulseAlpha))
                            .border(2.dp, Primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Primary)
                        )
                    }
                }

                TimelineStepState.ERROR -> {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(Error0)
                            .border(1.5.dp, Error, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = step.icon ?: Lucide.CircleAlert,
                            contentDescription = null,
                            tint = Error70,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                TimelineStepState.PENDING -> {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Neutral0)
                            .border(1.dp, Neutral20, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Neutral40)
                        )
                    }
                }
            }

            // Connecting Vertical Line
            if (!isLast) {
                val lineColor = when (step.state) {
                    TimelineStepState.COMPLETED -> Success
                    TimelineStepState.ACTIVE -> Primary.copy(alpha = 0.6f)
                    TimelineStepState.ERROR -> Error.copy(alpha = 0.6f)
                    TimelineStepState.PENDING -> Border
                }
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(38.dp)
                        .background(lineColor)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Right Content Column
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (isLast) 0.dp else 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = step.title,
                    fontSize = 13.5.sp,
                    fontWeight = if (step.state == TimelineStepState.ACTIVE) FontWeight.Bold else FontWeight.SemiBold,
                    color = when (step.state) {
                        TimelineStepState.COMPLETED -> TextPrimary
                        TimelineStepState.ACTIVE -> Primary60
                        TimelineStepState.ERROR -> Error
                        TimelineStepState.PENDING -> TextSecondary
                    },
                    lineHeight = 17.sp,
                    modifier = Modifier.weight(1f, fill = false)
                )

                if (!step.timestamp.isNullOrBlank()) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = step.timestamp,
                        fontSize = 11.sp,
                        color = TextMuted,
                        lineHeight = 14.sp
                    )
                }
            }

            if (!step.description.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = step.description,
                    fontSize = 11.5.sp,
                    color = TextSecondary,
                    lineHeight = 15.sp
                )
            }

            if (!step.remark.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            when (step.state) {
                                TimelineStepState.ERROR -> Error0
                                TimelineStepState.ACTIVE -> Primary0
                                else -> Neutral0
                            }
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = step.remark,
                        fontSize = 11.5.sp,
                        color = when (step.state) {
                            TimelineStepState.ERROR -> Error70
                            TimelineStepState.ACTIVE -> Primary60
                            else -> Neutral60
                        },
                        fontWeight = FontWeight.Medium,
                        lineHeight = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
fun HorizontalTimeline(
    steps: List<TimelineStepItem>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, step ->
            val isLast = index == steps.lastIndex

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Left connector
                    if (index > 0) {
                        val prevStep = steps[index - 1]
                        val lineActive = prevStep.state == TimelineStepState.COMPLETED
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(2.dp)
                                .background(if (lineActive) Primary else Border)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }

                    // Node Circle
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(
                                when (step.state) {
                                    TimelineStepState.COMPLETED -> Primary
                                    TimelineStepState.ACTIVE -> Primary0
                                    TimelineStepState.ERROR -> Error0
                                    TimelineStepState.PENDING -> Surface
                                }
                            )
                            .border(
                                width = 1.5.dp,
                                color = when (step.state) {
                                    TimelineStepState.COMPLETED -> Primary
                                    TimelineStepState.ACTIVE -> Primary
                                    TimelineStepState.ERROR -> Error
                                    TimelineStepState.PENDING -> Border
                                },
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        when (step.state) {
                            TimelineStepState.COMPLETED -> {
                                Icon(
                                    imageVector = Lucide.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                            TimelineStepState.ACTIVE -> {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Primary)
                                )
                            }
                            TimelineStepState.ERROR -> {
                                Icon(
                                    imageVector = Lucide.CircleAlert,
                                    contentDescription = null,
                                    tint = Error,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                            TimelineStepState.PENDING -> {
                                Text(
                                    text = "${index + 1}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextMuted
                                )
                            }
                        }
                    }

                    // Right connector
                    if (!isLast) {
                        val lineActive = step.state == TimelineStepState.COMPLETED
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(2.dp)
                                .background(if (lineActive) Primary else Border)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = step.title,
                    fontSize = 11.sp,
                    fontWeight = if (step.state == TimelineStepState.ACTIVE) FontWeight.Bold else FontWeight.Medium,
                    color = if (step.state == TimelineStepState.PENDING) TextMuted else TextPrimary,
                    maxLines = 1,
                    lineHeight = 14.sp
                )
            }
        }
    }
}