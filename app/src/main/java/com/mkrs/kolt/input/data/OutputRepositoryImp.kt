package com.mkrs.kolt.input.data

import com.mkrs.kolt.base.conectivity.webservice.MKTGeneralConfig
import com.mkrs.kolt.base.webservices.MKTGenericResponse
import com.mkrs.kolt.base.webservices.common.ErrorResponse
import com.mkrs.kolt.base.webservices.output.MKTOutputGetDateService
import com.mkrs.kolt.base.webservices.output.MKTOutputPostCreateService
import com.mkrs.kolt.base.webservices.output.MKTOutputPostDetailService
import com.mkrs.kolt.base.webservices.transfer.MKTTransferGetCodeService
import com.mkrs.kolt.input.domain.entity.OutputDetailRequest
import com.mkrs.kolt.input.domain.entity.OutputRequest
import com.mkrs.kolt.input.domain.repositories.OutputRepository
import com.mkrs.kolt.input.webservices.models.OutputDateResponse
import com.mkrs.kolt.utils.CONSTANST
import com.mkrs.kolt.utils.convertToSuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.input.data
 * Date: 25 / 09 / 2024
 *****/
object OutputRepositoryImp : OutputRepository {
    override suspend fun getCodePT(
        claveMaterial: String,
        isDummy: Boolean
    ) = withContext(Dispatchers.IO) {
        return@withContext if (isDummy) {
            MKTGenericResponse.Success("7135030")
        } else {
            val service = MKTTransferGetCodeService(claveMaterial)
            val response = convertToSuspend(service, MKTGeneralConfig.CODE_SUCCESS.toString())
            return@withContext if (response.ErrorCode == CONSTANST.ERROR_ZERO) {
                response.Result?.uniqueCode?.let { code ->
                    if (code.isEmpty()) {
                        MKTGenericResponse.Success(CONSTANST.ERROR_ZERO)
                    } else {
                        MKTGenericResponse.Success(code)
                    }
                } ?: run {
                    MKTGenericResponse.Success(CONSTANST.ERROR_ZERO)
                }
            } else {
                MKTGenericResponse.Failed(
                    response.Message ?: CONSTANST.EMPTY_DATA
                )
            }
        }
    }

    override suspend fun getDate(
        keyWms: String,
        isDummy: Boolean
    ) = withContext(Dispatchers.IO) {
        return@withContext if (isDummy) {
            MKTGenericResponse.Success(
                ErrorResponse("3533", "", false, "OK", 0, "25/09/2024 21:26:23")
            )
        } else {
            val service = MKTOutputGetDateService(keyWms)
            val response = convertToSuspend(service, MKTGeneralConfig.CODE_SUCCESS.toString())
            return@withContext if (!response.EsError) {
                MKTGenericResponse.Success(createResponseDate(response.Result))
            } else {
                MKTGenericResponse.Failed(CONSTANST.NO_DATE)
            }

        }
    }

    private fun createResponseDate(result: OutputDateResponse?): ErrorResponse {
        return result?.dateResponse ?: kotlin.run { ErrorResponse() }
    }

    override suspend fun postDetail(
        detail: OutputDetailRequest,
        isDummy: Boolean
    ) = withContext(Dispatchers.IO) {
        return@withContext if (isDummy) {
            MKTGenericResponse.Success(
                ErrorResponse(
                    "3533",
                    "0",
                    false,
                    "OK",
                    ObjType = 0,
                    Result = "300",
                    FechaHora = "25/09/2024 21:26:23"
                )
            )
        } else {
            val service = MKTOutputPostDetailService(detail)
            val response = convertToSuspend(service, MKTGeneralConfig.CODE_SUCCESS.toString())
            return@withContext if (!response.EsError) {
                MKTGenericResponse.Success(createResponseDate(response.Result))
            } else {
                MKTGenericResponse.Failed(CONSTANST.NO_DATE)
            }
        }
    }

    override suspend fun postCreateOutput(
        outputRequest: OutputRequest,
        isDummy: Boolean
    ) = withContext(Dispatchers.IO) {
        return@withContext if (isDummy) {
            MKTGenericResponse.Success(
                ErrorResponse(
                    "3533",
                    "0",
                    false,
                    "OK",
                    ObjType = 0,
                    Result = "300",
                    FechaHora = "25/09/2024 21:26:23"
                )
            )
        } else {
            val service = MKTOutputPostCreateService(outputRequest)
            val response = convertToSuspend(service, MKTGeneralConfig.CODE_SUCCESS.toString())
            return@withContext if (!response.EsError) {
                MKTGenericResponse.Success(createResponseDate(response.Result))
            } else {
                MKTGenericResponse.Failed(CONSTANST.NO_DATE)
            }
        }
    }
}