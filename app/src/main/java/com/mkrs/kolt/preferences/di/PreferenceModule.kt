package com.mkrs.kolt.preferences.di

import com.mkrs.kolt.preferences.data.PreferenceRepositoryImp
import com.mkrs.kolt.preferences.domain.usecases.GetIntValueUseCase
import com.mkrs.kolt.preferences.domain.usecases.GetStringValueUseCase
import com.mkrs.kolt.preferences.domain.usecases.SaveIntValueUseCase
import com.mkrs.kolt.preferences.domain.usecases.SaveStringValueUseCase
import com.mkrs.kolt.preferences.presentation.PreferenceViewModelFactory
import com.mkrs.kolt.utils.MKTSecureSharedPreference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.preferences.di
 * Date: 06 / 05 / 2024
 *****/

@Module
@InstallIn(SingletonComponent::class)
object PreferenceModule {

    @Singleton
    @Provides
    fun providePreferenceVMFactory(preference: MKTSecureSharedPreference): PreferenceViewModelFactory {
        val singletonRepository = providesPreferenceRepository(preference)
        return PreferenceViewModelFactory(
            providesSaveStringUseCase(singletonRepository),
            providesSaveIntUseCase(singletonRepository),
            providesGetStringUseCase(singletonRepository),
            providesGetIntUseCase(singletonRepository)
        )
    }

    @Singleton
    @Provides
    fun providesGetStringUseCase(preference: PreferenceRepositoryImp) =
        GetStringValueUseCase(preference)

    @Singleton
    @Provides
    fun providesGetIntUseCase(preference: PreferenceRepositoryImp) =
        GetIntValueUseCase(preference)

    @Singleton
    @Provides
    fun providesSaveStringUseCase(preferences: PreferenceRepositoryImp) =
        SaveStringValueUseCase(preferences)

    @Singleton
    @Provides
    fun providesSaveIntUseCase(preferences: PreferenceRepositoryImp) =
        SaveIntValueUseCase(preferences)

    @Singleton
    @Provides
    fun providesPreferenceRepository(preference: MKTSecureSharedPreference) =
        PreferenceRepositoryImp(preference)


}