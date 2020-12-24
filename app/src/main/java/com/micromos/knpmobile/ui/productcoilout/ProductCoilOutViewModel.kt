package com.micromos.knpmobile.ui.productcoilout

import android.graphics.Color
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.micromos.knpmobile.Event
import com.micromos.knpmobile.ViewModelBase
import com.micromos.knpmobile.dto.ShipOrder
import com.micromos.knpmobile.dto.ShipOrderFeed
import com.micromos.knpmobile.dto.GetCustCd
import com.micromos.knpmobile.network.KNPApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductCoilOutViewModel : ViewModelBase() {

    private val api = KNPApi.create()
    private var shipNoList = setOf("QS", "RS", "FS", "SS", "BS", "MS")

    val _sell_cust_cd = MutableLiveData<String>()
    val _ven_cust_cd = MutableLiveData<String>()
    val _dlv_cust_cd = MutableLiveData<String>()
    val cust_cd_visibility = MutableLiveData(View.INVISIBLE)

    private val _noRetrieve = MutableLiveData<Event<Unit>>()
    val noRetrieve: LiveData<Event<Unit>> = _noRetrieve

    private val _noNetworkConnect = MutableLiveData<Event<Unit>>()
    val noNetWorkConnect: LiveData<Event<Unit>> = _noNetworkConnect

    private val _noLabelNo = MutableLiveData<Event<Unit>>()
    val noLabelNo: LiveData<Event<Unit>> = _noLabelNo

    private val _dateTimeOverlap = MutableLiveData<Event<Unit>>()
    val dateTimeOverlap: LiveData<Event<Unit>> = _dateTimeOverlap

    private val _noCompleteCoilIn = MutableLiveData<Event<Unit>>()
    val noCompleteCoilIn: LiveData<Event<Unit>> = _noCompleteCoilIn

    private val _noCompleteAllLabel = MutableLiveData<Event<Unit>>()
    val noCompleteAllLabel: LiveData<Event<Unit>> = _noCompleteAllLabel

    val _requestNo = MutableLiveData<String?>()

    val coilInData = MutableLiveData<List<ShipOrder>>()

    val labelNoList = mutableListOf<String?>()
    val pdaDateTimeInList = mutableListOf<String?>()
    val pdaDateTimeOutList = mutableListOf<String?>()

    val _recyclerViewState = MutableLiveData<Event<Unit>>()
    var recyclerViewStateFlag: Boolean = false

    var shipNo: String? = null

    var numerator: Int = 0
    var denomiator: Int = 0

    val prevShipNo = MutableLiveData<String?>(null)

    val _test = MutableLiveData<String>()
    val test: LiveData<String> = _test

    fun shipRetrieve(requestNo: String?) {
        val requestNo = requestNo?.trim()
        //requestNo?.toUpperCase(Locale.ROOT)

        if (requestNo != null) {
            _isLoading.value = true
            if (requestNo.length == 11 && requestNo.substring(0, 2) in shipNoList) {
                Log.d("tete", "${prevShipNo.value} / ${_requestNo.value}")
                if (prevShipNo.value.equals(null)) {
                    prevShipNo.value = requestNo
                    settings(requestNo)
                } else if (prevShipNo.value != requestNo) {
                    _noCompleteAllLabel.value = Event(Unit)
                } else {
                    settings(requestNo)
                }
                if (_requestNo.value?.trim() == requestNo) {
                    recyclerViewStateFlag = false
                }
            } else {
                _recyclerViewState.value = Event(Unit)
                setCoilOut(requestNo)
            }
        }
    }

    private fun settings(requestNo: String) {
        getCustCd(requestNo)
        getShipList(requestNo)
        prevShipNo.value = requestNo
    }

    private fun getCustCd(requestNo: String) {
        api.getCustCd(requestNo).enqueue(object : Callback<GetCustCd> {
            override fun onResponse(call: Call<GetCustCd>, response: Response<GetCustCd>) {
                Log.d("testCustCd", response.body().toString())
                _sell_cust_cd.value = response.body()?.custNm.toString()
                _ven_cust_cd.value = response.body()?.venCustNm ?: ""
                _dlv_cust_cd.value = response.body()?.dlvCustNm ?: ""
                if (response.code() == 200) {
                    cust_cd_visibility.value = View.VISIBLE
                } else {
                    _noRetrieve.value = Event(Unit)
                    cust_cd_visibility.value = View.INVISIBLE
                }
            }

            override fun onFailure(call: Call<GetCustCd>, t: Throwable) {
                Log.d("testfailedCust", t.message.toString())
                noNetWork()
            }
        })
    }

    private fun getShipList(requestNo: String) {
        api.getShipOrder(requestNo).enqueue(object : Callback<ShipOrderFeed> {
            override fun onResponse(
                call: Call<ShipOrderFeed>,
                response: Response<ShipOrderFeed>
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
                    pdaDateTimeInList.clear()
                    pdaDateTimeOutList.clear()
                    for (i in 0 until length) {
                        labelNoList.add(coilInData.value?.get(i)?.labelNo)
                        pdaDateTimeInList.add(coilInData.value?.get(i)?.pdaDateTimeIn)
                        pdaDateTimeOutList.add(coilInData.value?.get(i)?.pdaDateTimeOut)
                        if (!pdaDateTimeOutList[i].isNullOrEmpty()) {
                            numerator++
                        }
                    }
                }
                shipNo = requestNo
                successCall()
            }

            override fun onFailure(call: Call<ShipOrderFeed>, t: Throwable) {
                Log.d("testfailedCoilIn", t.message.toString())
                noNetWork()
            }
        })
    }

    private fun setCoilOut(labelNo: String) {
        var successFlag = "false"
        _isLoading.value = true
        for (i in 0 until labelNoList.size) {
            if (labelNo == labelNoList[i]) {
                successFlag = if (pdaDateTimeInList[i].isNullOrEmpty()) {
                    "notCoilIn"
                } else if (pdaDateTimeOutList[i].isNullOrEmpty()) {
                    "true"
                } else {
                    "overlap"
                }
                break
            }
        }

        if (successFlag.equals("true")) {
            api.updatePDAout(labelNo).enqueue(object : Callback<Unit> {
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
            when (successFlag) {
                "overlap" -> _dateTimeOverlap.value = Event(Unit)
                "notCoilIn" -> _noCompleteCoilIn.value = Event(Unit)
                "false" -> _noLabelNo.value = Event(Unit)
            }
        }
    }

    fun setCardViewColor(pdaDateTime: String?): Int {
        return if (pdaDateTime.isNullOrBlank())
            Color.WHITE
        else
            Color.rgb(178, 218, 238)
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


