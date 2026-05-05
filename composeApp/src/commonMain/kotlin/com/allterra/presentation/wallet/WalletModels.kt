package com.allterra.presentation.wallet

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*

enum class WalletCategory {
    TICKET, BOOKING, INSURANCE, ID, OTHER
}

data class WalletItem(
    val id: String,
    val title: String,
    val subTitle: String,
    val category: WalletCategory,
    val date: String,
    val isOfflineAvailable: Boolean = true,
    val qrCodeData: String? = null,
    val fileUrl: String? = null
)

fun WalletCategory.icon(): ImageVector {
    return when (this) {
        WalletCategory.TICKET -> Icons.Outlined.ConfirmationNumber
        WalletCategory.BOOKING -> Icons.Outlined.Hotel
        WalletCategory.INSURANCE -> Icons.Outlined.Shield
        WalletCategory.ID -> Icons.Outlined.Badge
        WalletCategory.OTHER -> Icons.Outlined.Description
    }
}
