//package com.micromos.ddsteelmobile.ui.scanproductcoilin
//
//import android.graphics.Color
//import android.view.View
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import com.micromos.ddsteelmobile.BuildConfig
//import com.micromos.ddsteelmobile.Event
//import com.micromos.ddsteelmobile.ViewModelBase
//import com.micromos.ddsteelmobile.dto.ShipOrder
//import com.micromos.ddsteelmobile.network.ApiResult
//import com.micromos.ddsteelmobile.repository.ProductCoilRepositoryImpl
//
//
//class ScanProductCoilInViewModel : ViewModelBase() {
//
//    private var shipNoList = setOf("QS", "RS", "FS", "SS", "BS", "MS")
//    val sellCustCd = MutableLiveData<String>()
//    val yardCustCd = MutableLiveData<String>()
//    val dlvCustCd = MutableLiveData<String>()
//    val custCdVisibility = MutableLiveData(View.INVISIBLE)
//
//    private val _noRetrieve = MutableLiveData<Event<Unit>>()
//    val noRetrieve: LiveData<Event<Unit>> = _noRetrieve
//
//    private val _unExceptedError = MutableLiveData<Event<Unit>>()
//    val unExceptedError: LiveData<Event<Unit>> = _unExceptedError
//
//    private val _noNetworkConnect = MutableLiveData<Event<Unit>>()
//    val noNetWorkConnect: LiveData<Event<Unit>> = _noNetworkConnect
//
//    val _noLabelNo = MutableLiveData<Event<Unit>>()
//    val noLabelNo: LiveData<Event<Unit>> = _noLabelNo
//
//    private val _noModifyEvent = MutableLiveData<Event<Unit>>()
//    val noModifyEvent: LiveData<Event<Unit>> = _noModifyEvent
//
//    private val _dateTimeOverlap = MutableLiveData<Event<Unit>>()
//    val dateTimeOverlap: LiveData<Event<Unit>> = _dateTimeOverlap
//
//    private val _noCompleteAllLabel = MutableLiveData<Event<Unit>>()
//    val noCompleteAllLabel: LiveData<Event<Unit>> = _noCompleteAllLabel
//
//    val _requestNo = MutableLiveData<String?>()
//
//    val shipOrderList = MutableLiveData<List<ShipOrder>>()
//
//    val modifyClsList = mutableListOf<Int?>()
//    val scanDateList = mutableListOf<String?>()
//
//    var shipNo: String? = null
//
//    var numerator: Int = 0
//    var denomiator: Int = 0
//
//    var numeratorStr =  MutableLiveData<String>()
//    var denomiatorStr = MutableLiveData<String>()
//    var hyphen = MutableLiveData<String>()
//
//    val prevShipNo = MutableLiveData<String?>(null)
//    private val _cardClick = MutableLiveData<String>()
//    val cardClick: LiveData<String> = _cardClick
//
//    private val repository = ProductCoilRepositoryImpl()
//
//
//    fun shipNoRetrieve(_requestNo: String?) {
//        val requestNo = _requestNo?.trim()
//
//        if (requestNo != null) {
//            _isLoading.value = true
//            if (requestNo.length == 11 && requestNo.substring(0, 2) in shipNoList) {
//                when {
//                    prevShipNo.value.equals(null) -> {
//                        prevShipNo.value = requestNo
//                        getCommonInfo(requestNo)
//                    }
//                    prevShipNo.value != requestNo -> {
//                        _noCompleteAllLabel.value = Event(Unit)
//                    }
//                    else -> {
//                        getCommonInfo(requestNo)
//                    }
//                }
//            } else {
//                labelRetrieve(requestNo)
//            }
//        }
//    }
//
//    private fun getCommonInfo(requestNo: String) {
//        getCustCd(requestNo)
//        getShipOrder(requestNo)
//        prevShipNo.value = requestNo
//    }
//
//    private fun getCustCd(requestNo: String) {
//        repository.sendRequestCustCd(requestNo, object : ApiResult {
//            override fun onResult() {
//                val data = repository.getCustCD()
//                sellCustCd.value = data.value?.custNm ?: ""
//                yardCustCd.value = data.value?.yardCustNm ?: ""
//                dlvCustCd.value = data.value?.dlvCustNm ?: ""
//                custCdVisibility.value = View.VISIBLE
//            }
//
//            override fun nullBody() {
//                _noRetrieve.value = Event(Unit)
//                custCdVisibility.value = View.INVISIBLE
//            }
//
//            override fun onFailure() {
//                noNetWork()
//            }
//
//        })
//    }
//
//    private fun getShipOrder(requestNo: String) {
//        repository.sendRequestShipOrder(requestNo, object : ApiResult {
//            override fun onResult() {
//                shipOrderList.value = repository.getShipOrder()
//                val size = repository.getItemSize()
//                numerator = 0
//                denomiator = 0
//
//                if (size != 0) {
//                    denomiator = size
//                    modifyClsList.clear()
//                    scanDateList.clear()
//                    for (i in 0 until size) {
//                        //modifyClsList.add(shipOrderList.value?.get(i)?.modifyCLS)
//                        scanDateList.add(shipOrderList.value?.get(i)?.scanDate)
//                        if (!scanDateList[i].isNullOrEmpty()) {
//                            numerator++
//                        }
//                    }
//                }
//                numeratorStr.value = numerator.toString()
//                denomiatorStr.value = denomiator.toString()
//                hyphen.value = "/"
//
//                shipNo = requestNo
//                successCall()
//            }
//
//            override fun nullBody() {
//                unExpectedError()
//            }
//
//            override fun onFailure() {
//                noNetWork()
//            }
//        })
//
//    }
//
//    private fun labelRetrieve(labelNo: String) {
//        var successFlag = "false"
//        _isLoading.value = true
////        for (i in 0 until labelNoList.size) {
////            if (labelNo == labelNoList[i]) {
////                if (modifyClsList[i] == 1) {
////                    if (scanDateList[i].isNullOrEmpty()) {
////                        successFlag = "true"
////                    } else {
////                        successFlag = "overlap"
////                        successCall()
////                    }
////                } else {
////                    successFlag = "noModify"
////                }
////                break
////            }
////        }
//        if (successFlag == "true") {
//            repository.updateScanDate("IN", labelNo, shipNo!!, object : ApiResult {
//                override fun onResult() {
//                    successCall()
//                    shipNoRetrieve(shipNo)
//                }
//
//                override fun nullBody() {
//                    unExpectedError()
//                }
//
//                override fun onFailure() {
//                    noNetWork()
//                }
//            })
//        } else {
//            successCall()
//            when (successFlag) {
//                "overlap" -> _dateTimeOverlap.value = Event(Unit)
//                "noModify" -> _noModifyEvent.value = Event(Unit)
//                "false" -> _noLabelNo.value = Event(Unit)
//            }
//        }
//    }
//
//    fun setCardViewColor(pdaDateTime: String?): Int {
//        return if (pdaDateTime.isNullOrBlank())
//            Color.WHITE
//        else
//            Color.rgb(224, 247, 250)
//    }
//
//    fun setOkTvVisibility(pdaDateTime: String?): Int {
//        return if (pdaDateTime.isNullOrBlank())
//            View.GONE
//        else
//            View.VISIBLE
//    }
//
//    fun noNetWork() {
//        _noNetworkConnect.value = Event(Unit)
//        successCall()
//    }
//
//    fun cardClick(labelNo: String) {
//        if (BuildConfig.DEBUG)
//            _cardClick.value = labelNo
//    }
//
//    fun unExpectedError() {
//        _unExceptedError.value = Event(Unit)
//        successCall()
//    }
//
//    fun screenOrientation(){
//        onCleared()
//    }
//
//}
