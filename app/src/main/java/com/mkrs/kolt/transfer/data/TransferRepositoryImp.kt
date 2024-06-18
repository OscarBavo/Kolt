package com.mkrs.kolt.transfer.data

import com.mkrs.kolt.base.webservices.MKTGenericResponse
import com.mkrs.kolt.transfer.domain.repositories.TransferRepository
import com.mkrs.kolt.transfer.domain.models.FinalProductModel

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.transfer.data
 * Date: 01 / 06 / 2024
 *****/
object TransferRepositoryImp: TransferRepository {
    override suspend fun getCodePT(claveMaterial: String): MKTGenericResponse<String> {
        return if (claveMaterial.startsWith("710")) {
            MKTGenericResponse.Success(
                "Error",
                200,
                "No existe PR en SAP para la clave ingresada, verifique"
            )
        } else if (claveMaterial.startsWith("000")) {
            MKTGenericResponse.Failed("No existe", "Error conexión", 404, "no tiene conexion")
        } else {
            MKTGenericResponse.Success(
                "OK",
                200,
                "OK"
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