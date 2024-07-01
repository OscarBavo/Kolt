package com.mkrs.kolt.transfer.domain.repositories

import com.mkrs.kolt.base.webservices.MKTGenericResponse
import com.mkrs.kolt.base.webservices.common.ErrorResponse
import com.mkrs.kolt.base.webservices.entity.TransferInventoryRequest
import com.mkrs.kolt.base.webservices.entity.TransferRequest
import com.mkrs.kolt.transfer.domain.models.FinalProductModel

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.transfer.domain
 * Date: 27 / 05 / 2024
 *****/
interface TransferRepository {
    suspend fun getCodePT(
        claveMaterial: String,
        isDummy: Boolean = false
    ): MKTGenericResponse<String>

    suspend fun postDetailInventory(
        request: TransferInventoryRequest,
        isDummy: Boolean = false
    ): MKTGenericResponse<FinalProductModel>

    suspend fun postTransfer(
        request: TransferRequest,
        isDummy: Boolean = false
    ): MKTGenericResponse<ErrorResponse>
}