package com.mkrs.kolt.base.conectivity.webservice

import com.android.volley.AuthFailureError
import com.android.volley.Header
import com.android.volley.Request
import com.android.volley.toolbox.BaseHttpStack
import com.android.volley.toolbox.HttpResponse
import com.android.volley.toolbox.HurlStack
import com.mkrs.kolt.base.conectivity.webservice.MKTGeneralConfig.Companion.CONTENT_TYPE
import java.io.DataOutputStream
import java.io.FilterInputStream
import java.io.IOException
import java.io.InputStream
import java.lang.IllegalStateException
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.SSLSocketFactory

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.base.webservices.wsInterface
 * Date: 02 / 06 / 2024
 *****/
class MKTHurlStack : BaseHttpStack {
    companion object {
        const val HTTP_CONTINUE = 100
    }

    private lateinit var urlRewriter: HurlStack.UrlRewriter
    private lateinit var sslSocketFactory: SSLSocketFactory

    constructor() {
    }

    constructor(urlRewriter: UrlRewriter, sslSocketFactory: SSLSocketFactory) {
        this.urlRewriter = urlRewriter
        this.sslSocketFactory = sslSocketFactory
    }

    interface UrlRewriter : HurlStack.UrlRewriter {
        fun rewriterUrl(originalString: String): String
    }


    private fun hasResponseBody(method: Int, responseCode: Int): Boolean {
        return method != Request.Method.HEAD
                && !(HTTP_CONTINUE <= responseCode && responseCode < HttpURLConnection.HTTP_OK)
                && responseCode != HttpURLConnection.HTTP_NO_CONTENT
                && responseCode != HttpURLConnection.HTTP_NOT_MODIFIED
    }

    private fun convertHeaders(headerFields: Map<String, MutableList<String>>): MutableList<Header>? {
        val headerList: MutableList<Header> = mutableListOf()
        for (entry: Map.Entry<String, List<String>> in headerFields.entries) {
            for (value: String in entry.value) {
                headerList.add(Header(entry.key, value))
            }
        }
        return headerList
    }

    @Throws(IOException::class, AuthFailureError::class)
    override fun executeRequest(
        request: Request<*>?,
        additionalHeaders: MutableMap<String, String>?
    ): HttpResponse? {
        var url = request?.url
        additionalHeaders?.putAll(request?.headers!!)
        val rewritten = urlRewriter.rewriteUrl(url)

        if (rewritten.isNullOrEmpty()) {
            throw IOException("Url blocked by reqwriter: $url")
        }
        url = rewritten
        val parseUrl = URL(url)
        val connection = HttpURLConnection(parseUrl, request!!)

        var keepConnectionOpen = false
        return try {
            for (headerName: String in additionalHeaders?.keys!!) {
                connection.setRequestProperty(headerName, additionalHeaders[headerName])
            }
            setConnectionParamsRequest(connection, request)

            val responseCode = connection.responseCode

            if (responseCode == -1) {
                throw IOException("Could not retrieve response code from HttpUrlConnection")
            }

            if (!hasResponseBody(request.method, responseCode)) {
                HttpResponse(responseCode, convertHeaders(connection.headerFields))
            }

            keepConnectionOpen = true

            HttpResponse(
                responseCode,
                convertHeaders(connection.headerFields),
                connection.contentLength,
                MKTHurlStack().UrlConnectionInputStream(connection)
            )
        } finally {
            if (!keepConnectionOpen) {
                connection.disconnect()
            }
        }
    }

    @Throws(IOException::class, AuthFailureError::class)
    private fun setConnectionParamsRequest(connection: HttpURLConnection, req: Request<*>) {
        when (req.method) {
            Request.Method.DEPRECATED_GET_OR_POST -> {
                val postBody = req.body
                if (postBody.isNotEmpty()) {
                    connection.requestMethod = "POST"
                    addBody(connection, req, postBody)
                }
            }

            Request.Method.GET -> connection.requestMethod = "GET"
            Request.Method.DELETE -> connection.requestMethod = "DELETE"
            Request.Method.POST -> {
                connection.requestMethod = "POST"
                addBodyIfExists(connection, req)
            }

            Request.Method.PUT -> {
                connection.requestMethod = "PUT"
                addBodyIfExists(connection, req)
            }

            Request.Method.HEAD -> connection.requestMethod = "HEAD"
            Request.Method.OPTIONS -> connection.requestMethod = "OPTIONS"
            Request.Method.TRACE -> connection.requestMethod = "TRACE"
            Request.Method.PATCH -> {
                connection.requestMethod = "PATCH"
                addBodyIfExists(connection, req)
            }

            else -> throw IllegalStateException("Unknown method type")

        }
    }

    @Throws(IOException::class, AuthFailureError::class)
    private fun addBodyIfExists(connection: HttpURLConnection, req: Request<*>) {
        val body = req.body
        if (body.isNotEmpty()) {
            addBody(connection, req, body)
        }
    }

    @Throws(IOException::class)
    private fun addBody(connection: HttpURLConnection, req: Request<*>, postBody: ByteArray?) {
        connection.doOutput = true
        if (connection.requestProperties.containsKey(CONTENT_TYPE)) {
            connection.setRequestProperty(CONTENT_TYPE, req.bodyContentType)
        }

        val out = DataOutputStream(connection.outputStream)
        out.write(postBody)
        out.close()
    }

    @Throws(IOException::class)
    private fun HttpURLConnection(parseUrl: URL, req: Request<*>): HttpURLConnection {
        val connection = createConnection(parseUrl)

        val timeout = req.timeoutMs
        connection.connectTimeout = timeout
        connection.readTimeout = timeout
        connection.useCaches = false
        connection.doInput = false

        return connection
    }

    @Throws(IOException::class)
    private fun createConnection(url: URL): HttpURLConnection {
        val connection = url.openConnection() as HttpURLConnection
        connection.instanceFollowRedirects = HttpURLConnection.getFollowRedirects()
        return connection
    }

    inner class UrlConnectionInputStream(connection: HttpURLConnection) :
        FilterInputStream(inputStreamFromConnection(connection)) {
        private var con = connection

        override fun close() {
            super.close()
            con.disconnect()
        }

    }

    private fun inputStreamFromConnection(connection: HttpURLConnection): InputStream {
        return try {
            connection.inputStream
        } catch (ioe: IOException) {
            connection.errorStream
        }
    }
}