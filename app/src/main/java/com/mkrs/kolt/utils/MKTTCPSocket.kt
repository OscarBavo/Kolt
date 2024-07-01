package com.mkrs.kolt.utils

import androidx.lifecycle.MutableLiveData
import com.mkrs.kolt.dashboard.home.printer.PrinterUIState
import java.io.IOException
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

    companion object {
        private const val PRINTING_OK = "OK"
    }

    fun sendDataPrinter(
        data: String,
        ip: String,
        port: Int,
        printerStatus: MutableLiveData<PrinterUIState>
    ) {

        try {
            val socket = Socket(ip, port)
            socket.use {
                //printerStatus.postValue(PrinterUIState.Loading)
                it.getOutputStream().write(data.toByteArray())
                it.close()
                printerStatus.postValue(PrinterUIState.Printed(PRINTING_OK))
            }
        } catch (he: UnknownHostException) {
            val error = "An exception occurred:\n ${he.printStackTrace()}"
            printerStatus.postValue(PrinterUIState.Error(error))

        } catch (ioe: IOException) {
            val error = "An exception occurred:\n ${ioe.printStackTrace()}"
            printerStatus.postValue(PrinterUIState.Error(error))
        } catch (ce: ConnectException) {
            val error = "An exception occurred:\n ${ce.printStackTrace()}"
            printerStatus.postValue(PrinterUIState.Error(error))

        } catch (se: SocketException) {
            val error = "An exception occurred:\n ${se.printStackTrace()}"
            printerStatus.postValue(PrinterUIState.Error(error))
        }
    }
}