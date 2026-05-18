package com.allterra.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.allterra.presentation.common.components.redesign.AllterraButton
import com.allterra.presentation.common.components.redesign.AllterraButtonVariant
import com.allterra.presentation.common.components.redesign.AllterraToggle
import com.allterra.presentation.common.components.redesign.allterraClickable
import com.allterra.presentation.localization.appStrings
import com.allterra.presentation.theme.AllterraTheme
import com.allterra.config.AppConfig

@Composable
fun SettingsScreen(
    onLogout: () -> Unit,
    onBack: () -> Unit
) {
    val strings = appStrings()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AllterraTheme.colors.bg)
    ) {
        // Simple Top Bar
        Row(
            modifier = Modifier.fillMaxWidth().padding(AllterraTheme.spacing.s4),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(strings.tabSettings, style = AllterraTheme.typography.displayM, color = AllterraTheme.colors.ink)
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = AllterraTheme.spacing.screenPaddingX)
        ) {
            SettingsSection(strings.settingsDisplaySection) {
                SettingsRow(strings.settingsLanguageLabel, "English") // TODO: Dynamic
                SettingsToggleRow(strings.settingsThemeLabel, true) {} // TODO: Dynamic
            }

            SettingsSection(strings.settingsNotificationsSection) {
                SettingsToggleRow("Trip Reminders", true) {}
                SettingsToggleRow("Weather Alerts", false) {}
            }

            SettingsSection(strings.settingsPrivacySection) {
                SettingsRow("Location Sharing", "While using")
                SettingsRow("Profile Visibility", "Public")
            }

            SettingsSection(strings.settingsAboutSection) {
                SettingsRow(strings.settingsVersionLabel, "v${AppConfig.version}")
                SettingsRow("Terms of Service", "")
                SettingsRow("Privacy Policy", "")
            }

            Spacer(modifier = Modifier.height(AllterraTheme.spacing.s10))

            AllterraButton(
                text = strings.logoutAction,
                variant = AllterraButtonVariant.Ghost,
                modifier = Modifier.fillMaxWidth(),
                onClick = onLogout
            )
            
            Spacer(modifier = Modifier.height(AllterraTheme.spacing.s10))
        }
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.padding(top = AllterraTheme.spacing.s6)) {
        Text(
            text = title.uppercase(),
            style = AllterraTheme.typography.caption,
            color = AllterraTheme.colors.muted2,
            modifier = Modifier.padding(bottom = AllterraTheme.spacing.s2)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(AllterraTheme.colors.surface, shape = androidx.compose.foundation.shape.RoundedCornerShape(AllterraTheme.radius.md))
                .padding(horizontal = AllterraTheme.spacing.s4)
        ) {
            content()
        }
    }
}

@Composable
private fun SettingsRow(label: String, value: String, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .allterraClickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = AllterraTheme.typography.body, color = AllterraTheme.colors.ink)
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (value.isNotEmpty()) {
                Text(value, style = AllterraTheme.typography.body, color = AllterraTheme.colors.muted)
                Spacer(modifier = Modifier.width(8.dp))
            }
            Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = AllterraTheme.colors.line2)
        }
    }
}

@Composable
private fun SettingsToggleRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = AllterraTheme.typography.body, color = AllterraTheme.colors.ink)
        AllterraToggle(checked = checked, onCheckedChange = onCheckedChange)
    }
}
