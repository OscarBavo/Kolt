package com.mkrs.kolt.transfer.data

import com.mkrs.kolt.base.webservices.GenericResponse
import com.mkrs.kolt.transfer.domain.repositories.TransferRepository
import com.mkrs.kolt.transfer.domain.models.FinalProductModel

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.transfer.data
 * Date: 01 / 06 / 2024
 *****/
class TransferRepositoryImp(/*aqui debe ir la clase del api*/) : TransferRepository {
    override suspend fun getCodePT(claveMaterial: String): GenericResponse<String> {
        return if (claveMaterial.startsWith("710")) {
            GenericResponse.Success(
                "Error",
                200,
                "No existe PR en SAP para la clave ingresada, verifique"
            )
        } else if (claveMaterial.startsWith("000")) {
            GenericResponse.Failed("No existe", "Error conexión", 404, "no tiene conexion")
        } else {
            GenericResponse.Success(
                "OK",
                200,
                "OK"
            )
        }
    }

    override suspend fun postDetailInventory(
        claveMaterial: String,
        codigoUnico: String
    ): GenericResponse<FinalProductModel> {
        TODO("Not yet implemented")
    }


}