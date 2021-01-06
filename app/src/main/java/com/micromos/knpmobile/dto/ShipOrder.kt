package com.micromos.knpmobile.dto

import com.google.gson.annotations.SerializedName

data class GetCustCd(
    @SerializedName("CUST_NM") val custNm: String,
    @SerializedName("VEN_CUST_NM") val venCustNm: String,
    @SerializedName("DLV_CUST_NM") val dlvCustNm: String,
)

data class ShipOrderFeed(@SerializedName("data") val items: List<ShipOrder>)

data class ShipOrder(
    @SerializedName("SHIP_NO") val shipNo: String,
    @SerializedName("SHIP_SEQ") val shipSeq: String,
    @SerializedName("COIL_NO") val coilNo: String,
    @SerializedName("COIL_SEQ") val coilSeq: String,
    @SerializedName("PACK_NO") val packNo: String,
    @SerializedName("LABEL_NO") val labelNo: String,
    @SerializedName("PDA_DATETIME1") val pdaDateTimeIn: String,
    @SerializedName("PDA_DATETIME2") val pdaDateTimeOut: String,
    @SerializedName("NAME_CD") val nameCd: String,
    @SerializedName("NAME_NM") val nameNm: String,
    @SerializedName("STAN_CD") val stanCd: String,
    @SerializedName("STAN_NM") val stanNm: String,
    @SerializedName("SIZE_NO") val sizeNo: String,
    @SerializedName("QUANTITY") val quantity: Int,
    @SerializedName("WEIGHT") val weight: Int,
    @SerializedName("MODIFY_CLS") val modifyCLS: String,
)