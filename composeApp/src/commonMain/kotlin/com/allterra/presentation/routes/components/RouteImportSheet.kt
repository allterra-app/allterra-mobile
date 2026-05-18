package com.allterra.presentation.routes.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FileUpload
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.allterra.presentation.common.components.redesign.*
import com.allterra.presentation.localization.appStrings
import com.allterra.presentation.theme.AllterraTheme
import com.allterra.domain.repository.RouteCreateProgressStage

@Composable
fun RouteImportSheet(
    isUploading: Boolean,
    progressStage: RouteCreateProgressStage?,
    title: String,
    description: String,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onImportGpx: () -> Unit,
    onSave: () -> Unit,
) {
    val strings = appStrings()
    val terra = AllterraTheme.categorical.route

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(AllterraTheme.spacing.screenPaddingX)
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = strings.routeCreateTitle,
            style = AllterraTheme.typography.title,
            color = AllterraTheme.colors.ink,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        if (isUploading) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = terra.color)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = when(progressStage) {
                        RouteCreateProgressStage.UPLOADING -> strings.routeUploadingLabel
                        RouteCreateProgressStage.PROCESSING -> strings.routeProcessingLabel
                        else -> strings.loadingText
                    },
                    style = AllterraTheme.typography.body,
                    color = AllterraTheme.colors.muted
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(AllterraTheme.spacing.s4)) {
                AllterraInput(
                    value = title,
                    onValueChange = onTitleChanged,
                    placeholder = strings.routeTitleLabel
                )
                AllterraInput(
                    value = description,
                    onValueChange = onDescriptionChanged,
                    placeholder = strings.routeDescriptionLabel,
                    singleLine = false
                )
                
                AllterraButton(
                    text = strings.importGpxAction,
                    variant = AllterraButtonVariant.Secondary,
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Outlined.FileUpload, null) },
                    onClick = onImportGpx
                )

                Spacer(modifier = Modifier.height(12.dp))

                AllterraButton(
                    text = strings.saveAction,
                    variant = AllterraButtonVariant.Primary,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = title.isNotBlank(),
                    onClick = onSave
                )
            }
        }
    }
}
