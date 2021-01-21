package com.micromos.knpmobile.network


interface ApiResult {
        fun onResult()
        fun nullBody()
        fun onFailure()
}
