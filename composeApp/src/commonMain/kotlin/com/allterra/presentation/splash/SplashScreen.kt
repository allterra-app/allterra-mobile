package com.allterra.presentation.splash

import allterra.composeapp.generated.resources.Res
import allterra.composeapp.generated.resources.mountain_color_no_bg
import allterra.composeapp.generated.resources.allterra_name_no_bg
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import com.allterra.config.AppConfig
import com.allterra.presentation.theme.AllterraTheme
import org.jetbrains.compose.resources.painterResource

@Composable
fun SplashScreen(backdropIndex: Int) {
    var startAnimation by remember { mutableStateOf(false) }
    
    val pinOffset by animateDpAsState(
        targetValue = if (startAnimation) 0.dp else (-300).dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    
    val logoAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 800, easing = LinearOutSlowInEasing)
    )

    LaunchedEffect(Unit) {
        startAnimation = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AllterraTheme.colors.bg),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(Res.drawable.mountain_color_no_bg),
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .offset(y = pinOffset)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Image(
                painter = painterResource(Res.drawable.allterra_name_no_bg),
                contentDescription = "Allterra",
                modifier = Modifier
                    .width(200.dp)
                    .alpha(logoAlpha)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = com.allterra.presentation.localization.appStrings().appTagline,
                style = AllterraTheme.typography.body,
                color = AllterraTheme.colors.muted,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(logoAlpha).padding(horizontal = AllterraTheme.spacing.s4)
            )
        }

        // Versioning and Connectivity Status
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(2.dp)
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                            colors = listOf(
                                AllterraTheme.categorical.wallet.color,
                                AllterraTheme.categorical.route.color,
                                AllterraTheme.categorical.gear.color,
                                AllterraTheme.categorical.social.color
                            )
                        )
                    )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "ver. ${AppConfig.version} · ${AppConfig.environment.name}",
                style = AllterraTheme.typography.small,
                color = AllterraTheme.colors.muted2
            )
        }
    }
}
