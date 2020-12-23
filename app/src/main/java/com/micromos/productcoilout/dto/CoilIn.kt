package com.micromos.productcoilout.dto

import com.google.gson.annotations.SerializedName

data class GetCustCd(@SerializedName("CUST_NM") val cust_nm: String)

data class CoilInFeed(@SerializedName("data") val items: List<CoilIn>)

data class CoilIn(
    @SerializedName("SHIP_NO") val shipNo: String,
    @SerializedName("SHIP_SEQ") val shipSeq: String,
    @SerializedName("COIL_NO") val coilNo: String,
    @SerializedName("COIL_SEQ") val coilSeq: String,
    @SerializedName("PACK_NO") val packNo: String,
    @SerializedName("LABEL_NO") val labelNo: String,
    @SerializedName("PDA_DATETIME1") val pdaDateTime: String,
    @SerializedName("NAME_CD") val nameCd: String,
    @SerializedName("NAME_NM") val nameNm: String,
    @SerializedName("STAN_CD") val stanCd: String,
    @SerializedName("STAN_NM") val stanNm: String,
    @SerializedName("SIZE_NO") val sizeNo: String,
    @SerializedName("QUANTITY") val quantity: String,
    @SerializedName("WEIGHT") val weight: Int,
    @SerializedName("MODIFY_CLS") val modifyCLS: String,
)


