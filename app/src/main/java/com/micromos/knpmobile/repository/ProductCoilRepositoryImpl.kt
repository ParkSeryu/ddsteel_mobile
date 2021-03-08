package com.micromos.knpmobile.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.micromos.knpmobile.dto.GetCustCd
import com.micromos.knpmobile.dto.ShipOrder
import com.micromos.knpmobile.dto.ShipOrderFeed
import com.micromos.knpmobile.network.ApiResult
import com.micromos.knpmobile.network.KNPApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface ProductCoilRepository {
    fun sendRequestCustCd(requestNo: String, resultCallback: ApiResult)
    fun getCustCD(): LiveData<GetCustCd>

    fun sendRequestShipOrder(requestNo: String, programFlag : Int, resultCallback: ApiResult)
    fun getShipOrder(): List<ShipOrder>?
    fun getItemSize(): Int

    fun updateTimePDA(type : String , labelNo : String, shipNo : String, resultCallback:ApiResult)
}

class ProductCoilRepositoryImpl : ProductCoilRepository {
    private val api = KNPApi.create()
    private val custCdData = MutableLiveData<GetCustCd>()
    private val shipOrderListData = MutableLiveData<List<ShipOrder>>()
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

    override fun sendRequestShipOrder(requestNo: String, programFlag: Int, resultCallback: ApiResult) {
        api.getShipOrder(requestNo, programFlag).enqueue(object : Callback<ShipOrderFeed> {
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

    override fun updateTimePDA(type: String, labelNo: String, shipNo : String, resultCallback: ApiResult) {
        if (type == "IN") {
            api.updatePDAin(labelNo, shipNo).enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    Log.d("testUpdateTimeIn", response.body().toString())
                    if (response.code() == 200) resultCallback.onResult()
                    else resultCallback.nullBody()
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    Log.d("testFailedUpdateTimeIn", t.message.toString())
                    resultCallback.onFailure()
                }
            })
        } else if (type == "OUT") {
            api.updatePDAout(labelNo, shipNo).enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    Log.d("testUpdateTimeOut", response.body().toString())
                    if (response.code() == 200) resultCallback.onResult()
                    else resultCallback.nullBody()
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    Log.d("testFailedUpdateTimeOut", t.message.toString())
                    resultCallback.onFailure()
                }
            })
        }
    }
}