package com.micromos.ddsteelmobile.network


interface StockApiResult {
        fun onResult(packCls : Int)
        fun nullBody()
        fun onFailure()
}
