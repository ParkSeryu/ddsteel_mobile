package com.micromos.knpmobile.repository

import androidx.lifecycle.LiveData
import com.micromos.knpmobile.dto.GetCardInfo
import com.micromos.knpmobile.network.ApiResult

interface StockCheckRepository {
    fun sendRequestServerTime(resultCallback: ApiResult)
    fun getServerTime(): String

    fun sendRequestLabelNo(stockDate: String, labelNo: String, resultCallback: ApiResult)
    fun updateCoilStock(
        codeCd: String,
        labelNo: String,
        stockDate: String,
        resultCallback: ApiResult
    )
    fun getCardInfo() : LiveData<GetCardInfo>

    fun insertCoilStock(
        stockDate: String,
        user_id : String?,
        labelNo: String,
        codeCd: String,
        resultCallback: ApiResult
    )
}