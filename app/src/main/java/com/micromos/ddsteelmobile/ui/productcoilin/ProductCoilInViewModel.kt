package com.micromos.ddsteelmobile.ui.productcoilin

import android.graphics.Color
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.micromos.ddsteelmobile.Event
import com.micromos.ddsteelmobile.ViewModelBase
import com.micromos.ddsteelmobile.dto.ShipOrder
import com.micromos.ddsteelmobile.network.ApiResult
import com.micromos.ddsteelmobile.repository.ProductCoilRepositoryImpl
import com.micromos.ddsteelmobile.ui.login.LoginViewModel.Companion.user_id

class ProductCoilInViewModel : ViewModelBase() {

    private var shipNoList = setOf("DD")
    val sellCustCd = MutableLiveData<String>()
    val dlvCustCd = MutableLiveData<String>()
    val salesManCd = MutableLiveData<String>()
    val shipType = MutableLiveData<String>()
    val regionCd = MutableLiveData<String>()
    val remark = MutableLiveData<String>()
    val shipMasterNo = MutableLiveData<String>()

    val showRemarkButtonVisibility = MutableLiveData(View.INVISIBLE)
    val coilInVisibility = MutableLiveData(View.INVISIBLE)

    private val _noRetrieve = MutableLiveData<Event<Unit>>()
    val noRetrieve: LiveData<Event<Unit>> = _noRetrieve

    private val _unExceptedError = MutableLiveData<Event<Unit>>()
    val unExceptedError: LiveData<Event<Unit>> = _unExceptedError

    private val _noNetworkConnect = MutableLiveData<Event<Unit>>()
    val noNetWorkConnect: LiveData<Event<Unit>> = _noNetworkConnect

    private val _noStockEvent = MutableLiveData<Event<Unit>>()
    val noStockEvent: LiveData<Event<Unit>> = _noStockEvent

    private val _noLabelNo = MutableLiveData<Event<Unit>>()
    val noLabelNo: LiveData<Event<Unit>> = _noLabelNo

    private val _commonErrorEvent = MutableLiveData<Event<Unit>>()
    val commonErrorEvent: LiveData<Event<Unit>> = _commonErrorEvent

    private val _dateTimeOverlap = MutableLiveData<Event<Unit>>()
    val dateTimeOverlap: LiveData<Event<Unit>> = _dateTimeOverlap

    private val _noCompleteAllLabel = MutableLiveData<Event<Unit>>()
    val noCompleteAllLabel: LiveData<Event<Unit>> = _noCompleteAllLabel

    private val _forcedUpdateEvent = MutableLiveData<Event<Unit>>()
    val forcedUpdateEvent: LiveData<Event<Unit>> = _forcedUpdateEvent

    private val _rollbackUpdateEvent = MutableLiveData<Event<Unit>>()
    val rollbackUpdateEvent: LiveData<Event<Unit>> = _rollbackUpdateEvent

    private val _notInputShipNoEvent = MutableLiveData<Event<Unit>>()
    val notInputShipNoEvent: LiveData<Event<Unit>> = _notInputShipNoEvent

    private val _clickRemarkButtonEvent = MutableLiveData<Event<Unit>>()
    val clickRemarkButtonEvent : LiveData<Event<Unit>> = _clickRemarkButtonEvent

    val _requestNo = MutableLiveData<String?>()

    val shipOrderList = MutableLiveData<List<ShipOrder>>()
    var changePosition = MutableLiveData<Int?>(null)

    var shipNo: String? = null
    var errorCode: Int? = null

    val prevShipNo = MutableLiveData<String?>(null)

    var numerator = MutableLiveData<Int>(0)
    var denomiator = MutableLiveData<Int>(0)

    lateinit var clickShipNo: String
    lateinit var clickShipSeq: String

    private val repository = ProductCoilRepositoryImpl()

    private val _onClickScanButton = MutableLiveData<Event<Unit>>()
    val onClickScanButton: LiveData<Event<Unit>> = _onClickScanButton

