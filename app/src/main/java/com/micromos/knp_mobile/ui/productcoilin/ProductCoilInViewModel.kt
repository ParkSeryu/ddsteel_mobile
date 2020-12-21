package com.micromos.knp_mobile.ui.productcoilin

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.micromos.knp_mobile.dto.CoilIn
import com.micromos.knp_mobile.dto.CoilInFeed
import com.micromos.knp_mobile.dto.GetCustCd
import com.micromos.knp_mobile.network.KNPApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductCoilInViewModel : ViewModel() {

    private val api = KNPApi.create()

    val _sell_cust_cd = MutableLiveData<String>()

    val _shipNo = MutableLiveData<String>()
    val coilInData = MutableLiveData<List<CoilIn>>()




    fun retrieve(){
        val shipNo = _shipNo.value?.trim()?: ""
        api.getCustCd(shipNo).enqueue(object : Callback<GetCustCd>{
            override fun onResponse(call: Call<GetCustCd>, response: Response<GetCustCd>) {
                Log.d("test", response.body().toString())
                _sell_cust_cd.value = response.body()?.cust_nm.toString()0
            }

            override fun onFailure(call: Call<GetCustCd>, t: Throwable) {
            }
        })

        api.coilIn(shipNo).enqueue(object : Callback<CoilInFeed>{
            override fun onResponse(call: Call<CoilInFeed>, response: Response<CoilInFeed>) {
                Log.d("test", response.body().toString())
                coilInData.value = response.body()?.items
            }

            override fun onFailure(call: Call<CoilInFeed>, t: Throwable) {
                Log.d("testfailed", t.message.toString())
            }
        })
    }
}