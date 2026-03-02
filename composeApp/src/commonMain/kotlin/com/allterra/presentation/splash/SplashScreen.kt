package com.allterra.presentation.splash

import allterra.composeapp.generated.resources.Res
import allterra.composeapp.generated.resources.allterra_logo
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.allterra.presentation.common.components.resolveBackdrop
import com.allterra.presentation.localization.appStrings
import org.jetbrains.compose.resources.painterResource

@Composable
fun SplashScreen(backdropIndex: Int) {
    val strings = appStrings()

    androidx.compose.foundation.layout.Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(resolveBackdrop(backdropIndex)),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color(0x33000000), Color(0xAA122937))))
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = painterResource(Res.drawable.allterra_logo),
                contentDescription = strings.appName,
                modifier = Modifier.size(220.dp),
            )
            CircularProgressIndicator(color = Color.White)
            Text(text = strings.loadingText, color = Color.White, modifier = Modifier.padding(top = 12.dp))
        }
    }
}
