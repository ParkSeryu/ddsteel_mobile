package com.micromos.knpmobile.dto

import com.google.gson.annotations.SerializedName

data class GetCodeCdFeed(@SerializedName("data") val items: List<GetCodeCd>)

data class GetCodeCd(
    @SerializedName("CODE_CD") val codeCd: String,
    @SerializedName("CODE_NM") val codeNm: String,

    )

data class GetKnpLabelInfo(
    @SerializedName("LABEL_NO") val labelNo : String,
    @SerializedName("MILL_NO") val millNo : String,
    @SerializedName("SIZE_NO") val sizeNo : String,
    @SerializedName("WEIGHT") val weight : Int,
)

data class GetPosCd(
    @SerializedName("COIL_NO") val coilNo: String,
    @SerializedName("COIL_SEQ") val coilSeq: String,
    @SerializedName("STK_TYPE") val stkType: String,
    @SerializedName("POS_CD") val posCd: String,
)

data class HyundaiLabelInfoFeed(@SerializedName("response") val items: List<HyundaiLabelInfo>)

data class HyundaiLabelInfo(
    @SerializedName("CUST_NAME") val custName: String,
    @SerializedName("PROD_NAME") val prodName: String,
    @SerializedName("GOODS_NO") val goodsNo: String,
    @SerializedName("SPEC") val spec: String,
    @SerializedName("YP") val yp: String,
    @SerializedName("TS") val ts: String,
    @SerializedName("EL") val el: String,
    @SerializedName("HRB") val hrb: String,
    @SerializedName("C") val c: String,
    @SerializedName("MN") val mn: String,
    @SerializedName("P") val p: String,
    @SerializedName("GW_PRN") val gwPrn: String,
    @SerializedName("GW_BAK") val gwBak: String,
    @SerializedName("WGT") val wgt: Int,
)