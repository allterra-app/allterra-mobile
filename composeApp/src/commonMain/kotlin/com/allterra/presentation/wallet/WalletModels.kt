package com.allterra.presentation.wallet

import com.allterra.presentation.common.components.navigation.AllterraIcons

enum class WalletCategory {
    ALL, TRANSPORT, STAY, DOCS
}

enum class DocumentType {
    TICKET, BOOKING, INSURANCE, OTHER;
    
    fun icon(): androidx.compose.ui.graphics.vector.ImageVector {
        return when (this) {
            TICKET -> AllterraIcons.Ticket
            BOOKING -> AllterraIcons.Home
            INSURANCE -> AllterraIcons.Route
            OTHER -> AllterraIcons.Wallet
        }
    }
}

data class WalletItem(
    val id: String,
    val title: String,
    val type: DocumentType,
    val date: String,
    val tripName: String? = null,
    val isOffline: Boolean = false,
    val fileUrl: String? = null,
    val metadata: Map<String, String> = emptyMap()
)

