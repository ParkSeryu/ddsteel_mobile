package com.micromos.ddsteelmobile.dto

import com.google.gson.annotations.SerializedName


data class User(
        @SerializedName("USER_ID") val id: String,
        @SerializedName("PASSWORD") val password: String,
        @SerializedName("USER_NAME") val name: String,
        @SerializedName("WORKPLACE_CD") val workPlaceCd: String,
        @SerializedName("WORKPLACE_NM") val workPlaceNm: String,
        @SerializedName("PROGRAM_ID") val program: String,
)
