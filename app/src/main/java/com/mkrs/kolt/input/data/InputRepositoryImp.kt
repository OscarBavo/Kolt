package com.mkrs.kolt.input.data

import com.mkrs.kolt.base.conectivity.webservice.MKTGeneralConfig
import com.mkrs.kolt.base.webservices.MKTGenericResponse
import com.mkrs.kolt.base.webservices.common.ErrorResponse
import com.mkrs.kolt.base.webservices.inout.MKTInAddService
import com.mkrs.kolt.base.webservices.transfer.MKTTransferGetCodeService
import com.mkrs.kolt.input.domain.entity.InputRequest
import com.mkrs.kolt.input.domain.repositories.InputRepository
import com.mkrs.kolt.input.webservices.models.InAddResponse
import com.mkrs.kolt.utils.CONSTANST.Companion.ERROR_ZERO
import com.mkrs.kolt.utils.convertToSuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.input.data
 * Date: 28 / 08 / 2024
 *****/
object InputRepositoryImp : InputRepository {
    override suspend fun getCodePT(
        claveMaterial: String,
        isDummy: Boolean
    ): MKTGenericResponse<String> =
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

    override suspend fun createIn(
        request: InputRequest,
        isDummy: Boolean
    ): MKTGenericResponse<ErrorResponse> =
        withContext(Dispatchers.IO) {
            return@withContext if (isDummy) {
                MKTGenericResponse.Success(
                    ErrorResponse("3533", "", false, "OK", 0, "OK")
                )

            } else {
                val service = MKTInAddService(request)
                val response = convertToSuspend(service, MKTGeneralConfig.CODE_SUCCESS.toString())
                return@withContext if (response.ErrorCode == ERROR_ZERO) {
                    MKTGenericResponse.Success(createResponse(response.Result))
                } else {
                    MKTGenericResponse.Failed(response.ErrorCode)
                }
            }
        }

    private fun createResponse(result: InAddResponse?): ErrorResponse {
        return result?.createIn ?: kotlin.run {
            ErrorResponse()
        }
    }
}