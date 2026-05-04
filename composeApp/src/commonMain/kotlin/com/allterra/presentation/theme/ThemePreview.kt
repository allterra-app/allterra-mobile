package com.allterra.presentation.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ThemePreviewScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AllterraTheme.colors.bg)
            .verticalScroll(rememberScrollState())
            .padding(AllterraTheme.spacing.screenPaddingX)
    ) {
        Text("Allterra Design Tokens", style = AllterraTheme.typography.displayXL, color = AllterraTheme.colors.ink)
        
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

        Spacer(modifier = Modifier.height(AllterraTheme.spacing.sectionGap))

        Section("Core Colors") {
            ColorRow("Background", AllterraTheme.colors.bg)
            ColorRow("Background Sub", AllterraTheme.colors.bgSub)
            ColorRow("Surface", AllterraTheme.colors.surface)
            ColorRow("Ink", AllterraTheme.colors.ink)
            ColorRow("Muted", AllterraTheme.colors.muted)
            ColorRow("Line", AllterraTheme.colors.line)
        }

        Spacer(modifier = Modifier.height(AllterraTheme.spacing.sectionGap))

        Section("Categorical Colors") {
            CategoricalRow("Wallet (Moss)", AllterraTheme.categorical.wallet)
            CategoricalRow("Route (Terra)", AllterraTheme.categorical.route)
            CategoricalRow("Gear (Ochre)", AllterraTheme.categorical.gear)
            CategoricalRow("Social (Sky)", AllterraTheme.categorical.social)
        }
    }
}

@Composable
private fun Section(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(title.uppercase(), style = AllterraTheme.typography.caption, color = AllterraTheme.colors.muted)
        Spacer(modifier = Modifier.height(8.dp))
        content()
    }
}

@Composable
private fun TypographyRow(label: String, style: androidx.compose.ui.text.TextStyle) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(label, style = style, color = AllterraTheme.colors.ink)
    }
}

@Composable
private fun ColorRow(label: String, color: Color) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = AllterraTheme.typography.body, color = AllterraTheme.colors.ink)
        Box(modifier = Modifier.size(40.dp, 20.dp).background(color))
    }
}

@Composable
private fun CategoricalRow(label: String, categorical: CategoricalColor) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(label, style = AllterraTheme.typography.bodyStrong, color = AllterraTheme.colors.ink)
        Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
            Box(modifier = Modifier.weight(1f).height(20.dp).background(categorical.color))
            Box(modifier = Modifier.weight(1f).height(20.dp).background(categorical.color2))
            Box(modifier = Modifier.weight(1f).height(20.dp).background(categorical.soft))
            Box(modifier = Modifier.weight(1f).height(20.dp).background(categorical.ink))
        }
    }
}
