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

        fun update(serviceURL: String) {
            BASE_URL_KOLT = serviceURL
        }
    }

    class Transfer {
        companion object {
            val GET_CODE: String = "$BASE_URL_KOLT/GETCodigoPT"
            val POST_DETAIL: String = "$BASE_URL_KOLT/PostDetallesExistencia"
            val POST_TRANSFER: String = "$BASE_URL_KOLT/CrearTransferenciaInventario"
        }
    }

    class InOut {
        companion object {
            val POST_IN: String = "$BASE_URL_KOLT/CrearEntradaInventario"
            val POST_OUT: String = "$BASE_URL_KOLT/CrearSalidaInventario"
            val GET_OUT_RFC: String = "$BASE_URL_KOLT/GetProveedorNombreRazonSocial"
            val POST_OUT_DETAIL: String = "$BASE_URL_KOLT/GetExistenciaMPAlmacenSalida"
        }
    }
}