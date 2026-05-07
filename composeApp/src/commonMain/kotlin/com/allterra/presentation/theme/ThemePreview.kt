package com.allterra.presentation.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.allterra.presentation.common.components.redesign.*

@Composable
fun ThemePreviewScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AllterraTheme.colors.bg)
            .verticalScroll(rememberScrollState())
            .padding(AllterraTheme.spacing.screenPaddingX)
            .padding(bottom = 100.dp) // Space for floating tabbar placeholder
    ) {
        Text("Allterra Redesign Preview", style = AllterraTheme.typography.displayXL, color = AllterraTheme.colors.ink)
        
        Spacer(modifier = Modifier.height(AllterraTheme.spacing.sectionGap))
        
        Section("Typography") {
            TypographyRow("Display XL", AllterraTheme.typography.displayXL)
            TypographyRow("Display L", AllterraTheme.typography.displayL)
            TypographyRow("Display M", AllterraTheme.typography.displayM)
            TypographyRow("Title", AllterraTheme.typography.title)
            TypographyRow("Body", AllterraTheme.typography.body)
            TypographyRow("Body Strong", AllterraTheme.typography.bodyStrong)
            TypographyRow("Small", AllterraTheme.typography.small)
            TypographyRow("Caption", AllterraTheme.typography.caption)
            TypographyRow("Mono", AllterraTheme.typography.mono)
        }

        Divider()

        Section("Categorical Colors") {
            CategoricalRow("Wallet (Moss)", AllterraTheme.categorical.wallet)
            CategoricalRow("Route (Terra)", AllterraTheme.categorical.route)
            CategoricalRow("Gear (Ochre)", AllterraTheme.categorical.gear)
            CategoricalRow("Social (Sky)", AllterraTheme.categorical.social)
        }

        Divider()

        Section("Sunset Anchors") {
            ColorRow("Midnight Family", listOf(AllterraTheme.colors.midnight, AllterraTheme.colors.midnight2, AllterraTheme.colors.midnight3))
            ColorRow("Crimson Family", listOf(AllterraTheme.colors.crimson, AllterraTheme.colors.crimson2, AllterraTheme.colors.crimsonSoft))
            ColorRow("Night Mode Surfaces", listOf(AllterraTheme.colors.nightBg, AllterraTheme.colors.nightSurface, AllterraTheme.colors.nightSurface2))
        }

        Divider()

        Section("Buttons") {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AllterraButton("Primary", modifier = Modifier.weight(1f)) {}
                AllterraButton("Terra", variant = AllterraButtonVariant.Terra, modifier = Modifier.weight(1f)) {}
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AllterraButton("Secondary", variant = AllterraButtonVariant.Secondary, modifier = Modifier.weight(1f)) {}
                AllterraButton("Small", isSmall = true, modifier = Modifier.weight(1f)) {}
            }
        }

        Divider()

        Section("Inputs & Cards") {
            var text by remember { mutableStateOf("") }
            AllterraInput(value = text, onValueChange = { text = it }, placeholder = "Enter text...")
            Spacer(modifier = Modifier.height(12.dp))
            AllterraProgressBar(progress = 0.65f)
            Spacer(modifier = Modifier.height(12.dp))
            AllterraCard {
                Column {
                    Text("Card Content", style = AllterraTheme.typography.title, color = AllterraTheme.colors.ink)
                    Text("This is a standard Allterra card with shadow and border.", style = AllterraTheme.typography.body, color = AllterraTheme.colors.muted)
                }
            }
        }

        Divider()

        Section("Chips & Avatars") {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AllterraAvatar(initials = "JD", size = AllterraAvatarSize.Medium)
                AllterraAvatar(initials = "AS", size = AllterraAvatarSize.Small)
                AllterraChip("Pro User", categorical = AllterraTheme.categorical.wallet)
                AllterraChip("Route", categorical = AllterraTheme.categorical.route)
            }
        }

        Divider()

        Section("Selection") {
            var checked by remember { mutableStateOf(true) }
            var toggled by remember { mutableStateOf(true) }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                AllterraCheckbox(checked = checked, onCheckedChange = { checked = it })
                AllterraToggle(checked = toggled, onCheckedChange = { toggled = it })
            }
        }
    }
}

@Composable
private fun Section(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(title.uppercase(), style = AllterraTheme.typography.caption, color = AllterraTheme.colors.muted)
        Spacer(modifier = Modifier.height(12.dp))
        content()
    }
}

@Composable
private fun Divider() {
    HorizontalDivider(
        modifier = Modifier.padding(vertical = AllterraTheme.spacing.sectionGap),
        color = AllterraTheme.colors.line
    )
}

@Composable
private fun TypographyRow(label: String, style: androidx.compose.ui.text.TextStyle) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(label, style = style, color = AllterraTheme.colors.ink)
    }
}

@Composable
private fun ColorRow(label: String, colors: List<androidx.compose.ui.graphics.Color>) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(label, style = AllterraTheme.typography.bodyStrong, color = AllterraTheme.colors.ink)
        Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
            colors.forEach { color ->
                Box(modifier = Modifier.weight(1f).height(24.dp).background(color))
            }
        }
    }
}

@Composable
private fun CategoricalRow(label: String, categorical: CategoricalColor) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(label, style = AllterraTheme.typography.bodyStrong, color = AllterraTheme.colors.ink)
        Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
            Box(modifier = Modifier.weight(1f).height(24.dp).background(categorical.color))
            Box(modifier = Modifier.weight(1f).height(24.dp).background(categorical.color2))
            Box(modifier = Modifier.weight(1f).height(24.dp).background(categorical.soft))
            Box(modifier = Modifier.weight(1f).height(24.dp).background(categorical.ink))
        }
    }
}
