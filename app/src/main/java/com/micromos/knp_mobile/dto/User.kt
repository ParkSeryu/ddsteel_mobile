package com.micromos.knp_mobile.dto

import com.google.gson.annotations.SerializedName


data class User(
        @SerializedName("USER_ID") val id: String,
        @SerializedName("PASSWORD") val password: String,
        @SerializedName("USER_NAME") val name: String,
        @SerializedName("RETIRE_FLAG") val retire: String,
        @SerializedName("PROGRAM_ID") val program: String,
        var programs: List<String>){

    fun convertProgramList(){
        if(program.contains(",")){
            programs = program.split(",")
        }
    }
}