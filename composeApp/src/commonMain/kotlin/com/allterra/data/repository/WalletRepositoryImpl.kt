package com.allterra.data.repository

import com.allterra.core.result.ApiResult
import com.allterra.domain.repository.WalletRepository
import com.allterra.network.document.ApiDocumentType
import com.allterra.network.document.DocumentApi
import com.allterra.network.document.DocumentResponseDto
import com.allterra.presentation.wallet.DocumentType
import com.allterra.presentation.wallet.WalletItem

class WalletRepositoryImpl(
    private val api: DocumentApi
) : WalletRepository {

    override suspend fun getDocuments(): ApiResult<List<WalletItem>> {
        return when (val result = api.getDocuments()) {
            is ApiResult.Success -> ApiResult.Success(result.data.map { it.toDomain() })
            is ApiResult.ValidationError -> ApiResult.ValidationError(result.message, result.fields)
            is ApiResult.Unauthorized -> ApiResult.Unauthorized(result.message)
            is ApiResult.Forbidden -> ApiResult.Forbidden(result.message)
            is ApiResult.NotFound -> ApiResult.NotFound(result.message)
            is ApiResult.ServerError -> ApiResult.ServerError(result.message)
            is ApiResult.NetworkError -> ApiResult.NetworkError(result.message)
            is ApiResult.UnknownError -> ApiResult.UnknownError(result.message)
        }
    }

    override suspend fun getDocument(id: String): ApiResult<WalletItem> {
        return when (val result = api.getDocument(id)) {
            is ApiResult.Success -> ApiResult.Success(result.data.toDomain())
            is ApiResult.ValidationError -> ApiResult.ValidationError(result.message, result.fields)
            is ApiResult.Unauthorized -> ApiResult.Unauthorized(result.message)
            is ApiResult.Forbidden -> ApiResult.Forbidden(result.message)
            is ApiResult.NotFound -> ApiResult.NotFound(result.message)
            is ApiResult.ServerError -> ApiResult.ServerError(result.message)
            is ApiResult.NetworkError -> ApiResult.NetworkError(result.message)
            is ApiResult.UnknownError -> ApiResult.UnknownError(result.message)
        }
    }

    override suspend fun deleteDocument(id: String): ApiResult<Unit> {
        return api.deleteDocument(id)
    }

    private fun DocumentResponseDto.toDomain(): WalletItem {
        return WalletItem(
            id = id,
            title = title,
            type = when (type) {
                ApiDocumentType.TICKET -> DocumentType.TICKET
                ApiDocumentType.BOOKING -> DocumentType.BOOKING
                ApiDocumentType.INSURANCE -> DocumentType.INSURANCE
                ApiDocumentType.OTHER -> DocumentType.OTHER
            },
            date = createdAt.take(10),
            tripName = null,
            isOffline = true,
            fileUrl = fileUrl
        )
    }
}
