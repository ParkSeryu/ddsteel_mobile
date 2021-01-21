package com.micromos.knpmobile.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.micromos.knpmobile.dto.GetCardInfo
import com.micromos.knpmobile.dto.GetLabelNo
import com.micromos.knpmobile.network.ApiResult
import com.micromos.knpmobile.network.KNPApi
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StockCheckRepositoryImpl : StockCheckRepository {
    private val api = KNPApi.create()
    private lateinit var time: String
    private val cardData = MutableLiveData<GetCardInfo>()

    override fun sendRequestServerTime(resultCallback: ApiResult) {
        api.getDateTime().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200) {
                    time = response.body()!!.string().trim().substring(0, 8)
                    resultCallback.onResult()
                }
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

    override fun sendRequestLabelNo(stockDate: String, labelNo: String, resultCallback: ApiResult) {
        api.getLabelNo(stockDate, labelNo).enqueue(object : Callback<GetLabelNo> {
            override fun onResponse(call: Call<GetLabelNo>, response: Response<GetLabelNo>) {
                Log.d("testLabelNo", response.body().toString())
                if (response.code() == 200)
                    resultCallback.onResult()
                else if (response.code() == 204)
                    resultCallback.nullBody()
            }

            override fun onFailure(call: Call<GetLabelNo>, t: Throwable) {
                Log.d("testFailedGetLabelNo", t.message.toString())
                resultCallback.onFailure()
            }
        })
    }

    override fun updateCoilStock(
        codeCd: String,
        labelNo: String,
        stockDate: String,
        resultCallback: ApiResult
    ) {
        api.updateCoilStock(codeCd, labelNo, stockDate).enqueue(object : Callback<Unit> {
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
            inDate = stockDate,
            stockNo = stockDate,
            userId = user_id ?: "empty",
            labelNo = labelNo,
            posCd = codeCd
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