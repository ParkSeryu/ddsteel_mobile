package com.micromos.knpmobile.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.micromos.knpmobile.dto.GetPosCd
import com.micromos.knpmobile.network.ApiResult
import com.micromos.knpmobile.network.KNPApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface ChangePosRepository {
    fun sendRequestPosCd(labelNo : String, resultCallback: ApiResult)
    fun getPosCd() : LiveData<GetPosCd>
    fun changePosCd(codeCd : String, user_id : String, coilNo : String, coilSeq : String, stkType : String, resultCallback: ApiResult)
}

class ChangePosRepositoryImpl : ChangePosRepository{
    private val api = KNPApi.create()
    private val posCdData = MutableLiveData<GetPosCd>()
    override fun sendRequestPosCd(labelNo: String, resultCallback: ApiResult) {
        api.getPosCd(labelNo).enqueue(object : Callback<GetPosCd> {
            override fun onResponse(call: Call<GetPosCd>, response: Response<GetPosCd>) {
                Log.d("testPosRetrieve", "${response.body()}")
                if(response.code() == 200){
                    posCdData.value = response.body()
                    resultCallback.onResult()
                }
                else if (response.code() == 204){
                    resultCallback.nullBody()
                }
            }

            override fun onFailure(call: Call<GetPosCd>, t: Throwable) {
                Log.d("testFailedPosRetrieve", t.message.toString())
                resultCallback.onFailure()
            }
        })
    }

    override fun getPosCd() : LiveData<GetPosCd> {
        return posCdData
    }

    override fun changePosCd(
        codeCd: String,
        user_id: String,
        coilNo: String,
        coilSeq: String,
        stkType: String,
        resultCallback: ApiResult
    ) {
        api.changePosCd(codeCd, user_id, coilNo, coilSeq, stkType).enqueue(object : Callback<Unit>{
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