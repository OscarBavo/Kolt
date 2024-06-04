package com.mkrs.kolt.transfer.domain.entitie

import com.google.gson.annotations.SerializedName

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.transfer.domain.entitie
 * Date: 27 / 05 / 2024
 *****/
data class RDTransferResponse(
    @SerializedName("ItemCode")
    val itemCode: String = "",
    @SerializedName("MnfSerial")
    val mnfSerial: String = "",
    @SerializedName("Quantity")
    val quantity: String = "",
    @SerializedName("ItemName")
    val itemName: String = "",
    @SerializedName("SuppCatNum")
    val suppCatNum: String = "",
    @SerializedName("U_Pedido_Prog")
    val uPedidoProg: String = ""

)