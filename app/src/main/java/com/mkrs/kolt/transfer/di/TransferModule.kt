package com.mkrs.kolt.transfer.di

import android.app.Application
import com.mkrs.kolt.transfer.data.TransferRepositoryImp
import com.mkrs.kolt.transfer.domain.repositories.TransferRepository
import com.mkrs.kolt.transfer.domain.usecase.GetCodePTUseCase
import com.mkrs.kolt.transfer.domain.usecase.PostDetailInventoryUseCase
import com.mkrs.kolt.transfer.domain.usecase.PostTransferUseCase
import com.mkrs.kolt.transfer.presentation.TransferViewModelFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.transfer.di
 * Date: 01 / 06 / 2024
 *****/

@Module
@InstallIn(SingletonComponent::class)
object TransferModule {

    @Singleton
    @Provides
    fun providesTransferRepository(): TransferRepository {
        return TransferRepositoryImp
    }

    @Singleton
    @Provides
    fun providesTransferViewModelFactory(application: Application): TransferViewModelFactory {
        return TransferViewModelFactory(
            application,
            providesGetCodePTUseCase(),
            providesPostDetailInventoryUseCase(),
            providesPostTransferUseCase()
        )
    }

    @Singleton
    @Provides
    fun providesGetCodePTUseCase() =
        GetCodePTUseCase(providesTransferRepository())

    @Singleton
    @Provides
    fun providesPostDetailInventoryUseCase() =
        PostDetailInventoryUseCase(providesTransferRepository())


    @Singleton
    @Provides
    fun providesPostTransferUseCase() =
        PostTransferUseCase(providesTransferRepository())

}