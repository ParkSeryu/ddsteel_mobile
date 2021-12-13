package com.micromos.ddsteelmobile.network


interface StockApiResult {
        fun onResult(checkYardCust : Boolean, packCls : Int)
        fun nullBody()
        fun onFailure()
}
