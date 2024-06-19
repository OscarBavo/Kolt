package com.mkrs.kolt.transfer.data

import com.mkrs.kolt.base.conectivity.webservice.MKTGeneralConfig
import com.mkrs.kolt.base.webservices.MKTGenericResponse
import com.mkrs.kolt.base.webservices.transfer.MKTTransferGetCodeService
import com.mkrs.kolt.transfer.domain.repositories.TransferRepository
import com.mkrs.kolt.transfer.domain.models.FinalProductModel
import com.mkrs.kolt.utils.convertToSuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.transfer.data
 * Date: 01 / 06 / 2024
 *****/
object TransferRepositoryImp : TransferRepository {
    override suspend fun getCodePT(claveMaterial: String): MKTGenericResponse<String> =
        withContext(Dispatchers.IO) {
            val service = MKTTransferGetCodeService(claveMaterial)
            val response = convertToSuspend(service, MKTGeneralConfig.CODE_SUCCESS.toString())
            return@withContext if (response.ErrorCode == "0") {
                response.Result?.uniqueCode?.let { code ->
                    if (code.isEmpty()) {
                        MKTGenericResponse.Success("0")
                    } else {
                        MKTGenericResponse.Success(code)
                    }
                } ?: run {
                    MKTGenericResponse.Success("0")
                }
            } else {
                MKTGenericResponse.Failed(
                    response.Message ?: ""
                )
            }
        }


    override suspend fun postDetailInventory(
        claveMaterial: String,
        codigoUnico: String
    ): MKTGenericResponse<FinalProductModel> {
        return MKTGenericResponse.Success(
            FinalProductModel(
                "HB001",
                "16058",
                "100",
                "OSCAR",
                "HBQRO",
                "9140874"
            )
        )

    }


}