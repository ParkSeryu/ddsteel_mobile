package com.micromos.ddsteelmobile.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.micromos.ddsteelmobile.dto.GetLabelUpdateInfo
import com.micromos.ddsteelmobile.dto.GetMaterialCardInfo
import com.micromos.ddsteelmobile.dto.GetSequence
import com.micromos.ddsteelmobile.network.ApiResult
import com.micromos.ddsteelmobile.network.DDsteelApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface MaterialInRepository {
    fun sendRequestKeySequenceNo(resultCallback: ApiResult)
    fun getKeySequenceNo(): LiveData<GetSequence>

    fun labelIn(
        loginId: String,
        workPlaceCd: String,
        transNo: String,
        transCarNo: String,
        transMan: String,
        transManPhone: String,
        labelNo: String,
        resultCallback: ApiResult
    )

    fun getLabelUpdateResult(): LiveData<GetLabelUpdateInfo>

    fun sendRequestMaterialCardInfo(millNo: String, inNo: String, resultCallback: ApiResult)
    fun getMaterialCardInfo(): GetMaterialCardInfo
}

class MaterialInRepositoryImpl : MaterialInRepository {
    private val api = DDsteelApi.create()
    private val sequenceData = MutableLiveData<GetSequence>()
    private val returnResult = MutableLiveData<GetLabelUpdateInfo>()
    private val cardInfoData = MutableLiveData<GetMaterialCardInfo>()

    override fun sendRequestKeySequenceNo(resultCallback: ApiResult) {
        api.getNewSequence().enqueue(object : Callback<GetSequence> {
            override fun onResponse(call: Call<GetSequence>, response: Response<GetSequence>) {
                if (response.code() == 200) {
                    sequenceData.value = response.body()
                    resultCallback.onResult()
                } else if (response.code() == 203) {
                    resultCallback.nullBody()
                }
            }

            override fun onFailure(call: Call<GetSequence>, t: Throwable) {
                Log.d("failedSendRequestKeySequence", t.message.toString())
                resultCallback.onFailure()
            }
        })
    }

    override fun getKeySequenceNo(): LiveData<GetSequence> {
        return sequenceData
    }


    override fun labelIn(
        loginId: String,
        workPlaceCd: String,
        transNo: String,
        transCarNo: String,
        transMan: String,
        transManPhone: String,
        labelNo: String,
        resultCallback: ApiResult
    ) {
        api.updateMaterialIn(
            loginId,
            workPlaceCd,
            transNo,
            transCarNo,
            transMan,
            transManPhone,
            labelNo
        ).enqueue(object : Callback<GetLabelUpdateInfo> {
            override fun onResponse(
                call: Call<GetLabelUpdateInfo>,
                response: Response<GetLabelUpdateInfo>
            ) {
                Log.d("testUpdateTimeIn", response.body().toString())
                if (response.code() == 200) {
                    returnResult.value = response.body()
                    resultCallback.onResult()
                } else resultCallback.nullBody()
            }

            override fun onFailure(call: Call<GetLabelUpdateInfo>, t: Throwable) {
                Log.d("testFailedUpdateTimeIn", t.message.toString())
                resultCallback.onFailure()
            }
        })
    }

    override fun getLabelUpdateResult(): LiveData<GetLabelUpdateInfo> {
        return returnResult
    }

    override fun sendRequestMaterialCardInfo(
        millNo: String,
        inNo: String,
        resultCallback: ApiResult
    ) {
      api.getMaterialCardInfo(millNo, inNo).enqueue(object : Callback<GetMaterialCardInfo>{
          override fun onResponse(
              call: Call<GetMaterialCardInfo>,
              response: Response<GetMaterialCardInfo>
          ) {
              if (response.code() == 200) {
                  cardInfoData.value = response.body()
                  resultCallback.onResult()
              } else if (response.code() == 203) {
                  resultCallback.nullBody()
              }
          }

          override fun onFailure(call: Call<GetMaterialCardInfo>, t: Throwable) {
              Log.d("failGetCardInfo", t.message.toString())
              resultCallback.onFailure()
          }
      })
    }

    override fun getMaterialCardInfo(): GetMaterialCardInfo {
        return cardInfoData.value!!
    }

}
