package com.mkrs.kolt.preferences.di

import com.mkrs.kolt.preferences.data.PreferenceRepositoryImp
import com.mkrs.kolt.preferences.domain.usecases.GetIntValueUseCase
import com.mkrs.kolt.preferences.domain.usecases.GetStringValueUseCase
import com.mkrs.kolt.preferences.domain.usecases.SaveIntValueUseCase
import com.mkrs.kolt.preferences.domain.usecases.SaveStringValueUseCase
import com.mkrs.kolt.preferences.presentation.PreferenceViewModelFactory
import com.mkrs.kolt.utils.MKTSecureSharedPreference

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.preferences.di
 * Date: 06 / 05 / 2024
 *****/
object PreferenceModule {

    fun providePreferenceVMFactory(preference: MKTSecureSharedPreference): PreferenceViewModelFactory {
        val singletonRepository = providesPreferenceRepository(preference)
        return PreferenceViewModelFactory(
            providesSaveStringUseCase(singletonRepository),
            providesSaveIntUseCase(singletonRepository),
            providesGetStringUseCase(singletonRepository),
            providesGetIntUseCase(singletonRepository)
        )
    }

    private fun providesGetStringUseCase(preference: PreferenceRepositoryImp) =
        GetStringValueUseCase(preference)

    private fun providesGetIntUseCase(preference: PreferenceRepositoryImp) =
        GetIntValueUseCase(preference)

    private fun providesSaveStringUseCase(preferences: PreferenceRepositoryImp) =
        SaveStringValueUseCase(preferences)

    private fun providesSaveIntUseCase(preferences: PreferenceRepositoryImp) =
        SaveIntValueUseCase(preferences)

    private fun providesPreferenceRepository(preference: MKTSecureSharedPreference) =
        PreferenceRepositoryImp(preference)


}