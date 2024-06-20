package com.mkrs.kolt.base.conectivity.webservice

import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.google.gson.JsonArray
import com.mkrs.kolt.base.conectivity.webservice.MKTGeneralConfig.Companion.CODE_ERROR_COMMON
import com.mkrs.kolt.base.conectivity.webservice.MKTGeneralConfig.Companion.CODE_ERROR_UNAUTHORIZED_LOCATION
import com.mkrs.kolt.base.conectivity.webservice.MKTGeneralConfig.Companion.EMPTY_TEXT
import com.mkrs.kolt.base.webservices.entity.MKTResponse
import org.json.JSONException
import org.json.JSONObject
import java.io.FilterInputStream
import java.io.IOException
import java.io.InputStream
import java.net.CookieManager
import java.net.HttpURLConnection
import java.net.URLEncoder


/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.base.generic.webservice
 * Date: 05 / 06 / 2024
 *****/
abstract class MKTWebService<T>(
    var serviceUrl: String?,
    code: String = MKTGeneralConfig.CODE_SUCCESS.toString()
) : MKTFailureService() {

    companion object {
        private const val APPLICATION_JSON = "application/json"
        private const val SEMI_COLON = ";"
        private const val CHARSET = "charset=utf-8"
        private const val UTF_8 = "UTF-8"
        private const val EQUALS = "="
        private const val QUESTION_MARK = "?"
        private const val AMPERSAND = "&"
        private const val CONTENT_TYPE = "Content-type"
    }

    private var mockData: String? = null
    private var mockSuccess = false
    private var requestParam = HashMap<String, String>()
    private var multiRequestParam: MutableList<Pair<String, String?>> = mutableListOf()
    private var requestHeader = HashMap<String, String>()
    private var cookieManager = CookieManager()
    private var listener: MKTWebServiceListener<T>? = null
    var response: MKTResponse<T>
    private var codeSuccess: String? = null

    init {
        this.codeSuccess = code
        this.response = MKTResponse()
        //  this.response.tag = tag
        this.response.ErrorCode = code
    }

    fun getListener() = listener

    fun setListener(listener: MKTWebServiceListener<T>?) {
        this.listener = listener
    }

    fun addParams(name: String, value: String) {
        if (requestParam.containsKey(name)) {
            val old = requestParam[name]
            val pair = Pair(name, old)
            multiRequestParam.add(pair)
        }
        requestParam[name] = value
    }

    fun getRequestParams() = requestParam

    open fun getMultipleRequestParam(): List<Pair<String, String?>>? {
        return multiRequestParam
    }

    fun addHeader(name: String, value: String) {
        requestHeader[name] = value
    }

    open fun getRequestHeader(): java.util.HashMap<String, String> {
        return requestHeader
    }

    private fun requestJsonPost(jsonRequest: JSONObject) {
        val jsonObjRequest: JsonObjectRequest = object :
            JsonObjectRequest(Method.POST, serviceUrl, jsonRequest, onVolleySuccess(), onVolleyFailure()) {
            override fun getBodyContentType() = APPLICATION_JSON
            override fun getHeaders() = getRequestHeader()
            override fun getParams() = getRequestParams()
        }
        requestQueue(jsonObjRequest)
    }

    private fun requestStringPost() {
        val jsonObjRequest: StringRequest = object :
            StringRequest(Method.POST, serviceUrl, onVolleySuccess(), onVolleyFailure()) {
            override fun getHeaders() = getRequestHeader()
            override fun getParams() = getRequestParams()
        }
        requestQueue(jsonObjRequest)
    }

    private fun requestJsonResponseStringPost(body: JSONObject) {
        val stringRequest: StringRequest = object :
            StringRequest(Method.POST, serviceUrl, onVolleySuccess(), onVolleyFailure()) {
            override fun getBodyContentType(): String {
                return "$APPLICATION_JSON$SEMI_COLON $CHARSET"
            }

            override fun getHeaders() = getRequestHeader()
            override fun getParams() = getRequestParams()
            override fun getBody(): ByteArray {
                return body.toString().toByteArray()
            }
        }
        requestQueue(stringRequest)
    }

    private fun requestJsonPut(jsonRequest: JSONObject) {
        val jsonObjRequest: JsonObjectRequest = object :
            JsonObjectRequest(Method.PUT, serviceUrl, jsonRequest, onVolleySuccess(), onVolleyFailure()) {
            override fun getBodyContentType() = APPLICATION_JSON
            override fun getHeaders() = getRequestHeader()
            override fun getParams() = getRequestParams()
        }
        requestQueue(jsonObjRequest)
    }

    private fun requestStringPut() {
        val jsonObjRequest: StringRequest = object :
            StringRequest(Method.PUT, serviceUrl, onVolleySuccess(), onVolleyFailure()) {
            override fun getHeaders() = getRequestHeader()
            override fun getParams() = getRequestParams()
            override fun getBody() = EMPTY_TEXT.toByteArray()
        }
        requestQueue(jsonObjRequest)
    }

    private fun requestStringPatch() {
        val jsonObjRequest: StringRequest = object :
            StringRequest(Method.PATCH, serviceUrl, onVolleySuccess(), onVolleyFailure()) {
            override fun getHeaders() = getRequestHeader()
            override fun getParams() = getRequestParams()
            override fun getBody() = EMPTY_TEXT.toByteArray()
        }
        requestQueue(jsonObjRequest)
    }

    private fun requestJsonPatch(jsonRequest: JSONObject) {
        val jsonObjRequest: JsonObjectRequest = object :
            JsonObjectRequest(
                Method.PATCH,
                serviceUrl,
                jsonRequest,
                onVolleySuccess(),
                onVolleyFailure()
            ) {
            override fun getBodyContentType() = APPLICATION_JSON
            override fun getHeaders() = getRequestHeader()
            override fun getParams() = getRequestParams()
        }
        requestQueue(jsonObjRequest)
    }

    private fun requestStringDelete() {
        val jsonObjRequest: StringRequest = object :
            StringRequest(Method.DELETE, serviceUrl, onVolleySuccess(), onVolleyFailure()) {
            override fun getHeaders() = getRequestHeader()
            override fun getParams() = getRequestParams()
            override fun getUrl(): String {
                var stringBuilder = serviceUrl?.let {
                    StringBuilder(it)
                }
                stringBuilder = getUrlFormat(stringBuilder)
                return stringBuilder.toString()
            }
        }
        requestQueue(jsonObjRequest)
    }

    private fun requestStringGet() {
        val jsonObjRequest: StringRequest = object :
            StringRequest(Method.GET, serviceUrl, onVolleySuccess(), onVolleyFailure()) {
            override fun getHeaders(): Map<String, String> {
                return getRequestHeader()
            }

            override fun getParams(): Map<String, String> {
                return getRequestParams()
            }

            override fun getUrl(): String {
                var stringBuilder = serviceUrl?.let { StringBuilder(it) } /*StringBuilder(url)?.let {
                    java.lang.StringBuilder(it)
                }*/
                stringBuilder = getUrlFormat(stringBuilder)

                getMultipleRequestParam()?.map { pair ->
                    try {
                        val key = URLEncoder.encode(pair.first, UTF_8)
                        val value = URLEncoder.encode(pair.second, UTF_8)
                        stringBuilder?.append(AMPERSAND)?.append(key)?.append(EQUALS)?.append(value)
                    } catch (_: UnsupportedOperationException) {
                    }
                }
                return stringBuilder.toString()
            }
        }
        requestQueue(jsonObjRequest)
    }

    abstract fun buildRequest()

    protected abstract fun execute()

    fun start() {
        execute()
    }

    fun post(body: JSONObject?) {
        if (body != null) {
            requestJsonPost(body)
        } else {
            requestStringPost()
        }
    }

    fun post(body: String?) {
        setRequest(body, TypeRequest.POST)
    }

    fun delete() {
        requestStringDelete()
    }

    fun put(body: String?) {
        setRequest(body, TypeRequest.PUT)
    }

    fun patch(body: String?) {
        setRequest(body, TypeRequest.PATCH)
    }

    fun get() {
        requestStringGet()
    }

    private enum class TypeRequest {
        POST, PUT, PATCH
    }

    protected open fun <T> requestQueue(request: Request<T>) {
        request.setShouldCache(false)
        request.retryPolicy = DefaultRetryPolicy(
            MKTGeneralConfig.TIME_OUT,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        CookieManager.setDefault(cookieManager)
        MKTRequestQueue.addRequest(request)
    }

    private fun <T> onVolleySuccess(): Response.Listener<T> {
        return Response.Listener { response: Any? ->
            if (response != null) {
                onSuccess(this.codeSuccess, response.toString())
            }
        }
    }

    private fun <T> onVolleySuccessArray():Response.Listener<JsonArray>{
        return Response.Listener { response ->
            onSuccess(this.codeSuccess, response.toString())
        }
    }

    abstract fun onSuccess(statusCode: String?, responseString: String?)

    protected open fun onVolleyFailure(): Response.ErrorListener? {
        return Response.ErrorListener { error: VolleyError ->
            try {
                if (error.networkResponse.statusCode == CODE_ERROR_UNAUTHORIZED_LOCATION) {
                    MKTRequestQueue.cancelAll()
                    return@ErrorListener
                }
                if (error.networkResponse != null) {
                    val bodyRes: String = tryGetBody(error)
                    this.onFailure(error.networkResponse.statusCode.toString(), bodyRes, error)
                    return@ErrorListener
                }
            } catch (ig: Exception) {
            //    this.onFailure(CODE_ERROR_COMMON.toString(), null, ig.cause)
            }
            this.onFailure(CODE_ERROR_COMMON.toString(), null, error)
        }
    }

    override fun onFailure(code: String?, response: String?, throwable: Throwable?) {
        this.response.ErrorCode = CODE_ERROR_COMMON.toString()
        /*
        if(res!=null && !response.isNullOrEmpty()){
          //  val exception=MKTUtils().parse(response, )
        }*/

        listener?.onFinish(this.response)


    }

    private fun tryGetBody(error: VolleyError): String {
        if (error.networkResponse != null && error.networkResponse.data != null) {
            val encoding = error.networkResponse.headers?.get(CONTENT_TYPE)?.let {
                it.split(SEMI_COLON.toRegex()).toTypedArray()[1].split(EQUALS.toRegex())
                    .toTypedArray()[1]
            } ?: kotlin.run {
                EMPTY_TEXT
            }
            return String(error.networkResponse.data, charset(encoding))
        }
        return EMPTY_TEXT
    }

    private fun getUrlFormat(stringBuilder: StringBuilder?): StringBuilder? {
        val stringFrmt = stringBuilder
        var i = 0
        getRequestParams().map { mapItem ->
            val key: String?
            val value: String?

            try {
                key = URLEncoder.encode(mapItem.key, UTF_8)
                value = URLEncoder.encode(mapItem.value, UTF_8)
                if (i == 0) {
                    stringFrmt?.append(QUESTION_MARK)?.append(key)?.append(EQUALS)?.append(value)
                } else {
                    stringFrmt?.append(AMPERSAND)?.append(key)?.append(EQUALS)?.append(value)
                }
            } catch (_: UnsupportedOperationException) {
            }
            i++
        }
        return stringFrmt
    }

    private fun setRequest(body: String?, typeRequest: TypeRequest) {
        when (body) {
            EMPTY_TEXT -> {
                selectedTypeRequest(typeRequest)
            }
            else -> {
                val jsonObj: JSONObject
                try {
                    jsonObj = JSONObject(body)
                    selectedTypeRequestWithBody(jsonObj, typeRequest)
                } catch (jex: JSONException) {
                    this.onFailure(CODE_ERROR_COMMON.toString(), EMPTY_TEXT, jex)
                }
            }
        }
    }

    private fun selectedTypeRequest(typeRequest: TypeRequest) {
        when (typeRequest) {
            TypeRequest.POST -> requestStringPost()
            TypeRequest.PUT -> requestStringPut()
            TypeRequest.PATCH -> requestStringPatch()
        }
    }

    private fun selectedTypeRequestWithBody(json: JSONObject, typeRequest: TypeRequest) {
        when (typeRequest) {
            TypeRequest.POST -> requestJsonPost(json)
            TypeRequest.PUT -> requestJsonPut(json)
            TypeRequest.PATCH -> requestJsonPatch(json)
        }
    }
}