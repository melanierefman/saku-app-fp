package com.example.saku.app.features.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.ArrowRight
import com.composables.icons.lucide.Lucide
import com.example.saku.app.R
import com.example.saku.app.core.data.TokenManager
import com.example.saku.app.core.ui.components.Button
import com.example.saku.app.core.ui.components.ButtonSize
import com.example.saku.app.core.ui.components.ButtonVariant
import com.example.saku.app.ui.theme.Primary
import com.example.saku.app.ui.theme.TextPrimary
import com.example.saku.app.ui.theme.TextSecondary
import kotlinx.coroutines.launch

data class OnboardingPageData(
    val imageRes: Int,
    val title: String,
    val description: String,
    val buttonText: String,
    val showArrow: Boolean = false
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onNavigateToLogin: () -> Unit
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager.getInstance(context) }
    val scope = rememberCoroutineScope()

    val pages = listOf(
        OnboardingPageData(
            imageRes = R.drawable.onboarding_1,
            title = "Pinjaman Mudah & Cepat",
            description = "Ajukan pinjaman sesuai kebutuhan Anda dengan proses yang praktis dan mudah.",
            buttonText = "Lanjut",
            showArrow = true
        ),
        OnboardingPageData(
            imageRes = R.drawable.onboarding_2,
            title = "Plafond Sesuai\nKemampuan",
            description = "Dapatkan rekomendasi plafon berdasarkan data dan hasil scoring Anda.",
            buttonText = "Mulai Sekarang",
            showArrow = false
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })

    val completeOnboarding = {
        scope.launch {
            tokenManager.setOnboardingCompleted(true)
            onNavigateToLogin()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Action: Lewati (Skip) Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedVisibility(
                visible = pagerState.currentPage < pages.size - 1,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text(
                    text = "Lewati",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            completeOnboarding()
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Horizontal Pager for Onboarding Cards
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { pageIndex ->
            val page = pages[pageIndex]

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Illustration Card with Rounded Corners
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(28.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = page.imageRes),
                        contentDescription = page.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(28.dp))
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Title
                Text(
                    text = page.title,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                    lineHeight = 32.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Description Subtitle
                Text(
                    text = page.description,
                    fontSize = 14.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 21.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Dot Page Indicators (Active Pill + Inactive Dot)
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 12.dp)
        ) {
            repeat(pages.size) { index ->
                val isSelected = pagerState.currentPage == index
                val width by animateDpAsState(
                    targetValue = if (isSelected) 24.dp else 8.dp,
                    label = "dot_width"
                )
                val color = if (isSelected) Primary else Color(0xFFE2E8F0)

                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .height(8.dp)
                        .width(width)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bottom CTA Button
        val currentPageData = pages[pagerState.currentPage]
        val isLastPage = pagerState.currentPage == pages.size - 1

        Button(
            text = currentPageData.buttonText,
            onClick = {
                if (isLastPage) {
                    completeOnboarding()
                } else {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }
            },
            variant = ButtonVariant.Primary,
            size = ButtonSize.LG,
            fullWidth = true,
            trailingIcon = if (currentPageData.showArrow) Lucide.ArrowRight else null
        )

        Spacer(modifier = Modifier.height(12.dp))
    }
}
