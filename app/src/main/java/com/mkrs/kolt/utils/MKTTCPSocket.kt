package com.mkrs.kolt.utils

import androidx.lifecycle.MutableLiveData
import com.mkrs.kolt.dashboard.home.printer.PrinterUIState
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

    fun sendDataPrinter(
        data: String,
        ip: String,
        port: Int,
        printerStatus: MutableLiveData<PrinterUIState>
    ) {

        try {
            val socket = Socket(ip, port)
            socket.use {
                printerStatus.postValue(PrinterUIState.Loading)
                it.getOutputStream().write(data.toByteArray())
                val bufferReader = BufferedReader(InputStreamReader(it.inputStream))
                var line = ""
                while (true) {
                    line = bufferReader.readLine() ?: break
                    break
                }
                bufferReader.close()
                it.close()
                printerStatus.postValue(PrinterUIState.Printed(line))
            }
        } catch (he: UnknownHostException) {
            val error = "An exception occurred:\n ${he.printStackTrace()}"
            printerStatus.postValue(PrinterUIState.Printed(error))

        } catch (ioe: IOException) {
            val error = "An exception occurred:\n ${ioe.printStackTrace()}"
            printerStatus.postValue(PrinterUIState.Printed(error))
        } catch (ce: ConnectException) {
            val error = "An exception occurred:\n ${ce.printStackTrace()}"
            printerStatus.postValue(PrinterUIState.Printed(error))

        } catch (se: SocketException) {
            val error = "An exception occurred:\n ${se.printStackTrace()}"
            printerStatus.postValue(PrinterUIState.Printed(error))
        }
    }
}