    fun shipNoRetrieve(_requestNo: String?) {
        val requestNo = _requestNo?.trim()
        if (!requestNo.isNullOrEmpty()) {
            _isLoading.value = true
            if (requestNo.length == 10 && requestNo.substring(0, 2) in shipNoList) {
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
                }
            } else {
                labelRetrieve(requestNo)
            }
        }
    }

    private fun getCommonInfo(requestNo: String) {
        numerator.value = 0
        denomiator.value = 0
        prevShipNo.value = requestNo
        getCustCd(requestNo)
    }

    private fun getCustCd(requestNo: String) {
        repository.sendRequestCustCd(requestNo, object : ApiResult {
            override fun onResult() {
                val data = repository.getCustCD()

                shipMasterNo.value = requestNo.substring(0, 2) + "-" + requestNo.substring(
                    2,
                    4
                ) + "-" + requestNo.substring(4, requestNo.length)
                sellCustCd.value = data.value?.custNm ?: ""
                dlvCustCd.value = data.value?.dlvCustNm ?: ""
                salesManCd.value = data.value?.salesManNm ?: ""
                shipType.value = data.value?.shipType ?: ""
                regionCd.value = data.value?.regionNm ?: ""
                remark.value = data.value?.remark ?: ""
                if(remark.value.isNullOrEmpty()){
                    showRemarkButtonVisibility.value = View.INVISIBLE
                }else{
                    showRemarkButtonVisibility.value = View.VISIBLE
                }
                coilInVisibility.value = View.VISIBLE
                getShipOrder(requestNo)
            }

            override fun nullBody() {
                _noRetrieve.value = Event(Unit)
                coilInVisibility.value = View.INVISIBLE
                successCall()
            }

            override fun onFailure() {
                noNetWork()
            }
        })
    }

    private fun getShipOrder(requestNo: String) {
        repository.sendRequestShipOrder(requestNo, object : ApiResult {
            override fun onResult() {
                changePosition.value = null
                shipOrderList.value = repository.getShipOrder()
                val size = repository.getItemSize()
                denomiator.value = size
                for (i in 0 until size) {
                    if (!shipOrderList.value!![i].scanDate.isNullOrEmpty()) {
                        numerator.value = numerator.value?.plus(1)
                    }
                }
                shipNo = requestNo
                successCall()
            }

            override fun nullBody() {
                unExpectedError()
            }

            override fun onFailure() {
                noNetWork()
            }
        })
    }

    private fun labelRetrieve(labelNo: String) {
        if (shipMasterNo.value.isNullOrEmpty()) {
            _notInputShipNoEvent.value = Event(Unit)
            successCall()
        } else {
            _isLoading.value = true
            repository.updateScanDate(user_id!!, labelNo, shipNo!!, object : ApiResult {
                override fun onResult() {
                    //recyclerViewStateFlag = true 2021. 03. 04 부로 사용하지 않는 기능
                    val scanInfoResult = repository.getUpdateScanResult()
                    if (scanInfoResult.value!!.returnResult == "0") {
                        for (i in 0 until repository.getItemSize()) {
                            if (shipOrderList.value!![i].shipNo == scanInfoResult.value!!.shipNo && shipOrderList.value!![i].shipSeq == scanInfoResult.value!!.shipSeq) {
                                changePosition.value = i
                                shipOrderList.value!![i].scanDate = scanInfoResult.value!!.scanDate
                                shipOrderList.value!![i].scanCls = scanInfoResult.value!!.scanCls
                                shipOrderList.value!![i].scanTime = scanInfoResult.value!!.scanTime
                                shipOrderList.value!![i].coilNo = scanInfoResult.value!!.coilNo
                                shipOrderList.value!![i].coilSeq = scanInfoResult.value!!.actlCoilSeq

                                numerator.value = numerator.value?.plus(1)
                                shipOrderList.value = shipOrderList.value
                            }
                        }
                        successCall()
                    } else {
                        when (scanInfoResult.value!!.returnResult) {
                            "1" -> {
                                _noStockEvent.value = Event(Unit)
                            }
                            "2" -> {
                                _dateTimeOverlap.value = Event(Unit)
                            }
                            "3" -> {
                                _noLabelNo.value = Event(Unit)
                            }
                            "4" -> {
                                _commonErrorEvent.value = Event(Unit)
                                errorCode = 4
                            }
                            "5" -> {
                                _commonErrorEvent.value = Event(Unit)
                                errorCode = 5
                            }
                            "6" -> {
                                _commonErrorEvent.value = Event(Unit)
                                errorCode = 6
                            }
                            "7" -> {
                                _commonErrorEvent.value = Event(Unit)
                                errorCode = 7
                            }
                        }
                        successCall()
                    }
                }

                override fun nullBody() {
                    unExpectedError()
                }

                override fun onFailure() {
                    noNetWork()
                }
            })
        }
    }

    fun cardClick(shipNo: String, shipSeq: String, scanDate: String?) {
        clickShipNo = shipNo
        clickShipSeq = shipSeq
        if (scanDate.isNullOrEmpty()) {
            _forcedUpdateEvent.value = Event(Unit)

        } else {
            _rollbackUpdateEvent.value = Event(Unit)
        }
    }

    fun clickShowRemarkButton(){
        _clickRemarkButtonEvent.value = Event(Unit)
    }


    fun forcedUpdate() {
        _isLoading.value = true
        repository.forcedUpdate(user_id!!, clickShipNo, clickShipSeq, object : ApiResult {
            override fun onResult() {
                for (i in 0 until repository.getItemSize()) {
                    val scanInfoResult = repository.getUpdateScanResult()
                    if (shipOrderList.value!![i].shipNo == scanInfoResult.value!!.shipNo && shipOrderList.value!![i].shipSeq == scanInfoResult.value!!.shipSeq) {
                        changePosition.value = i
                        shipOrderList.value!![i].scanDate = scanInfoResult.value!!.scanDate
                        shipOrderList.value!![i].scanCls = scanInfoResult.value!!.scanCls
                        shipOrderList.value!![i].scanTime = scanInfoResult.value!!.scanTime
                        shipOrderList.value = shipOrderList.value

                        numerator.value = numerator.value?.plus(1)
                    }
                }
                successCall()
            }

            override fun nullBody() {
                unExpectedError()
            }

            override fun onFailure() {
                noNetWork()
            }
        })
    }

    fun rollbackUpdate() {
        _isLoading.value = true

        repository.rollbackUpdate(clickShipNo, clickShipSeq, object : ApiResult {
            override fun onResult() {
                for (i in 0 until repository.getItemSize()) {
                    val scanInfoResult = repository.getUpdateScanResult()
                    if (shipOrderList.value!![i].shipNo == scanInfoResult.value!!.shipNo && shipOrderList.value!![i].shipSeq == scanInfoResult.value!!.shipSeq) {
                        changePosition.value = i
                        shipOrderList.value!![i].scanDate = null
                        shipOrderList.value!![i].scanCls = null
                        shipOrderList.value!![i].scanTime = null
                        shipOrderList.value = shipOrderList.value
                        numerator.value = numerator.value?.minus(1)
                    }
                }
                successCall()
            }

            override fun nullBody() {
                unExpectedError()
            }

            override fun onFailure() {
                noNetWork()
            }
        })
    }

    fun setCardViewColor(scan_date: String?): Int {
        return if (scan_date.isNullOrBlank())
            Color.WHITE
        else
            Color.rgb(224, 247, 250)
    }

    fun setOkTvVisibility(scan_date: String?): Int {
        return if (scan_date.isNullOrBlank())
            View.GONE
        else
            View.VISIBLE
    }

    fun noNetWork() {
        _noNetworkConnect.value = Event(Unit)
        successCall()
    }

    fun unExpectedError() {
        _unExceptedError.value = Event(Unit)
        successCall()
    }

    fun screenOrientation() {
        onCleared()
    }

    fun scanBarCode() {
        _onClickScanButton.value = Event(Unit)
    }


}


