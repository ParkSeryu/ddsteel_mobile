package com.micromos.ddsteelmobile.dto

import com.google.gson.annotations.SerializedName

data class GetSequence(
    @SerializedName("KEY_KIND") val keyKind: String,
    @SerializedName("KEY_DATA") val keyData: String,
    @SerializedName("SEQUENCE_NO") val sequenceNo: String,
)

data class GetLabelUpdateInfo(
    @SerializedName("RETURN_RESULT") val returnResult: String,
)

data class GetMaterialCardInfo(
    @SerializedName("NAME_NM") val nameNm: String,
    @SerializedName("TYPE_NM") val typeNm: String,
    @SerializedName("STAN_NM") val stanNm: String,
    @SerializedName("MILL_NO") val millNo: String,
    @SerializedName("WEIGHT") val weight: Double,
    @SerializedName("SIZE_NO") val sizeNo: String,
)
