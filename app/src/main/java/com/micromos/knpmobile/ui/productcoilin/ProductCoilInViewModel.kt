package com.micromos.knpmobile.ui.productcoilin

import android.graphics.Color
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.micromos.knpmobile.BuildConfig
import com.micromos.knpmobile.Event
import com.micromos.knpmobile.ViewModelBase
import com.micromos.knpmobile.dto.ShipOrder
import com.micromos.knpmobile.network.ApiResult
import com.micromos.knpmobile.network.KNPApi
import com.micromos.knpmobile.repository.ProductCoilRepositoryImpl

class ProductCoilInViewModel : ViewModelBase() {

    private var shipNoList = setOf("QS", "RS", "FS", "SS", "BS", "MS")
    val sellCustCd = MutableLiveData<String>()
    val venCustCd = MutableLiveData<String>()
    val dlvCustCd = MutableLiveData<String>()
    val custCdVisibility = MutableLiveData(View.INVISIBLE)

    private val _noRetrieve = MutableLiveData<Event<Unit>>()
    val noRetrieve: LiveData<Event<Unit>> = _noRetrieve

    private val _noNetworkConnect = MutableLiveData<Event<Unit>>()
    val noNetWorkConnect: LiveData<Event<Unit>> = _noNetworkConnect

    private val _noLabelNo = MutableLiveData<Event<Unit>>()
    val noLabelNo: LiveData<Event<Unit>> = _noLabelNo

    private val _noModifyEvent = MutableLiveData<Event<Unit>>()
    val noModifyEvent: LiveData<Event<Unit>> = _noModifyEvent

    private val _dateTimeOverlap = MutableLiveData<Event<Unit>>()
    val dateTimeOverlap: LiveData<Event<Unit>> = _dateTimeOverlap

    private val _noCompleteAllLabel = MutableLiveData<Event<Unit>>()
    val noCompleteAllLabel: LiveData<Event<Unit>> = _noCompleteAllLabel

    val _requestNo = MutableLiveData<String?>()

    val shipOrderList = MutableLiveData<List<ShipOrder>>()

    val labelNoList = mutableListOf<String?>()
    val modifyClsList = mutableListOf<Int?>()
    val pdaDateTimeCoilInList = mutableListOf<String?>()

    private val _recyclerViewState = MutableLiveData<Event<Unit>>()
    val recyclerViewState: LiveData<Event<Unit>> = _recyclerViewState
    var recyclerViewStateFlag: Boolean = false

    var shipNo: String? = null

    var numerator: Int = 0
    var denomiator: Int = 0

    val prevShipNo = MutableLiveData<String?>(null)
    private val _cardClick = MutableLiveData<String>()
    val cardClick: LiveData<String> = _cardClick

    private val repository = ProductCoilRepositoryImpl()


    fun shipNoRetrieve(_requestNo: String?) {
        val requestNo = _requestNo?.trim()

        if (requestNo != null) {
            _isLoading.value = true
            if (requestNo.length == 11 && requestNo.substring(0, 2) in shipNoList) {
                when {
                    prevShipNo.value.equals(null) -> {
                        prevShipNo.value = requestNo
                        getCommonInfo(requestNo)
                    }
                    prevShipNo.value != requestNo -> {
                        _noCompleteAllLabel.value = Event(Unit)
                    }
                    else -> {
                        getCommonInfo(requestNo)
                    }
                }/*
                if (this._requestNo.value?.trim() == requestNo) {
                    recyclerViewStateFlag = false
                }*/
            } else {
                //_recyclerViewState.value = Event(Unit)
                labelRetrieve(requestNo)
            }
        }
    }

    private fun getCommonInfo(requestNo: String) {
        getCustCd(requestNo)
        getShipOrder(requestNo)
        prevShipNo.value = requestNo
    }

    private fun getCustCd(requestNo: String) {
        repository.sendRequestCustCd(requestNo, object : ApiResult {
            override fun onResult() {
                val data = repository.getCustCD()
                sellCustCd.value = data.value?.custNm ?: ""
                venCustCd.value = data.value?.venCustNm ?: ""
                dlvCustCd.value = data.value?.dlvCustNm ?: ""
                custCdVisibility.value = View.VISIBLE
            }

            override fun nullBody() {
                _noRetrieve.value = Event(Unit)
                custCdVisibility.value = View.INVISIBLE
            }

            override fun onFailure() {
                noNetWork()
            }

        })
    }

    private fun getShipOrder(requestNo: String) {
        repository.sendRequestShipOrder(requestNo, 1, object : ApiResult {
            override fun onResult() {
                shipOrderList.value = repository.getShipOrder()
                val size = repository.getItemSize()
                numerator = 0
                denomiator = 0

                if (size != 0) {
                    denomiator = size
                    labelNoList.clear()
                    modifyClsList.clear()
                    pdaDateTimeCoilInList.clear()
                    for (i in 0 until size) {
                        labelNoList.add(shipOrderList.value?.get(i)?.labelNo)
                        modifyClsList.add(shipOrderList.value?.get(i)?.modifyCLS)
                        pdaDateTimeCoilInList.add(shipOrderList.value?.get(i)?.pdaDateTimeIn)
                        if (!pdaDateTimeCoilInList[i].isNullOrEmpty()) {
                            numerator++
                        }
                    }
                }
                shipNo = requestNo
                successCall()
            }

            override fun nullBody() {
                TODO("Not yet implemented")
            }

            override fun onFailure() {
                noNetWork()
            }
        })

    }

    private fun labelRetrieve(labelNo: String) {
        var successFlag = "false"
        _isLoading.value = true
        for (i in 0 until labelNoList.size) {
            if (labelNo == labelNoList[i]) {
                if (modifyClsList[i] == 1) {
                    if (pdaDateTimeCoilInList[i].isNullOrEmpty()) {
                        successFlag = "true"
                    } else {
                        successFlag = "overlap"
                        successCall()
                    }
                } else {
                    successFlag = "noModify"
                }
                break
            }
        }
        if (successFlag == "true") {
            repository.updateTimePDA("IN", labelNo, shipNo!!, object : ApiResult {
                override fun onResult() {
                    successCall()
                    //recyclerViewStateFlag = true 2021. 03. 04 부로 사용하지 않는 기능
                    shipNoRetrieve(shipNo)
                }

                override fun nullBody() {
                    TODO("Not yet implemented")
                }

                override fun onFailure() {
                    noNetWork()
                }
            })
        } else {
            successCall()
            when (successFlag) {
                "overlap" -> _dateTimeOverlap.value = Event(Unit)
                "noModify" -> _noModifyEvent.value = Event(Unit)
                "false" -> _noLabelNo.value = Event(Unit)
            }
        }
    }

    fun setCardViewColor(pdaDateTime: String?): Int {
        return if (pdaDateTime.isNullOrBlank())
            Color.WHITE
        else
            Color.rgb(255, 253, 231)
    }

    fun setOkTvVisibility(pdaDateTime: String?): Int {
        return if (pdaDateTime.isNullOrBlank())
            View.GONE
        else
            View.VISIBLE
    }

    fun noNetWork() {
        _noNetworkConnect.value = Event(Unit)
        successCall()
    }

    fun cardClick(labelNo: String) {
        if(BuildConfig.DEBUG)
        _cardClick.value = labelNo
    }

}


