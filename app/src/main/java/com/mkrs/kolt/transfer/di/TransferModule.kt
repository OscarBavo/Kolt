package com.mkrs.kolt.transfer.di

import com.mkrs.kolt.transfer.data.TransferRepositoryImp
import com.mkrs.kolt.transfer.domain.repositories.TransferRepository
import com.mkrs.kolt.transfer.domain.usecase.GetCodePTUseCase
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

    /*
       @Singleton
    @Provides
    fun providesGetStringUseCase(preference: PreferenceRepositoryImp) =
        GetStringValueUseCase(preference)
     */

    @Singleton
    @Provides
    fun providesTransferRepository() = TransferRepositoryImp()

    @Singleton
    @Provides
    fun providesGetCodePTUseCase(transferRepo: TransferRepositoryImp) =
        GetCodePTUseCase(transferRepo)

    @Singleton
    @Provides
    fun providesPostDetailInventoryUseCase(transferRepo: TransferRepositoryImp) =
        GetCodePTUseCase(transferRepo)

}