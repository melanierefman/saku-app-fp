package com.example.saku.app.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton as M3IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.CircleAlert
import com.composables.icons.lucide.Eye
import com.composables.icons.lucide.FileText
import com.composables.icons.lucide.IdCard
import com.composables.icons.lucide.ImagePlus
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.RotateCw
import com.composables.icons.lucide.Trash2
import com.example.saku.app.ui.theme.Border
import com.example.saku.app.ui.theme.Error
import com.example.saku.app.ui.theme.Error0
import com.example.saku.app.ui.theme.Neutral0
import com.example.saku.app.ui.theme.Neutral40
import com.example.saku.app.ui.theme.Primary
import com.example.saku.app.ui.theme.Primary0
import com.example.saku.app.ui.theme.Primary20
import com.example.saku.app.ui.theme.Primary60
import com.example.saku.app.ui.theme.Primary90
import com.example.saku.app.ui.theme.Success
import com.example.saku.app.ui.theme.Success0
import com.example.saku.app.ui.theme.Surface
import com.example.saku.app.ui.theme.TextMuted
import com.example.saku.app.ui.theme.TextPrimary
import com.example.saku.app.ui.theme.TextSecondary

enum class UploadStatus {
    EMPTY,
    UPLOADING,
    UPLOADED,
    ERROR
}

@Composable
fun DocumentUploadCard(
    title: String,
    modifier: Modifier = Modifier,
    description: String = "Format JPG, PNG, atau PDF (maks. 5MB)",
    status: UploadStatus = UploadStatus.EMPTY,
    icon: ImageVector = Lucide.IdCard,
    fileName: String? = null,
    fileSize: String? = null,
    statusBadgeText: String? = null,
    statusBadgeVariant: BadgeVariant = BadgeVariant.Success,
    errorMessage: String? = null,
    uploadProgress: Float = 0.6f,
    onUploadClick: () -> Unit,
    onPreviewClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(
            width = 1.dp,
            color = when (status) {
                UploadStatus.ERROR -> Error.copy(alpha = 0.6f)
                UploadStatus.UPLOADED -> Primary20
                else -> Border
            }
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Icon + Title + Description + Status Badge (1 line)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                when (status) {
                                    UploadStatus.UPLOADED -> Success0
                                    UploadStatus.ERROR -> Error0
                                    else -> Primary0
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = when (status) {
                                UploadStatus.UPLOADED -> Success
                                UploadStatus.ERROR -> Error
                                else -> Primary
                            },
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = title,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            lineHeight = 17.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = description,
                            fontSize = 11.5.sp,
                            color = TextSecondary,
                            lineHeight = 15.sp
                        )
                    }
                }

                if (statusBadgeText != null && status == UploadStatus.UPLOADED) {
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

            Spacer(modifier = Modifier.height(14.dp))

            // Body depending on Status
            when (status) {
                UploadStatus.EMPTY -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Neutral0)
                            .border(1.dp, Border, RoundedCornerShape(12.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(color = Primary.copy(alpha = 0.1f)),
                                onClick = onUploadClick
                            )
                            .padding(vertical = 18.dp, horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Lucide.ImagePlus,
                                contentDescription = "Upload",
                                tint = Primary,
                                modifier = Modifier.size(26.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Ketuk untuk Memilih Foto Dokumen",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Primary60
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Pastikan foto terang, tidak silau, dan tulisan terbaca jelas",
                                fontSize = 11.sp,
                                color = TextMuted,
                                textAlign = TextAlign.Center,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }

                UploadStatus.UPLOADING -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Primary0)
                            .border(1.dp, Primary20, RoundedCornerShape(12.dp))
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Primary,
                                strokeWidth = 2.5.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Mengunggah Dokumen...",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Primary60,
                                    lineHeight = 16.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${(uploadProgress * 100).toInt()}% selesai",
                                    fontSize = 11.5.sp,
                                    color = Primary90,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                }

                UploadStatus.UPLOADED -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF9FAFB))
                            .border(1.dp, Border, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Primary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Lucide.FileText,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = fileName ?: "dokumen_terunggah.jpg",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(1.dp))
                                    Text(
                                        text = fileSize ?: "2.4 MB • Berhasil Diunggah",
                                        fontSize = 11.sp,
                                        color = TextSecondary,
                                        lineHeight = 14.sp
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (onPreviewClick != null) {
                                    M3IconButton(onClick = onPreviewClick, modifier = Modifier.size(32.dp)) {
                                        Icon(
                                            imageVector = Lucide.Eye,
                                            contentDescription = "Preview",
                                            tint = Neutral40,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                M3IconButton(onClick = onUploadClick, modifier = Modifier.size(32.dp)) {
                                    Icon(
                                        imageVector = Lucide.RotateCw,
                                        contentDescription = "Ganti Foto",
                                        tint = Primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                if (onDeleteClick != null) {
                                    M3IconButton(onClick = onDeleteClick, modifier = Modifier.size(32.dp)) {
                                        Icon(
                                            imageVector = Lucide.Trash2,
                                            contentDescription = "Hapus",
                                            tint = Error,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                UploadStatus.ERROR -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Error0)
                            .border(1.dp, Error.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(Error.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Lucide.CircleAlert,
                                        contentDescription = null,
                                        tint = Error,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Gagal Mengunggah Dokumen",
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Error,
                                        lineHeight = 16.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = errorMessage ?: "Ukuran file terlalu besar atau format tidak didukung.",
                                        fontSize = 11.sp,
                                        color = Error.copy(alpha = 0.85f),
                                        lineHeight = 14.5.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Button(
                                    text = "Coba Lagi",
                                    onClick = onUploadClick,
                                    variant = ButtonVariant.Error,
                                    size = ButtonSize.SM,
                                    fullWidth = false,
                                    leadingIcon = Lucide.RotateCw
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}