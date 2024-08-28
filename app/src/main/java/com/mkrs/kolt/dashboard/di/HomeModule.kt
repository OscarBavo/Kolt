package com.mkrs.kolt.dashboard.di

import android.content.Context
import com.mkrs.kolt.utils.MKTSecureSharedPreference

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.presentation.di
 * Date: 06 / 05 / 2024
 *****/
object HomeModule {
    @Volatile
    private var homePreference: MKTSecureSharedPreference? = null

    fun providesHomePreferences(context: Context, name: String): MKTSecureSharedPreference {
        return homePreference ?: MKTSecureSharedPreference.newInstance(context, name)
            .also { homePreference = it }
    }
}