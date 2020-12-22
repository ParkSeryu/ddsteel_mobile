package com.micromos.knp_mobile.ui.productcoilin

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.micromos.knp_mobile.Event
import com.micromos.knp_mobile.ViewModelBase
import com.micromos.knp_mobile.dto.CoilIn
import com.micromos.knp_mobile.dto.CoilInFeed
import com.micromos.knp_mobile.dto.GetCustCd
import com.micromos.knp_mobile.network.KNPApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductCoilInViewModel : ViewModelBase() {

    private val api = KNPApi.create()

    val _sell_cust_cd = MutableLiveData<String>()
    val sell_cust_cd_visibility = MutableLiveData<Int>()

    private val _noRetrieve = MutableLiveData<Event<Unit>>()
    val noRetrieve: LiveData<Event<Unit>> = _noRetrieve

    private val _noNetworkConnect = MutableLiveData<Event<Unit>>()
    val noNetWorkConnect: LiveData<Event<Unit>> = _noNetworkConnect

    val _requestNo = MutableLiveData<String>()
    val coilInData = MutableLiveData<List<CoilIn>>()


    init {
        sell_cust_cd_visibility.value = View.INVISIBLE
    }


    fun retrieve(){
        _isLoading.value = true
        val requestNo = _requestNo.value?.trim()?: ""
        if(requestNo.length == 11){
            api.getCustCd(requestNo).enqueue(object : Callback<GetCustCd>{
                override fun onResponse(call: Call<GetCustCd>, response: Response<GetCustCd>) {
                    Log.d("testCustCd", response.body().toString())
                    _sell_cust_cd.value = response.body()?.cust_nm.toString()
                    if(response.code() == 200){
                        sell_cust_cd_visibility.value = View.VISIBLE
                    }
                    else{
                        _noRetrieve.value = Event(Unit)
                        sell_cust_cd_visibility.value = View.INVISIBLE
                    }
                }

                override fun onFailure(call: Call<GetCustCd>, t: Throwable) {
                    Log.d("testfailedCust", t.message.toString())
                    noNetWork()
                }
            })

            api.coilIn(requestNo).enqueue(object : Callback<CoilInFeed>{
                override fun onResponse(call: Call<CoilInFeed>, response: Response<CoilInFeed>) {
                    Log.d("testCoilIn", response.body().toString())
                    coilInData.value = response.body()?.items
                    successCall()
                }

                override fun onFailure(call: Call<CoilInFeed>, t: Throwable) {
                    Log.d("testfailedCoilIn", t.message.toString())
                    noNetWork()
                }
            })
        }

        else {
            api.updatePDA(requestNo).enqueue(object : Callback<Unit>{
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    Log.d("testUpdatePda", response.body().toString())
                    successCall()
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    Log.d("testfailedUpdatePDA", t.message.toString())
                    noNetWork()
                }
            })
        }



    }

    fun successCall(){
        _isLoading.value = false
    }

    fun noNetWork(){
        _noNetworkConnect.value = Event(Unit)
        _isLoading.value = false
    }


}