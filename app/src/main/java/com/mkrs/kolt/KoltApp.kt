package com.mkrs.kolt

import android.app.Application
import com.mkrs.kolt.base.conectivity.webservice.APIKolt
import com.mkrs.kolt.base.conectivity.webservice.MKTRequestQueue
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
        APIKolt.init(getString(R.string.default_web_service))
        MKTRequestQueue.init(this)
    }
}