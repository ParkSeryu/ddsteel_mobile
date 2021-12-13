package com.micromos.ddsteelmobile.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.micromos.ddsteelmobile.dto.GetCommonPosCd
import com.micromos.ddsteelmobile.dto.GetLabelPosCd
import com.micromos.ddsteelmobile.network.ApiResult
import com.micromos.ddsteelmobile.network.DDsteelApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface ChangePosRepository {
    fun getLabelPosCd(labelNo: String, resultCallback: ApiResult)
    fun getPosCd(): LiveData<GetLabelPosCd>
    fun changePosCd(
        codeCd: String,
        user_id: String,
        coilNo: String,
        coilSeq: String,
        resultCallback: ApiResult
    )
}

class ChangePosRepositoryImpl : ChangePosRepository {
    private val api = DDsteelApi.create()
    private val labelInfo = MutableLiveData<GetLabelPosCd>()

    override fun getLabelPosCd(labelNo: String, resultCallback: ApiResult) {
        api.getLabelPosCd(labelNo).enqueue(object : Callback<GetLabelPosCd> {
            override fun onResponse(call: Call<GetLabelPosCd>, response: Response<GetLabelPosCd>) {
                Log.d("testLabelPosRetrieve", "${response.body()}")
                if (response.code() == 200) {
                    labelInfo.value = response.body()!!
                    resultCallback.onResult()
                } else if (response.code() == 203) {
                    resultCallback.nullBody()
                }
            }

            override fun onFailure(call: Call<GetLabelPosCd>, t: Throwable) {
                Log.d("testFailedPosRetrieve", t.message.toString())
                resultCallback.onFailure()
            }
        })
    }

    override fun getPosCd(): LiveData<GetLabelPosCd> {
        return labelInfo
    }

    override fun changePosCd(
        codeCd: String,
        user_id: String,
        coilNo: String,
        coilSeq: String,
        resultCallback: ApiResult
    ) {
        api.changePosCd(codeCd, user_id, coilNo, coilSeq).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                Log.d("testPosSave", response.body().toString())
                if (response.code() == 200) {
                    resultCallback.onResult()
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Log.d("testFailedPosSave", t.message.toString())
                resultCallback.onFailure()
            }
        })
    }
}