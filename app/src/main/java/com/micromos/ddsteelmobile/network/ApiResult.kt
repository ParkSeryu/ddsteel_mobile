package com.micromos.ddsteelmobile.network


interface ApiResult {
        fun onResult()
        fun nullBody()
        fun onFailure()
}
