package com.micromos.knpmobile.dto

import com.google.gson.annotations.SerializedName

data class GetLabelNo(
    @SerializedName("LABEL_NO") val labelNo: String
)

data class GetCardInfo(
    @SerializedName("LABEL_NO") val labelNo: String,
    @SerializedName("COIL_NO") val coilNo: String,
    @SerializedName("COIL_SEQ") val coilSeq: String,
    @SerializedName("NAME_CD") val nameCd: String,
    @SerializedName("NAME_NM") val nameNm: String,
    @SerializedName("STAN_CD") val stanCd: String,
    @SerializedName("STAN_NM") val stanNm: String,
    @SerializedName("SIZE_NO") val sizeNo: String,
    @SerializedName("QUANTITY") val quantity: Int,
    @SerializedName("WEIGHT") val weight: Int,
    @SerializedName("TIME") val time: String,
)