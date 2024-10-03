package com.mkrs.kolt.input.data

import com.mkrs.kolt.base.conectivity.webservice.MKTGeneralConfig
import com.mkrs.kolt.base.webservices.MKTGenericResponse
import com.mkrs.kolt.base.webservices.common.ErrorResponse
import com.mkrs.kolt.base.webservices.output.MKTOutputGetDateService
import com.mkrs.kolt.base.webservices.output.MKTOutputPostCreateService
import com.mkrs.kolt.base.webservices.output.MKTOutputPostDetailService
import com.mkrs.kolt.base.webservices.transfer.MKTTransferGetCodeService
import com.mkrs.kolt.input.domain.entity.OutPutDetailResponse
import com.mkrs.kolt.input.domain.entity.OutputDetailRequest
import com.mkrs.kolt.input.domain.entity.OutputRequest
import com.mkrs.kolt.input.domain.models.OutputPrinterModel
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
                ErrorResponse("3533", "", true, "OK", 0, "25/09/2024 21:26:23")
            )
        } else {
            val service = MKTOutputGetDateService(keyWms)
            val response = convertToSuspend(service, MKTGeneralConfig.CODE_SUCCESS.toString())
            response.EsError
            return@withContext if (!response.Result?.dateResponse?.EsError!!) {
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
                OutputPrinterModel(
                    whsCode = detail.whsCode,
                    itemCodeMP = detail.itemCodeMP,
                    itemCodePT = "ASBVS",
                    batchNumber = detail.distNumber,
                    manufacturerSerialNumber = "AS/BAS12",
                    quantity = 10.0,
                    supportCatNumber = "JMV",
                    order = "JMV01",
                    pieces = "10.0",
                    stdPack = "outputDetail.quantity",
                    itemName = "OSCAR"
                )
            )
        } else {
            val service = MKTOutputPostDetailService(detail)
            val response = convertToSuspend(service, MKTGeneralConfig.CODE_SUCCESS.toString())
            return@withContext if (!response.Result?.response?.EsError!!) {
                MKTGenericResponse.Success(createDetail(detail,response.Result))
            } else {
                MKTGenericResponse.Failed(CONSTANST.NO_DATE)
            }
        }
    }
    private fun createDetail( detail: OutputDetailRequest,result: OutPutDetailResponse?): OutputPrinterModel {
        return result?.let { outputDetail->
            OutputPrinterModel(
                whsCode = detail.whsCode,
                itemCodeMP = detail.itemCodeMP,
                itemCodePT = outputDetail.itemCode,
                batchNumber = detail.distNumber,
                manufacturerSerialNumber = outputDetail.mnfSerial,
                quantity = outputDetail.quantity.toDouble(),
                supportCatNumber = outputDetail.supportCatNumber,
                order = outputDetail.order,
                pieces = outputDetail.quantity,
                stdPack = outputDetail.quantity,
                itemName = outputDetail.itemName
            )
        }?: kotlin.run{
            OutputPrinterModel()
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
            return@withContext if (!response.Result?.dateResponse?.EsError!!) {
                MKTGenericResponse.Success(createResponseDate(response.Result))
            } else {
                MKTGenericResponse.Failed(createResponseDate(response.Result).Message)
            }
        }
    }
}