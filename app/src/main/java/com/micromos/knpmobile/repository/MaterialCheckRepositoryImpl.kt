package com.micromos.knpmobile.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.micromos.knpmobile.dto.GetKnpLabelInfo
import com.micromos.knpmobile.dto.HyundaiLabelInfo
import com.micromos.knpmobile.dto.HyundaiLabelInfoFeed
import com.micromos.knpmobile.network.ApiResult
import com.micromos.knpmobile.network.HyundaiSteelApi
import com.micromos.knpmobile.network.KNPApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface MaterialCheckRepository {

    fun requestHyundaiLabelInfo(labelNo: String, resultCallback: ApiResult)
    fun requestKnpLabelInfo(labelNo: String, resultCallback: ApiResult)
    fun getHyundaiLabelInfo(): LiveData<List<HyundaiLabelInfo>>
    fun getKnpLabelInfo(): LiveData<GetKnpLabelInfo>
}

class MaterialCheckRepositoryImpl : MaterialCheckRepository {

    private val hyundaiApi = HyundaiSteelApi.create()
    private val knpApi = KNPApi.create()
    private val labelInfo = MutableLiveData<GetKnpLabelInfo>()
    private val hyundaiLabelInfo = MutableLiveData<List<HyundaiLabelInfo>>()

    override fun requestHyundaiLabelInfo(labelNo: String, resultCallback: ApiResult) {
        hyundaiApi.getLabelInfo(labelNo).enqueue(object : Callback<HyundaiLabelInfoFeed> {
            override fun onResponse(
                call: Call<HyundaiLabelInfoFeed>,
                response: Response<HyundaiLabelInfoFeed>
            ) {
                if (response.code() == 200) {
                    hyundaiLabelInfo.value = response.body()?.items
                    resultCallback.onResult()
                } else if (response.code() == 204) {
                    resultCallback.nullBody()
                }
            }

            override fun onFailure(call: Call<HyundaiLabelInfoFeed>, t: Throwable) {
                Log.d("testFailedMaterialRetrieve", t.message.toString())
                resultCallback.onFailure()
            }
        })
    }

    override fun getHyundaiLabelInfo(): LiveData<List<HyundaiLabelInfo>> {
        return hyundaiLabelInfo
    }

    override fun requestKnpLabelInfo(labelNo: String, resultCallback: ApiResult) {
        knpApi.getKnpLabelInfo(labelNo).enqueue(object : Callback<GetKnpLabelInfo> {
            override fun onResponse(
                call: Call<GetKnpLabelInfo>,
                response: Response<GetKnpLabelInfo>
            ) {
                if (response.code() == 200) {
                    labelInfo.value = response.body()
                    resultCallback.onResult()
                } else if (response.code() == 204) {
                    resultCallback.nullBody()
                }
            }

            override fun onFailure(call: Call<GetKnpLabelInfo>, t: Throwable) {
                Log.d("testFailedMaterialRetrieve", t.message.toString())
                resultCallback.onFailure()
            }
        })
    }

    override fun getKnpLabelInfo(): LiveData<GetKnpLabelInfo> {
        return labelInfo
    }


}