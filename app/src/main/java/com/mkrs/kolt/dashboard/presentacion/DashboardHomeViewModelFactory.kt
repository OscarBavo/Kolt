package com.mkrs.kolt.dashboard.presentacion

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mkrs.kolt.preferences.domain.usecases.GetIntValueUseCase
import com.mkrs.kolt.preferences.domain.usecases.GetStringValueUseCase
import com.mkrs.kolt.preferences.domain.usecases.SaveIntValueUseCase
import com.mkrs.kolt.preferences.domain.usecases.SaveStringValueUseCase
import com.mkrs.kolt.preferences.presentation.PreferencesViewModel

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.dashboard.presentacion
 * Date: 06 / 05 / 2024
 *****/
class DashboardHomeViewModelFactory(
    private val saveStringValueUseCase: SaveStringValueUseCase,
    private val saveIntValueUseCase: SaveIntValueUseCase,
    private val getStringValueUseCase: GetStringValueUseCase,
    private val getIntValueUseCase: GetIntValueUseCase
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PreferencesViewModel::class.java))
            return PreferencesViewModel(
                getIntValueUseCase,
                getStringValueUseCase,
                saveIntValueUseCase,
                saveStringValueUseCase
            ) as T
        throw IllegalArgumentException("Error de clase")
    }
}