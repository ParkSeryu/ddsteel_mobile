package com.micromos.knp_mobile.dto

import com.google.gson.annotations.SerializedName
import java.text.DecimalFormat

data class GetCustCd(@SerializedName("CUST_NM") val cust_nm: String)

data class CoilInFeed(@SerializedName("data") val items: List<CoilIn>)

data class CoilIn(
    @SerializedName("SHIP_NO") val shipNo: String,
    @SerializedName("SHIP_SEQ") val shipSeq: String,
    @SerializedName("COIL_NO") val coilNo: String,
    @SerializedName("COIL_SEQ") val coilSeq: String,
    @SerializedName("PACK_NO") val packNo: String,
    @SerializedName("LABEL_NO") val labelNo: String,
    @SerializedName("NAME_CD") val namecd: String,
    @SerializedName("NAME_NM") val namenm: String,
    @SerializedName("STAN_CD") val stancd: String,
    @SerializedName("STAN_NM") val stannm: String,
    @SerializedName("SIZE_NO") val sizeNo: String,
    @SerializedName("QUANTITY") val quantity: String,
    @SerializedName("WEIGHT") val weight: Int,
    @SerializedName("MODIFY_CLS") val modifyCLS: String,
)


