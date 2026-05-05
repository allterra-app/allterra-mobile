package com.allterra.presentation.common.components.redesign

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.allterra.presentation.theme.AllterraTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllterraSheet(
    onDismiss: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(),
    content: @Composable ColumnScope.() -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AllterraTheme.colors.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        scrimColor = AllterraTheme.colors.ink.copy(alpha = 0.35f),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 14.dp)
                    .size(width = 36.dp, height = 4.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(AllterraTheme.colors.line2)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AllterraTheme.spacing.screenPaddingX)
                .padding(bottom = 30.dp)
        ) {
            content()
        }
    }
}
