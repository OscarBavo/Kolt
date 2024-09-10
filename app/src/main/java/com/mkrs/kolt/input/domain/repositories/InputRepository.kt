package com.mkrs.kolt.input.domain.repositories

import com.mkrs.kolt.base.webservices.MKTGenericResponse

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.input.domain.repositories
 * Date: 29 / 08 / 2024
 *****/
interface InputRepository {
    suspend fun getKey(isDummy: Boolean = false): MKTGenericResponse<String>
}