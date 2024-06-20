package com.mkrs.kolt.base.conectivity.webservice

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.base.conectivity.webservice
 * Date: 18 / 06 / 2024
 *****/
class APIKolt {
    companion object {
        private var BASE_URL_KOLT = ""

        @JvmStatic
        fun init(serviceURL: String) {
            BASE_URL_KOLT = serviceURL
        }
    }

    class Transfer{
        companion object{
            val GET_CODE:String="$BASE_URL_KOLT/GETCodigoPT"
            val POST_DETAIL:String="$BASE_URL_KOLT/PostDetallesExistencia"
        }
    }
}