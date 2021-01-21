package com.micromos.knpmobile.dto

import com.google.gson.annotations.SerializedName

data class GetCodeCdFeed(@SerializedName("data") val items: List<GetCodeCd>)

data class GetCodeCd(
    @SerializedName("CODE_CD") val codeCd: String,
    @SerializedName("CODE_NM") val codeNm: String,

    )

data class GetPosCd(
    @SerializedName("COIL_NO") val coilNo: String,
    @SerializedName("COIL_SEQ") val coilSeq: String,
    @SerializedName("STK_TYPE") val stkType: String,
    @SerializedName("POS_CD") val posCd: String,
)

