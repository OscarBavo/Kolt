package com.mkrs.kolt.preferences.domain.usecases

import com.mkrs.kolt.preferences.domain.PreferenceRepository

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.preferences.domain.usecases
 * Date: 06 / 05 / 2024
 *****/
class GetStringValueUseCase(private val preferenceRepository: PreferenceRepository) {
    fun execute(key: String, defaultValue:String?):String{
        return defaultValue?.let {
            preferenceRepository.getStringValue(key, it)
        }?: run{
            preferenceRepository.getStringValue(key)
        }
    }
}