package com.micromos.ddsteelmobile.dto

import com.google.gson.annotations.SerializedName

data class GetCommonPosCdFeed(@SerializedName("DATA") val items: List<GetCommonPosCd>)

data class GetCommonPosCd(
    @SerializedName("CODE_CD") val posCd: String,
    @SerializedName("CODE_NAME") val posNm: String,
)

data class GetLabelPosCd(
    @SerializedName("COIL_NO") val coilNo: String,
    @SerializedName("COIL_SEQ") val coilSeq: String,
    @SerializedName("STK_TYPE") val stkType: String,
    @SerializedName("POS_CD") val posCd: String,
)
