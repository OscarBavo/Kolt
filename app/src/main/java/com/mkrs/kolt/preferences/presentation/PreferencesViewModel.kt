package com.mkrs.kolt.preferences.presentation

import androidx.lifecycle.ViewModel
import com.mkrs.kolt.preferences.domain.usecases.GetIntValueUseCase
import com.mkrs.kolt.preferences.domain.usecases.GetStringValueUseCase
import com.mkrs.kolt.preferences.domain.usecases.SaveIntValueUseCase
import com.mkrs.kolt.preferences.domain.usecases.SaveStringValueUseCase

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.preferences.presentation
 * Date: 06 / 05 / 2024
 *****/
class PreferencesViewModel(
    private val getIntValueUseCase: GetIntValueUseCase,
    private val getStringValueUseCase: GetStringValueUseCase,
    private val saveIntValueUseCase: SaveIntValueUseCase,
    private val saveStringValueUseCase: SaveStringValueUseCase
) : ViewModel() {

    fun saveString(key: String, value: String) {
        saveStringValueUseCase.execute(key, value)
    }

    fun saveInt(key: String, value: Int) {
        saveIntValueUseCase.execute(key, value)
    }

    fun getString(key: String, defaultValue: String) =
        getStringValueUseCase.execute(key, defaultValue = defaultValue)

    fun getInt(key: String, defaultValue: Int) = getIntValueUseCase.execute(key, defaultValue)
}