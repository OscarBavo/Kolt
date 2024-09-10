package com.mkrs.kolt.input.data

import com.mkrs.kolt.base.webservices.MKTGenericResponse
import com.mkrs.kolt.input.domain.repositories.InputRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.input.data
 * Date: 28 / 08 / 2024
 *****/
object InputRepositoryImp:InputRepository {
    override suspend fun getKey(isDummy:Boolean): MKTGenericResponse<String> =

        withContext(Dispatchers.IO) {
        return@withContext if(isDummy) {
            MKTGenericResponse.Success("Hola")
        }else{
            MKTGenericResponse.Success("Hola")
        }
    }
}