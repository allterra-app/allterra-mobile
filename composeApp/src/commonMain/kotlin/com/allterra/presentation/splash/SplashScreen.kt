package com.allterra.presentation.splash

import allterra.composeapp.generated.resources.Res
import allterra.composeapp.generated.resources.allterra_logo
import allterra.composeapp.generated.resources.allterra_text_logo
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.allterra.presentation.theme.AllterraTheme
import org.jetbrains.compose.resources.painterResource

@Composable
fun SplashScreen(backdropIndex: Int) {
    var startAnimation by remember { mutableStateOf(false) }
    
    val pinOffset by animateDpAsState(
        targetValue = if (startAnimation) 0.dp else (-300).dp,
        animationSpec = spring(
            dampingRatio = 0.5f, // Medium bounce
            stiffness = 200f // Low stiffness
        )
    )
    
    val logoAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1000, delayMillis = 500)
    )

    LaunchedEffect(Unit) {
        startAnimation = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AllterraTheme.colors.bgSub),
        contentAlignment = Alignment.Center
    ) {
        // Simple mountain placeholder using basic shapes or just color for now
        // In a real app, this would be a vector illustration
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.4f)
                .align(Alignment.BottomCenter)
                .background(AllterraTheme.colors.mossSoft.copy(alpha = 0.5f))
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(Res.drawable.allterra_logo),
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .offset(y = pinOffset)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Image(
                painter = painterResource(Res.drawable.allterra_text_logo),
                contentDescription = "Allterra",
                modifier = Modifier
                    .width(200.dp)
                    .alpha(logoAlpha)
            )
        }
    }
}
