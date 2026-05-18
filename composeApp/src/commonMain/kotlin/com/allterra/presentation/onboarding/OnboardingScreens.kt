package com.allterra.presentation.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import allterra.composeapp.generated.resources.Res
import allterra.composeapp.generated.resources.allterra_logo
import allterra.composeapp.generated.resources.mountain_color_no_bg
import allterra.composeapp.generated.resources.allterra_name_no_bg
import com.allterra.presentation.common.components.navigation.AllterraIcons
import com.allterra.presentation.common.components.redesign.AllterraButton
import com.allterra.presentation.common.components.redesign.AllterraButtonVariant
import com.allterra.presentation.localization.appStrings
import com.allterra.presentation.theme.AllterraCategory
import com.allterra.presentation.theme.AllterraTheme
import com.allterra.presentation.theme.CategoricalColor
import org.jetbrains.compose.resources.painterResource

@Composable
fun OnboardingScreen(onCompleted: () -> Unit) {
    var currentStep by remember { mutableStateOf(-1) }

    if (currentStep == -1) {
        AllterraTheme(category = AllterraCategory.Social) {
            OnboardingOverview(onStart = { currentStep = 0 })
        }
    } else {
        val category = when (currentStep) {
            0 -> AllterraCategory.Wallet
            1 -> AllterraCategory.Route
            2 -> AllterraCategory.Gear
            else -> AllterraCategory.Social
        }
        AllterraTheme(category = category) {
            OnboardingStep(
                step = currentStep,
                onNext = {
                    if (currentStep < 3) currentStep++ else onCompleted()
                },
                onSkip = onCompleted
            )
        }
    }
}

@Composable
fun OnboardingOverview(onStart: () -> Unit) {
    val strings = appStrings()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AllterraTheme.colors.bg)
            .padding(horizontal = AllterraTheme.spacing.screenPaddingX)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(AllterraTheme.spacing.s10))

        Image(painter = painterResource(Res.drawable.mountain_color_no_bg), contentDescription = null, modifier = Modifier.size(80.dp))
        Image(painter = painterResource(Res.drawable.allterra_name_no_bg), contentDescription = "Allterra", modifier = Modifier.height(32.dp))

        Text(
            text = strings.appTagline,
            style = AllterraTheme.typography.body,
            color = AllterraTheme.colors.muted,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = AllterraTheme.spacing.s3)
        )

        Spacer(modifier = Modifier.height(AllterraTheme.spacing.s8))

        Column(verticalArrangement = Arrangement.spacedBy(AllterraTheme.spacing.s3)) {
            Row(horizontalArrangement = Arrangement.spacedBy(AllterraTheme.spacing.s3)) {
                FeatureCard(strings.walletFeatureTitle, strings.walletFeatureDesc, "01", AllterraIcons.Ticket, AllterraCategory.Wallet, Modifier.weight(1f))
                FeatureCard(strings.routesFeatureTitle, strings.routesFeatureDesc, "02", AllterraIcons.Route, AllterraCategory.Route, Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(AllterraTheme.spacing.s3)) {
                FeatureCard(strings.gearFeatureTitle, strings.gearFeatureDesc, "03", AllterraIcons.Scales, AllterraCategory.Gear, Modifier.weight(1f))
                FeatureCard(strings.sharingFeatureTitle, strings.sharingFeatureDesc, "04", AllterraIcons.Sharing, AllterraCategory.Social, Modifier.weight(1f))
            }
        }

        Spacer(modifier = Modifier.height(AllterraTheme.spacing.s6))

        Row(horizontalArrangement = Arrangement.spacedBy(AllterraTheme.spacing.s2)) {
            BadgePill("OFFLINE FIRST")
            BadgePill("OPEN GPX")
            BadgePill("PRIVACY")
        }

        Spacer(modifier = Modifier.height(AllterraTheme.spacing.s8))

        AllterraButton(text = strings.onboardingStartAction + " ->", modifier = Modifier.fillMaxWidth(), onClick = onStart)

        AllterraButton(text = strings.onboardingSkipAction, variant = AllterraButtonVariant.Ghost, modifier = Modifier.fillMaxWidth(), onClick = onStart)

        Spacer(modifier = Modifier.height(AllterraTheme.spacing.s4))
    }
}

