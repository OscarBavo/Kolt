package com.mkrs.kolt.input.di

import android.app.Application
import com.mkrs.kolt.input.data.OutputRepositoryImp
import com.mkrs.kolt.input.domain.repositories.OutputRepository
import com.mkrs.kolt.input.domain.usecase.output.GetOutputCodePTUseCase
import com.mkrs.kolt.input.domain.usecase.output.GetOutputDateUseCase
import com.mkrs.kolt.input.domain.usecase.output.PostCreateOutputUseCase
import com.mkrs.kolt.input.domain.usecase.output.PostOutputItemDetailUseCase
import com.mkrs.kolt.input.presentation.output.OutputViewModelFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.input.di
 * Date: 25 / 09 / 2024
 *****/

@Module
@InstallIn(SingletonComponent::class)
object OutputModule {
    @Singleton
    @Provides
    fun providesOutputRepository(): OutputRepository {
        return OutputRepositoryImp
    }

    @Singleton
    @Provides
    fun providesOutputViewModelFactory(application: Application): OutputViewModelFactory {
        return OutputViewModelFactory(
            application,
            providesOutputGetCodePTUseCase(),
            providesOutputGetDateUseCase(),
            providesOutputPostDetailItemUseCase(),
            providesOutputPostCreateUseCase()
        )
    }

    @Singleton
    @Provides
    fun providesOutputGetCodePTUseCase() = GetOutputCodePTUseCase(providesOutputRepository())

    @Singleton
    @Provides
    fun providesOutputGetDateUseCase() = GetOutputDateUseCase(providesOutputRepository())

    @Singleton
    @Provides
    fun providesOutputPostDetailItemUseCase() =
        PostOutputItemDetailUseCase(providesOutputRepository())

    @Singleton
    @Provides
    fun providesOutputPostCreateUseCase() = PostCreateOutputUseCase(providesOutputRepository())

}