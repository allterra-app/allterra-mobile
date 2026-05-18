package com.allterra.domain.repository

import com.allterra.core.result.ApiResult
import com.allterra.presentation.wallet.WalletItem

interface WalletRepository {
    suspend fun getDocuments(): ApiResult<List<WalletItem>>
    suspend fun getDocument(id: String): ApiResult<WalletItem>
    suspend fun deleteDocument(id: String): ApiResult<Unit>
}
