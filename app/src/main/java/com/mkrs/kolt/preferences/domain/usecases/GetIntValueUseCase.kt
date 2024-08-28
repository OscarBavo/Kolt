package com.mkrs.kolt.preferences.domain.usecases

import com.mkrs.kolt.preferences.domain.PreferenceRepository

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.preferences.domain.usecases
 * Date: 06 / 05 / 2024
 *****/
class GetIntValueUseCase (private val preferenceRepository: PreferenceRepository) {
    fun execute(key: String, defaultValue:Int?):Int{
        return defaultValue?.let {
            preferenceRepository.getIntValue(key, it)
        }?: run{
            preferenceRepository.getIntValue(key)
        }
    }
}