package com.allterra.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import com.allterra.presentation.localization.appStrings

@Composable
fun SettingsScreen() {
    val strings = appStrings()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(strings.settingsPlaceholderTitle, style = MaterialTheme.typography.headlineSmall)
        Text(strings.settingsPlaceholderBody, style = MaterialTheme.typography.bodyLarge)
    }
}
