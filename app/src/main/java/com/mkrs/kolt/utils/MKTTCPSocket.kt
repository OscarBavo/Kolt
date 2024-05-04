package com.mkrs.kolt.utils

import android.content.Context
import com.mkrs.kolt.R
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.ConnectException
import java.net.Socket
import java.net.SocketException
import java.net.UnknownHostException

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.utils
 * Date: 03 / 05 / 2024
 *****/
class MKTTCPSocket {

    fun sendDataPrinter(data: String, namePrinter: String, context: Context): String {
        val ipPrinter = MKTSecureSharedPreference.newInstance(
            context,
            context.resources.getString(R.string.data_name_printer)
        ).getString(namePrinter, "")
        val portPrinter = MKTSecureSharedPreference.newInstance(
            context,
            context.resources.getString(R.string.data_name_printer_port)
        ).getInt(namePrinter, 9100)
        try {
            val socket = Socket(ipPrinter, portPrinter)
            socket.use {
                var responseString: String? = null
                it.getOutputStream().write(data.toByteArray())
                val bufferReader = BufferedReader(InputStreamReader(it.inputStream))
                while (true) {
                    val line = bufferReader.readLine() ?: break
                    responseString += line
                    if (line == "exit") break
                }
                println("Received: $responseString")
                bufferReader.close()
                it.close()
                return responseString!!
            }
        } catch (he: UnknownHostException) {
            return "An exception occurred:\n ${he.printStackTrace()}"

        } catch (ioe: IOException) {
            return "An exception occurred:\n ${ioe.printStackTrace()}"
        } catch (ce: ConnectException) {
            return "An exception occurred:\n ${ce.printStackTrace()}"

        } catch (se: SocketException) {
            return "An exception occurred:\n ${se.printStackTrace()}"
        }
    }
}