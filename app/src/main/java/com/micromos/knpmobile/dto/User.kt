package com.micromos.knpmobile.dto

import com.google.gson.annotations.SerializedName


data class User(
        @SerializedName("USER_ID") val id: String,
        @SerializedName("PASSWORD") val password: String,
        @SerializedName("USER_NAME") val name: String,
        @SerializedName("RETIRE_FLAG") val retire: String,
        @SerializedName("PROGRAM_ID") val program: String,
)
