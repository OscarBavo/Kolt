package com.mkrs.kolt.base.webservices.entity

import com.google.gson.annotations.SerializedName

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.base.webservices.entity
 * Date: 20 / 06 / 2024
 *****/
class TransferRequest(
    @SerializedName("Referencia")
    val reference: String = "",
    @SerializedName("Lineas")
    val lines: MutableList<LineasED>
)

/*

{
  "Referencia": "string",
  "Lineas": [
    {
      "WhsCode": "string",
      "ItemCodeMP": "string",
      "ItemCodePT": "string",
      "BatchNuber": "string",
      "ManufacturerSerialNumber": "string",
      "Quantity": 0
    }
  ]
}
 */
class LineasED(
    @SerializedName("WhsCode")
    val whsCode: String = "",
    @SerializedName("ItemCodeMP")
    val itemCodeMP: String = "",
    @SerializedName("ItemCodePT")
    val itemCodePT: String = "",
    @SerializedName("BatchNumber")
    val batchNumber: String = "",
    @SerializedName("ManufacturerSerialNumber")
    val manSerialNum: String = "",
    @SerializedName("Quantity")
    val quantity: Double = 0.0
)