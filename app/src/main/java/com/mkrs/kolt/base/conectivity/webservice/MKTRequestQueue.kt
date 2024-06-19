package com.mkrs.kolt.base.conectivity.webservice

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.base.webservices
 * Date: 02 / 06 / 2024
 *****/
object MKTRequestQueue {
    lateinit var applicationContext: Context

    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(applicationContext, MKTHttpClient().getHurlStack())
    }

    @JvmStatic
    fun init(applicationContext: Context) {
        MKTRequestQueue.applicationContext = applicationContext
    }

    @JvmStatic
    fun <T> addRequest(request: Request<T>) {
        requestQueue.add(request)
    }

    fun cancelAll() {
        requestQueue.cancelAll { true }
    }
}