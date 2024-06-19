package com.mkrs.kolt.transfer.domain.repositories

import com.mkrs.kolt.base.webservices.MKTGenericResponse
import com.mkrs.kolt.transfer.domain.models.FinalProductModel

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.transfer.domain
 * Date: 27 / 05 / 2024
 *****/
interface TransferRepository {
    suspend fun getCodePT(claveMaterial: String): MKTGenericResponse<String>
    suspend fun postDetailInventory(
        claveMaterial: String,
        codigoUnico: String
    ): MKTGenericResponse<FinalProductModel>
}