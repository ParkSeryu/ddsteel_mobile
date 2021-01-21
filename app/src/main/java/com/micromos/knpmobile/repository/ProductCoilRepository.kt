package com.micromos.knpmobile.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.micromos.knpmobile.dto.GetCustCd
import com.micromos.knpmobile.dto.ShipOrder
import com.micromos.knpmobile.network.ApiResult

interface ProductCoilRepository {
    fun sendRequestCustCd(requestNo: String, resultCallback: ApiResult)
    fun getCustCD(): LiveData<GetCustCd>

    fun sendRequestShipOrder(requestNo: String, resultCallback: ApiResult)
    fun getShipOrder(): List<ShipOrder>?
    fun getItemSize(): Int

    fun updateTimePDA(type : String , labelNo : String, resultCallback:ApiResult)
}