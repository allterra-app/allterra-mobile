package com.allterra.presentation.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.allterra.presentation.common.components.redesign.AllterraButton
import com.allterra.presentation.common.components.redesign.AllterraButtonVariant
import com.allterra.presentation.theme.AllterraTheme
import com.allterra.presentation.theme.CategoricalColor

@Composable
fun OnboardingScreen(onCompleted: () -> Unit) {
    var currentStep by remember { mutableStateOf(-1) } // -1 is Overview

    if (currentStep == -1) {
        OnboardingOverview(onStart = { currentStep = 0 })
    } else {
        OnboardingStep(
            step = currentStep,
            onNext = {
                if (currentStep < 3) currentStep++ else onCompleted()
            },
            onSkip = onCompleted
        )
    }
}

@Composable
fun OnboardingOverview(onStart: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AllterraTheme.colors.bg)
            .padding(AllterraTheme.spacing.screenPaddingX),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))
        
        Text(
            text = "Welcome to Allterra",
            style = AllterraTheme.typography.displayXL,
            color = AllterraTheme.colors.ink,
            textAlign = TextAlign.Center
        )
        
        Text(
            text = "Your digital companion for every trip.",
            style = AllterraTheme.typography.body,
            color = AllterraTheme.colors.muted,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        // 2x2 Grid of features
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FeatureCard("Wallet", AllterraTheme.categorical.wallet, Modifier.weight(1f))
                FeatureCard("Map", AllterraTheme.categorical.route, Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FeatureCard("Gear", AllterraTheme.categorical.gear, Modifier.weight(1f))
                FeatureCard("Social", AllterraTheme.categorical.social, Modifier.weight(1f))
            }
        }

        Spacer(modifier = Modifier.weight(1.5f))

        AllterraButton(
            text = "Get Started",
            modifier = Modifier.fillMaxWidth(),
            onClick = onStart
        )
        
        Spacer(modifier = Modifier.height(AllterraTheme.spacing.s4))
    }
}

@Composable
private fun FeatureCard(title: String, categorical: CategoricalColor, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(AllterraTheme.radius.md))
            .background(categorical.soft)
            .padding(16.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        Text(
            text = title,
            style = AllterraTheme.typography.title,
            color = categorical.ink
        )
    }
}

@Composable
fun OnboardingStep(step: Int, onNext: () -> Unit, onSkip: () -> Unit) {
    val wallet = AllterraTheme.categorical.wallet
    val route = AllterraTheme.categorical.route
    val gear = AllterraTheme.categorical.gear
    val social = AllterraTheme.categorical.social

    val data = remember(step, wallet, route, gear, social) {
        when (step) {
            0 -> OnboardingData("Travel Wallet", "Aggregate all your PDFs, tickets, and bookings in one secure place.", wallet)
            1 -> OnboardingData("Offline Maps", "Navigate trails and cities without worrying about internet connection.", route)
            2 -> OnboardingData("Gear Tracking", "Manage your equipment, track wear, and never forget a piece of gear.", gear)
            else -> OnboardingData("Share the Journey", "Join clubs, follow friends, and share your outdoor stories.", social)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(data.categorical.soft)
            .padding(AllterraTheme.spacing.screenPaddingX),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(100.dp))
        
        // Placeholder for illustration
        Box(
            modifier = Modifier
                .size(240.dp)
                .clip(RoundedCornerShape(AllterraTheme.radius.xl))
                .background(Color.White.copy(alpha = 0.5f))
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = data.title,
            style = AllterraTheme.typography.displayL,
            color = data.categorical.ink,
            textAlign = TextAlign.Center
        )

        Text(
            text = data.description,
            style = AllterraTheme.typography.body,
            color = data.categorical.ink.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp, bottom = 40.dp)
        )

        // Progress dots
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(4) { i ->
                Box(
                    modifier = Modifier
                        .size(if (i == step) 24.dp else 8.dp, 8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (i == step) data.categorical.color else data.categorical.color.copy(alpha = 0.2f))
                )
            }
        }

        Spacer(modifier = Modifier.height(60.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AllterraButton(
                text = "Skip",
                variant = AllterraButtonVariant.Ghost,
                modifier = Modifier.weight(1f),
                onClick = onSkip
            )
            AllterraButton(
                text = if (step == 3) "Finish" else "Next",
                modifier = Modifier.weight(2f),
                onClick = onNext
            )
        }
        
        Spacer(modifier = Modifier.height(AllterraTheme.spacing.s4))
    }
}

private data class OnboardingData(
    val title: String,
    val description: String,
    val categorical: CategoricalColor
)
