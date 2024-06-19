package com.mkrs.kolt.base.conectivity.webservice


/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.base.webservices
 * Date: 02 / 06 / 2024
 *****/
class MKTHttpClient {
    private var hurlStack: MKTHurlStack = MKTHurlStack()

    private fun createOrUpdateHurlStack() {
        if (hurlStack == null) {
            hurlStack = MKTHurlStack()
        }
    }

    fun getHurlStack(): MKTHurlStack {
        createOrUpdateHurlStack()
        return hurlStack
    }
}