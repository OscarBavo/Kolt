package com.mkrs.kolt.transfer.data

import com.mkrs.kolt.base.conectivity.webservice.MKTGeneralConfig
import com.mkrs.kolt.base.webservices.MKTGenericResponse
import com.mkrs.kolt.base.webservices.common.ErrorResponse
import com.mkrs.kolt.base.webservices.entity.TransferInventoryRequest
import com.mkrs.kolt.base.webservices.entity.TransferRequest
import com.mkrs.kolt.base.webservices.entity.TransferUniqueCodeResponse
import com.mkrs.kolt.base.webservices.transfer.MKTTransferGetCodeService
import com.mkrs.kolt.base.webservices.transfer.MKTTransferPostUniqueCodeService
import com.mkrs.kolt.base.webservices.transfer.MKTTransferPostTransferService
import com.mkrs.kolt.transfer.domain.repositories.TransferRepository
import com.mkrs.kolt.transfer.domain.models.FinalProductModel
import com.mkrs.kolt.transfer.webservices.models.TransferResponse
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
    private const val ERROR_ZERO = "0"
    override suspend fun getCodePT(claveMaterial: String, isDummy:Boolean): MKTGenericResponse<String> =
        withContext(Dispatchers.IO) {
            return@withContext if (isDummy) {
                MKTGenericResponse.Success("7135030")
            } else {
                val service = MKTTransferGetCodeService(claveMaterial)
                val response = convertToSuspend(service, MKTGeneralConfig.CODE_SUCCESS.toString())
                return@withContext if (response.ErrorCode == ERROR_ZERO) {
                    response.Result?.uniqueCode?.let { code ->
                        if (code.isEmpty()) {
                            MKTGenericResponse.Success(ERROR_ZERO)
                        } else {
                            MKTGenericResponse.Success(code)
                        }
                    } ?: run {
                        MKTGenericResponse.Success(ERROR_ZERO)
                    }
                } else {
                    MKTGenericResponse.Failed(
                        response.Message ?: ""
                    )
                }
            }
        }


    override suspend fun postDetailInventory(
        request: TransferInventoryRequest,
        isDummy: Boolean
    ): MKTGenericResponse<FinalProductModel> =
        withContext(Dispatchers.IO) {
            return@withContext if (isDummy) {
                MKTGenericResponse.Success(
                    createInventoryModel(
                        TransferUniqueCodeResponse(
                            "7135030", "Oscar", "50", "AD403769", "AD403769", "AD430",
                            ErrorResponse("", "", false, "OK", 0, "OK")
                        )
                    )
                )
            } else {
                val service = MKTTransferPostUniqueCodeService(request)
                val response = convertToSuspend(service, MKTGeneralConfig.CODE_SUCCESS.toString())
                return@withContext if (response.ErrorCode == ERROR_ZERO) {
                    MKTGenericResponse.Success(createInventoryModel(response.Result))
                } else {
                    MKTGenericResponse.Failed(response.ErrorCode)
                }
            }
        }

    override suspend fun postTransfer(
        request: TransferRequest,
        isDummy: Boolean
    ): MKTGenericResponse<ErrorResponse> =
        withContext(Dispatchers.IO) {
            return@withContext if (isDummy) {
                MKTGenericResponse.Success(
                    createResponse(
                        TransferResponse(
                            ErrorResponse(
                                "0",
                                "0",
                                false,
                                "OK",
                                4,
                                "7133238",
                                FechaHora = "27/06/2024 19:09"
                            )
                        )
                    )
                )
            } else {
                val service = MKTTransferPostTransferService(request)
                val response = convertToSuspend(service, MKTGeneralConfig.CODE_SUCCESS.toString())
                return@withContext if (response.ErrorCode == ERROR_ZERO) {
                    MKTGenericResponse.Success(createResponse(response.Result))
                } else {
                    MKTGenericResponse.Failed(response.ErrorCode)
                }
            }
        }

    private fun createResponse(result: TransferResponse?): ErrorResponse {
        return result?.transfer ?: kotlin.run {
            ErrorResponse()
        }
    }


    private fun createInventoryModel(result: TransferUniqueCodeResponse?): FinalProductModel {
        return result?.let {
            FinalProductModel(
                result.ItemCode,
                result.MnfSerial,
                result.Quantity,
                result.ItemName,
                result.SuppCatNum,
                result.PedidoProg
            )
        } ?: kotlin.run {
            FinalProductModel()
        }
    }

}