package com.mkrs.kolt.preferences.di

import android.content.Context
import com.mkrs.kolt.utils.MKTSecureSharedPreference

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.preferences.di
 * Date: 08 / 05 / 2024
 *****/
object HomeModule {

    @Volatile
    private var homePreferences: MKTSecureSharedPreference? = null

    fun provideHomePReferences(context: Context, name: String): MKTSecureSharedPreference {
        return homePreferences ?: MKTSecureSharedPreference.newInstance(context, name)
            .also { homePreferences = it }
    }
}