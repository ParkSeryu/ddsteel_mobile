package com.micromos.ddsteelmobile.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.micromos.ddsteelmobile.dto.GetCardInfo
import com.micromos.ddsteelmobile.dto.GetLabelNo
import com.micromos.ddsteelmobile.network.ApiResult
import com.micromos.ddsteelmobile.network.DDsteelApi
import com.micromos.ddsteelmobile.network.StockApiResult
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface StockCheckRepository {
    fun sendRequestServerTime(resultCallback: ApiResult)
    fun getServerTime(): String

    fun sendRequestLabelNo(
        stockDate: String,
        labelNo: String,
        ResultCallback: StockApiResult
    )

    fun updateCoilStock(
        codeCd: String,
        labelNo: String,
        stockDate: String,
        packCls: Int,
        resultCallback: ApiResult
    )

    fun getCardInfo(): LiveData<GetCardInfo>

    fun insertCoilStock(
        stockDate: String,
        user_id: String?,
        labelNo: String,
        codeCd: String,
        resultCallback: ApiResult
    )
}

class StockCheckRepositoryImpl : StockCheckRepository {
    private val api = DDsteelApi.create()
    private lateinit var time: String
    private val cardData = MutableLiveData<GetCardInfo>()

    override fun sendRequestServerTime(resultCallback: ApiResult) {
        api.getDateTime().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200) {
                    time = response.body()!!.string().trim().substring(0, 8)
                    resultCallback.onResult()
                } else resultCallback.nullBody()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("testFailedGetDate", t.message.toString())
                resultCallback.onFailure()
            }
        })
    }

    override fun getServerTime(): String {
        return time
    }

    override fun sendRequestLabelNo(
        stockDate: String,
        labelNo: String,
        ResultCallback: StockApiResult
    ) {
        api.getLabelNo(stockDate, labelNo).enqueue(object : Callback<GetLabelNo> {
            override fun onResponse(call: Call<GetLabelNo>, response: Response<GetLabelNo>) {
                if (response.code() == 200) {
                    ResultCallback.onResult(response.body()!!.packCls)
                } else if (response.code() == 204) {
                    ResultCallback.nullBody()
                }
            }

            override fun onFailure(call: Call<GetLabelNo>, t: Throwable) {
                Log.d("testFailedGetLabelNo", t.message.toString())
                ResultCallback.onFailure()
            }
        })
    }

    override fun updateCoilStock(
        codeCd: String,
        labelNo: String,
        stockDate: String,
        packCls: Int,
        resultCallback: ApiResult
    ) {
        api.updateCoilStock(codeCd, labelNo, stockDate, packCls)
            .enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (response.code() == 200) {
                        api.getCardInfo(labelNo, stockDate).enqueue(object : Callback<GetCardInfo> {
                            override fun onResponse(
                                call: Call<GetCardInfo>,
                                response: Response<GetCardInfo>
                            ) {
                                if (response.code() == 200) {
                                    Log.d("testUpdateCardInfo", "${response.body()}")
                                    cardData.value = response.body()
                                    resultCallback.onResult()
                                } else resultCallback.nullBody()
                            }

                            override fun onFailure(call: Call<GetCardInfo>, t: Throwable) {
                                Log.d("testFailedUpdateCardInfo", t.message.toString())
                                resultCallback.onFailure()
                            }
                        })
                    } else resultCallback.nullBody()
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    Log.d("testFailedUpdateCardInfo", t.message.toString())
                    resultCallback.onFailure()
                }
            })
    }

    override fun insertCoilStock(
        stockDate: String,
        user_id: String?,
        labelNo: String,
        codeCd: String,
        resultCallback: ApiResult
    ) {
        api.insertCoilStock(
            stockDate,
            stockDate,
            user_id ?: "empty",
            labelNo,
            codeCd
        ).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                Log.d("testInsert", "$stockDate,$labelNo, $codeCd")
                if (response.code() == 200) {
                    api.getCardInfo(labelNo, stockDate).enqueue(object : Callback<GetCardInfo> {
                        override fun onResponse(
                            call: Call<GetCardInfo>,
                            response: Response<GetCardInfo>
                        ) {
                            Log.d("testInsertCardInfo", "${response.body()}")
                            cardData.value = response.body()
                            resultCallback.onResult()
                        }

                        override fun onFailure(call: Call<GetCardInfo>, t: Throwable) {
                            Log.d("testFailedInsertCardInfo", t.message.toString())
                            resultCallback.onFailure()
                        }
                    })
                } else resultCallback.nullBody()
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Log.d("testFailedInsert", t.message.toString())
                resultCallback.onFailure()
            }
        })
    }

    override fun getCardInfo(): LiveData<GetCardInfo> {
        return cardData
    }
}