package com.micromos.knpmobile.ui.scanproductstockcheck

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.micromos.knpmobile.Event
import com.micromos.knpmobile.MainActivity
import com.micromos.knpmobile.ViewModelBase
import com.micromos.knpmobile.dto.GetCardInfo
import com.micromos.knpmobile.network.ApiResult
import com.micromos.knpmobile.network.StockApiResult
import com.micromos.knpmobile.repository.StockCheckRepositoryImpl
import com.micromos.knpmobile.ui.login.LoginViewModel

class ScanProductStockCheckViewModel : ViewModelBase() {

    private val _noNetworkConnect = MutableLiveData<Event<Unit>>()
    val noNetWorkConnect: LiveData<Event<Unit>> = _noNetworkConnect

    private val _noLabelNo = MutableLiveData<Event<Unit>>()
    val noLabelNo: LiveData<Event<Unit>> = _noLabelNo

    private val _unExceptedError = MutableLiveData<Event<Unit>>()
    val unExceptedError: LiveData<Event<Unit>> = _unExceptedError

    private val _noPosCdMatch = MutableLiveData<Event<Unit>>()
    val noPosCdMatch: LiveData<Event<Unit>> = _noPosCdMatch

    private val _noYardCustCdMatch = MutableLiveData<Event<Unit>>()
    val noYardCustCdMatch: LiveData<Event<Unit>> = _noYardCustCdMatch

    private val _noPosCdNo = MutableLiveData<Event<Unit>>()
    val noPosCdNo: LiveData<Event<Unit>> = _noPosCdNo

    val isScanFlag = MutableLiveData<Boolean>()
    val labelNo = MutableLiveData<String?>()
    val inDate = MutableLiveData<String>()
    val posCd = MutableLiveData<String?>()
    val yardCustCd = MutableLiveData<String>()

    lateinit var stockDate: String
    private lateinit var posCdTrim: String
    private lateinit var labelNoTrim: String
    private lateinit var codeCd: String

    private val _cardItemListDataUpdate = MutableLiveData<GetCardInfo>()
    val cardItemListDataUpdate: LiveData<GetCardInfo> = _cardItemListDataUpdate

    private val _cardItemListDataInsert = MutableLiveData<GetCardInfo>()
    val cardItemListDataInsert: LiveData<GetCardInfo> = _cardItemListDataInsert

    private val repository = StockCheckRepositoryImpl()

    init {
        getServerTime()
        isScanFlag.value = false
    }

    private fun getServerTime() {
        repository.sendRequestServerTime(object : ApiResult {
            override fun onResult() {
                val serverTime = repository.getServerTime()
                val year = serverTime.substring(0, 4)
                val month = serverTime.substring(4, 6)
                val day = serverTime.substring(6, 8)
                inDate.value = "$year / $month / $day"
            }

            override fun nullBody() {
                unExpectedError()
            }

            override fun onFailure() {
                noNetWork()
            }
        })
    }

    fun labelRetrieve() {
        if (isScanFlag.value == true) return
        isScanFlag.value = true
        posCdTrim = posCd.value?.trim().toString()
        labelNoTrim = labelNo.value?.trim().toString()
        MainActivity.codeList.forEach {
            if (it.value == posCdTrim) {
                codeCd = it.key
            }
        }

        if (posCdTrim != "null") {
            if (posCdTrim in MainActivity.codeNmList) {
                if (labelNoTrim != "null" && labelNoTrim != "") {
                    _isLoading.value = true
                    repository.sendRequestLabelNo(
                        stockDate,
                        labelNoTrim,
                        yardCustCd.value!!,
                        object : StockApiResult {
                            override fun onResult(checkYardCust: Boolean, packCls: Int) {
                                if (checkYardCust) {
                                    _noYardCustCdMatch.value = Event(Unit)
                                    successCall()
                                } else {
                                    updateCoilStock(packCls)
                                }
                            }
                            override fun nullBody() {
                                insertCoilStock()
                            }
                            override fun onFailure() {
                                noNetWork()
                            }
                        })
                } else {
                    _noLabelNo.value = Event(Unit)
                }
            } else {
                _noPosCdMatch.value = Event(Unit)
            }
        } else {
            _noPosCdNo.value = Event(Unit)
        }
    }

    private fun updateCoilStock(packCls: Int) {
        repository.updateCoilStock(
            codeCd,
            labelNoTrim,
            stockDate,
            yardCustCd.value!!,
            packCls,
            object : ApiResult {
                override fun onResult() {
                    _cardItemListDataUpdate.value = repository.getCardInfo().value
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

    private fun insertCoilStock() {
        repository.insertCoilStock(stockDate,
            LoginViewModel.user_id, labelNoTrim, codeCd, yardCustCd.value!!, object : ApiResult {
                override fun onResult() {
                    _cardItemListDataInsert.value = repository.getCardInfo().value
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

    fun setCardVisibility(stockCls: String): Int {
        return if (stockCls == "I") {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    fun setText(stockCls: String?, cntCheckFlag: Int): String {
        return if (stockCls == "I") {
            if(cntCheckFlag == 1){
                "관리자 확인요망!"
            }else {
                "신규 업데이트"
            }
        } else {
            "OK"
        }
    }

    fun screenOrientation() {
        onCleared()
    }

    fun unExpectedError() {
        _unExceptedError.value = Event(Unit)
        successCall()
    }

    fun noNetWork() {
        _noNetworkConnect.value = Event(Unit)
        successCall()
    }
}