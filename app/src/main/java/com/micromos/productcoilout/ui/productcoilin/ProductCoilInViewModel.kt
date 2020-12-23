package com.micromos.productcoilout.ui.productcoilin

import android.graphics.Color
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.micromos.productcoilout.Event
import com.micromos.productcoilout.ViewModelBase
import com.micromos.productcoilout.dto.CoilIn
import com.micromos.productcoilout.dto.CoilInFeed
import com.micromos.productcoilout.dto.GetCustCd
import com.micromos.productcoilout.network.KNPApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductCoilInViewModel : ViewModelBase() {

    private val api = KNPApi.create()

    var shipNoList = setOf("QS", "RS", "FS", "SS", "BS", "MS")

    val _sell_cust_cd = MutableLiveData<String>()

    val sell_cust_cd_visibility = MutableLiveData<Int>()

    private val _noRetrieve = MutableLiveData<Event<Unit>>()
    val noRetrieve: LiveData<Event<Unit>> = _noRetrieve

    private val _noNetworkConnect = MutableLiveData<Event<Unit>>()
    val noNetWorkConnect: LiveData<Event<Unit>> = _noNetworkConnect

    private val _noLabelNo = MutableLiveData<Event<Unit>>()
    val noLabelNo: LiveData<Event<Unit>> = _noLabelNo

    private val _dateTimeOverlap = MutableLiveData<Event<Unit>>()
    val dateTimeOverlap: LiveData<Event<Unit>> = _dateTimeOverlap

    val _requestNo = MutableLiveData<String?>()

    val coilInData = MutableLiveData<List<CoilIn>>()

    val labelNoList = mutableListOf<String?>()
    val pdaDateTimeList = mutableListOf<String?>()

    val _recyclerViewState = MutableLiveData<Event<Unit>>()
    var recyclerViewStateFlag: Boolean = false

    var shipNo: String? = null

    var numerator : Int = 0
    var denomiator : Int = 0

    val _test = MutableLiveData<String>()
    val test: LiveData<String> = _test

    init {
        sell_cust_cd_visibility.value = View.INVISIBLE
    }


    fun shipRetrieve(requestNo: String?) {
        val requestNo = requestNo?.trim()
        //requestNo?.toUpperCase(Locale.ROOT)

        if (requestNo != null) {
            _isLoading.value = true
            if (requestNo.length == 11 && requestNo.substring(0,2) in shipNoList) {
                api.getCustCd(requestNo).enqueue(object : Callback<GetCustCd> {
                    override fun onResponse(call: Call<GetCustCd>, response: Response<GetCustCd>) {
                        Log.d("testCustCd", response.body().toString())
                        _sell_cust_cd.value = response.body()?.cust_nm.toString()
                        if (response.code() == 200) {
                            sell_cust_cd_visibility.value = View.VISIBLE
                        } else {
                            _noRetrieve.value = Event(Unit)
                            sell_cust_cd_visibility.value = View.INVISIBLE
                        }
                    }

                    override fun onFailure(call: Call<GetCustCd>, t: Throwable) {
                        Log.d("testfailedCust", t.message.toString())
                        noNetWork()
                    }
                })

                api.coilIn(requestNo).enqueue(object : Callback<CoilInFeed> {
                    override fun onResponse(
                        call: Call<CoilInFeed>,
                        response: Response<CoilInFeed>
                    ) {
                        Log.d("testCoilIn", response.body().toString())

                        coilInData.value = response.body()?.items

                        numerator = 0
                        denomiator = 0

                        Log.d("testCoilIn", "${response.body()?.items?.size}")
                        val length = response.body()?.items?.size

                        if (length != null) {
                            denomiator = length

                            labelNoList.clear()
                            pdaDateTimeList.clear()
                            for (i in 0 until length) {
                                labelNoList.add(coilInData.value?.get(i)?.labelNo)
                                pdaDateTimeList.add(coilInData.value?.get(i)?.pdaDateTime)
                                if(!pdaDateTimeList[i].isNullOrEmpty()){
                                    numerator++
                                }
                            }
                        }
                        shipNo = requestNo
                        successCall()
                    }

                    override fun onFailure(call: Call<CoilInFeed>, t: Throwable) {
                        Log.d("testfailedCoilIn", t.message.toString())
                        noNetWork()
                    }


                })
                if (_requestNo.value?.trim() == requestNo)
                    recyclerViewStateFlag = false
            } else {
                _recyclerViewState.value = Event(Unit)
                labelRetrieve(requestNo)
            }
        }
    }

    private fun labelRetrieve(labelNo: String) {
        var successFlag = "false"
        _isLoading.value = true
        for (i in 0 until labelNoList.size) {
            if (labelNo == labelNoList[i]) {
                if (pdaDateTimeList[i].isNullOrEmpty()) {
                    successFlag = "true"
                    Log.d("test", successFlag)
                    break
                } else {
                    successFlag = "overlap"
                    Log.d("test", successFlag)
                    successCall()
                }
            }
        }

        if (successFlag.equals("true")) {
            api.updatePDA(labelNo).enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    Log.d("testUpdatePda", response.body().toString())
                    successCall()
                    recyclerViewStateFlag = true
                    shipRetrieve(shipNo)
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    Log.d("testfailedUpdatePDA", t.message.toString())
                    noNetWork()
                }
            })
        } else {
            successCall()
            if (successFlag.equals("overlap"))
                _dateTimeOverlap.value = Event(Unit)
            else {
                _noLabelNo.value = Event(Unit)
            }

        }
    }

    fun setCardViewColor(pdaDateTime: String?): Int {
        return if (pdaDateTime.isNullOrBlank())
            Color.WHITE
        else
            Color.rgb(255, 249, 196)
    }

    fun setOkTvVisibility(pdaDateTime: String?): Int {
        return if (pdaDateTime.isNullOrBlank())
            View.GONE
        else
            View.VISIBLE
    }

    fun successCall() {
        _isLoading.value = false
    }

    fun noNetWork() {
        _noNetworkConnect.value = Event(Unit)
        successCall()
    }

    fun test(labelNo: String) {
        _test.value = labelNo
    }

}


