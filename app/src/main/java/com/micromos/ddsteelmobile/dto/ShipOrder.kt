package com.micromos.ddsteelmobile.dto

import com.google.gson.annotations.SerializedName

data class GetCustCd(
    @SerializedName("SELL_CUST_NM") val custNm: String,
    @SerializedName("SHIP_TYPE") val shipType: String,
    @SerializedName("DLV_CUST_NM") val dlvCustNm: String,
    @SerializedName("SALES_MAN_NM") val salesManNm: String,
    @SerializedName("REGION_NM") val regionNm: String,
)

data class ShipOrderFeed(@SerializedName("DATA") val items: List<ShipOrder>)

data class ShipOrder(
    @SerializedName("SHIP_NO") val shipNo: String,
    @SerializedName("SHIP_SEQ") val shipSeq: String,
    @SerializedName("COIL_NO") val coilNo: String,
    @SerializedName("COIL_SEQ") val coilSeq: String,
    @SerializedName("NAME_CD") val nameCd: String,
    @SerializedName("NAME_NM") val nameNm: String,
    @SerializedName("TYPE_CD") val typeCd: String,
    @SerializedName("TYPE_NM") val typeNm: String,
    @SerializedName("STAN_CD") val stanCd: String,
    @SerializedName("STAN_NM") val stanNm: String,
    @SerializedName("MILL_MAKER_CD") val millMakerCd: String,
    @SerializedName("MILL_MAKER_NM") val millMakerNm: String,
    @SerializedName("SIZE_NO") val sizeNo: String,
    @SerializedName("QUANTITY") val quantity: Int,
    @SerializedName("WEIGHT") val weight: Double,
    @SerializedName("SCAN_DATE") var scanDate: String?,
    @SerializedName("TIME") var scanTime: String?,
    @SerializedName("SCAN_CLS") var scanCls: String?,
)

data class GetUpdateScanCardInfo(
    @SerializedName("RETURN_RESULT") val returnResult: String,
    @SerializedName("SHIP_NO") val shipNo: String,
    @SerializedName("SHIP_SEQ") val shipSeq: String,
    @SerializedName("SCAN_DATE") val scanDate: String,
    @SerializedName("SCAN_CLS") val scanCls: String,
    @SerializedName("TIME") val scanTime: String,
)




