package com.example.saku.app.core.ui.components.feedback

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.ShieldAlert
import com.example.saku.app.core.ui.components.Button
import com.example.saku.app.core.ui.components.ButtonSize
import com.example.saku.app.core.ui.components.ButtonVariant
import com.example.saku.app.ui.theme.Error
import com.example.saku.app.ui.theme.Error0
import com.example.saku.app.ui.theme.Error60
import com.example.saku.app.ui.theme.TextPrimary
import com.example.saku.app.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RootWarningDialog(
    reasons: List<String>,
    onDismiss: () -> Unit,
    onExitApp: () -> Unit
) {
    BasicAlertDialog(
        onDismissRequest = {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Warning Icon Shield
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Error0),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Lucide.ShieldAlert,
                        contentDescription = "Peringatan Keamanan",
                        tint = Error60,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Peringatan Keamanan Perangkat",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Aplikasi SAKU mendeteksi bahwa perangkat ini telah dimodifikasi (Root / Jailbroken). Demi menjaga kerahasiaan data finansial dan saldo pinjaman Anda, penggunaan pada perangkat yang di-root tidak disarankan.",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )

                if (reasons.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF9FAFB))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "Indikasi Terdeteksi:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Error
                        )
                        reasons.take(2).forEach { reason ->
                            Text(
                                text = "• $reason",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        text = "Keluar",
                        onClick = onExitApp,
                        variant = ButtonVariant.Outline,
                        size = ButtonSize.MD,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        text = "Lanjutkan",
                        onClick = onDismiss,
                        variant = ButtonVariant.Primary,
                        size = ButtonSize.MD,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