@Composable
private fun FeatureCard(title: String, desc: String, index: String, icon: androidx.compose.ui.graphics.vector.ImageVector, category: AllterraCategory, modifier: Modifier = Modifier) {
    val colors = when(category) {
        AllterraCategory.Wallet -> AllterraTheme.categorical.wallet
        AllterraCategory.Route -> AllterraTheme.categorical.route
        AllterraCategory.Gear -> AllterraTheme.categorical.gear
        AllterraCategory.Social -> AllterraTheme.categorical.social
        else -> CategoricalColor(AllterraTheme.colors.ink, AllterraTheme.colors.ink, AllterraTheme.colors.line, AllterraTheme.colors.ink)
    }

    Box(
        modifier = modifier
            .aspectRatio(0.85f)
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(AllterraTheme.radius.md))
            .clip(RoundedCornerShape(AllterraTheme.radius.md))
            .background(AllterraTheme.colors.surface)
            .padding(AllterraTheme.spacing.s3)
    ) {
        Column(horizontalAlignment = Alignment.Start) {
            Icon(icon, contentDescription = null, tint = colors.color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(AllterraTheme.spacing.s2))
            Text(title, style = AllterraTheme.typography.title, color = AllterraTheme.colors.ink)
            Text(desc, style = AllterraTheme.typography.small, color = AllterraTheme.colors.muted)
            Spacer(modifier = Modifier.weight(1f))
            Text(index, style = AllterraTheme.typography.caption, color = AllterraTheme.colors.muted2)
        }
    }
}

@Composable
private fun BadgePill(text: String) {
    Box(modifier = Modifier.clip(RoundedCornerShape(50)).background(AllterraTheme.colors.bgSub).padding(horizontal = AllterraTheme.spacing.s3, vertical = AllterraTheme.spacing.s1)) {
        Text(text, style = AllterraTheme.typography.caption, color = AllterraTheme.colors.muted)
    }
}

@Composable
fun OnboardingStep(step: Int, onNext: () -> Unit, onSkip: () -> Unit) {
    val strings = appStrings()
    val categorical = AllterraTheme.currentCategoryColors

    val icon = when (step) {
        0 -> AllterraIcons.Ticket
        1 -> AllterraIcons.Route
        2 -> AllterraIcons.Scales
        else -> AllterraIcons.Sharing
    }

    val (title, description) = when (step) {
        0 -> strings.onboardingWalletTitle to strings.onboardingWalletBody
        1 -> strings.onboardingMapTitle to strings.onboardingMapBody
        2 -> strings.onboardingGearTitle to strings.onboardingGearBody
        else -> strings.onboardingSocialTitle to strings.onboardingSocialBody
    }

    Column(
        modifier = Modifier.fillMaxSize().background(AllterraTheme.colors.bg).padding(horizontal = AllterraTheme.spacing.screenPaddingX),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(AllterraTheme.spacing.s10))

        Box(modifier = Modifier.size(240.dp).clip(RoundedCornerShape(AllterraTheme.radius.xl)).background(AllterraTheme.colors.surface2), contentAlignment = Alignment.Center) {
            Icon(imageVector = icon, contentDescription = null, tint = categorical.color, modifier = Modifier.size(96.dp))
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(text = title, style = AllterraTheme.typography.displayL, color = AllterraTheme.colors.ink, textAlign = TextAlign.Center)
        Text(text = description, style = AllterraTheme.typography.body, color = AllterraTheme.colors.muted, textAlign = TextAlign.Center, modifier = Modifier.padding(top = AllterraTheme.spacing.s2, bottom = AllterraTheme.spacing.s8))

        Row(horizontalArrangement = Arrangement.spacedBy(AllterraTheme.spacing.s1)) {
            repeat(4) { i ->
                Box(modifier = Modifier.size(if (i == step) 24.dp else 8.dp, 8.dp).clip(RoundedCornerShape(4.dp)).background(if (i == step) categorical.color else AllterraTheme.colors.line))
            }
        }

        Spacer(modifier = Modifier.height(AllterraTheme.spacing.s6))

        Row(modifier = Modifier.fillMaxWidth().padding(bottom = AllterraTheme.spacing.s4), horizontalArrangement = Arrangement.spacedBy(AllterraTheme.spacing.s3)) {
            AllterraButton(text = strings.onboardingSkipAction, variant = AllterraButtonVariant.Ghost, modifier = Modifier.weight(1f), onClick = onSkip)
            AllterraButton(text = if (step == 3) strings.onboardingFinishAction else strings.onboardingNextAction, modifier = Modifier.weight(2f), onClick = onNext)
        }
    }
}
