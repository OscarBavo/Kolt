package com.mkrs.kolt.presentation.di

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt
 * Date: 30 / 04 / 2024
 *****/


@HiltAndroidApp
class KoltApp : Application() {
    companion object {
        lateinit var instance: KoltApp
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}