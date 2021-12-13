package com.micromos.ddsteelmobile.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.micromos.ddsteelmobile.dto.*
import com.micromos.ddsteelmobile.network.ApiResult
import com.micromos.ddsteelmobile.network.DDsteelApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface ProductCoilRepository {
    fun sendRequestCustCd(requestNo: String, resultCallback: ApiResult)
    fun getCustCD(): LiveData<GetCustCd>

    fun sendRequestShipOrder(requestNo: String, resultCallback: ApiResult)
    fun getShipOrder(): List<ShipOrder>?
    fun getItemSize(): Int
    fun updateScanDate(userId: String, labelNo: String, shipNo: String, resultCallback: ApiResult)
    fun getUpdateScanResult(): LiveData<GetUpdateScanCardInfo>
    fun forcedUpdate(userId : String, shipNo: String, shipSeq: String, resultCallback: ApiResult)
    fun rollbackUpdate( shipNo: String, shipSeq: String, resultCallback: ApiResult)
}

class ProductCoilRepositoryImpl : ProductCoilRepository {
    private val api = DDsteelApi.create()
    private val custCdData = MutableLiveData<GetCustCd>()
    private val shipOrderListData = MutableLiveData<List<ShipOrder>>()
    private val updateInfoResult = MutableLiveData<GetUpdateScanCardInfo>()
    private var length: Int? = null

    override fun sendRequestCustCd(requestNo: String, resultCallback: ApiResult) {
        api.getCustCd(requestNo).enqueue(object : Callback<GetCustCd> {
            override fun onResponse(call: Call<GetCustCd>, response: Response<GetCustCd>) {
                Log.d("testRequestCustCd", response.body().toString())
                if (response.code() == 200) {
                    custCdData.value = response.body()
                    resultCallback.onResult()
                } else resultCallback.nullBody()
            }

            override fun onFailure(call: Call<GetCustCd>, t: Throwable) {
                Log.d("testFailRequestCustCd", t.message.toString())
                resultCallback.onFailure()
            }
        })
    }

    override fun getCustCD(): LiveData<GetCustCd> {
        return custCdData
    }

    override fun sendRequestShipOrder(requestNo: String, resultCallback: ApiResult) {
        api.getShipOrder(requestNo).enqueue(object : Callback<ShipOrderFeed> {
            override fun onResponse(call: Call<ShipOrderFeed>, response: Response<ShipOrderFeed>) {
                Log.d("testCoil", response.body().toString())
                shipOrderListData.value = response.body()?.items

                Log.d("testCoilLength", "${response.body()?.items?.size}")
                length = response.body()?.items?.size
                resultCallback.onResult()
            }

            override fun onFailure(call: Call<ShipOrderFeed>, t: Throwable) {
                Log.d("testFailedCoil", t.message.toString())
                resultCallback.onFailure()
            }
        })
    }

    override fun getShipOrder(): List<ShipOrder>? {
        return shipOrderListData.value
    }

    override fun getItemSize(): Int {
        return length ?: 0
    }

    override fun updateScanDate(
        userId: String,
        labelNo: String,
        shipNo: String,
        resultCallback: ApiResult
    ) {
        api.updateScanDate(userId, labelNo, shipNo).enqueue(object : Callback<GetUpdateScanCardInfo> {
            override fun onResponse(
                call: Call<GetUpdateScanCardInfo>,
                response: Response<GetUpdateScanCardInfo>
            ) {
                Log.d("testUpdateTimeIn", response.body().toString())
                updateInfoResult.value = response.body()
                if (response.code() == 200) resultCallback.onResult()
                else resultCallback.nullBody()
            }

            override fun onFailure(call: Call<GetUpdateScanCardInfo>, t: Throwable) {
                Log.d("testFailedUpdateTimeIn", t.message.toString())
                resultCallback.onFailure()
            }
        })
    }

    override fun forcedUpdate(userId: String, shipNo: String, shipSeq: String, resultCallback: ApiResult) {
        api.forcedUpdate(userId, shipNo, shipSeq).enqueue(object : Callback<GetUpdateScanCardInfo> {
            override fun onResponse(call: Call<GetUpdateScanCardInfo>, response: Response<GetUpdateScanCardInfo>) {
                Log.d("testUpdateForced", response.body().toString())
                updateInfoResult.value = response.body()
                if (response.code() == 200) resultCallback.onResult()
                else resultCallback.nullBody()
            }

            override fun onFailure(call: Call<GetUpdateScanCardInfo>, t: Throwable) {
                Log.d("testFailedUpdateForced", t.message.toString())
                resultCallback.onFailure()
            }
        })
    }

    override fun rollbackUpdate(
        shipNo: String,
        shipSeq: String,
        resultCallback: ApiResult
    ) {
        api.rollBackUpdate(shipNo, shipSeq).enqueue(object : Callback<GetUpdateScanCardInfo>{
            override fun onResponse(call: Call<GetUpdateScanCardInfo>, response: Response<GetUpdateScanCardInfo>) {
                Log.d("testUpdateRollback", response.body().toString())
                updateInfoResult.value = response.body()
                if (response.code() == 200) resultCallback.onResult()
                else resultCallback.nullBody()
            }

            override fun onFailure(call: Call<GetUpdateScanCardInfo>, t: Throwable) {
                Log.d("testFailedUpdateRollback", t.message.toString())
                resultCallback.onFailure()
            }
        })
    }

    override fun getUpdateScanResult(): LiveData<GetUpdateScanCardInfo> {
        return updateInfoResult
    }
}