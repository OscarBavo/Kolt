package com.mkrs.kolt.input.di

import android.app.Application
import com.mkrs.kolt.input.data.InputRepositoryImp
import com.mkrs.kolt.input.domain.repositories.InputRepository
import com.mkrs.kolt.input.domain.usecase.GetInCodePTUseCase
import com.mkrs.kolt.input.domain.usecase.PostAddInUseCase
import com.mkrs.kolt.input.presentation.input.InputViewModelFactory

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.input.di
 * Date: 28 / 08 / 2024
 *****/

@Module
@InstallIn(SingletonComponent::class)
object InputModule {
    @Singleton
    @Provides
    fun providesInputRepository(): InputRepository {
        return InputRepositoryImp
    }

    @Singleton
    @Provides

    fun providesInputViewModelFactory(application: Application): InputViewModelFactory {
        return InputViewModelFactory(application, providesInGetCodePTUseCase(), providesInUseCase())
    }

    @Singleton
    @Provides
    fun providesInGetCodePTUseCase() =
        GetInCodePTUseCase(providesInputRepository())

    @Singleton
    @Provides
    fun providesInUseCase() =
        PostAddInUseCase(providesInputRepository())
}