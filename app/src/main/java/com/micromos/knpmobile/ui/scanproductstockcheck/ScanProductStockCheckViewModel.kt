package com.micromos.knpmobile.ui.scanproductstockcheck

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.micromos.knpmobile.Event
import com.micromos.knpmobile.MainActivity
import com.micromos.knpmobile.ViewModelBase
import com.micromos.knpmobile.dto.GetCardInfo
import com.micromos.knpmobile.network.ApiResult
import com.micromos.knpmobile.repository.StockCheckRepositoryImpl
import com.micromos.knpmobile.ui.login.LoginViewModel

class ScanProductStockCheckViewModel : ViewModelBase() {

    private val _noNetworkConnect = MutableLiveData<Event<Unit>>()
    val noNetWorkConnect: LiveData<Event<Unit>> = _noNetworkConnect

    private val _noLabelNo = MutableLiveData<Event<Unit>>()
    val noLabelNo: LiveData<Event<Unit>> = _noLabelNo

    private val _unExceptedError = MutableLiveData<Event<Unit>>()
    val unExceptedError : LiveData<Event<Unit>> = _unExceptedError

    private val _noPosCdMatch = MutableLiveData<Event<Unit>>()
    val noPosCdMatch: LiveData<Event<Unit>> = _noPosCdMatch

    private val _noPosCdNo = MutableLiveData<Event<Unit>>()
    val noPosCdNo: LiveData<Event<Unit>> = _noPosCdNo

    val labelNo = MutableLiveData<String?>()
    val inDate = MutableLiveData<String>()
    val posCd = MutableLiveData<String?>()

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
        Log.d("test","test")
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
                    repository.sendRequestLabelNo(stockDate, labelNoTrim, object : ApiResult {
                        override fun onResult() {
                            updateCoilStock()
                            successCall()
                        }

                        override fun nullBody() {
                            insertCoilStock()
                            successCall()
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

    private fun updateCoilStock() {
        _isLoading.value = true
        repository.updateCoilStock(codeCd, labelNoTrim, stockDate, object : ApiResult {
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
        _isLoading.value = true
        repository.insertCoilStock(stockDate,
            LoginViewModel.user_id, labelNoTrim, codeCd, object : ApiResult {
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

    fun setText(stockCls: String?): String {
        return if (stockCls == "I") {
            "신규 업데이트"
        } else {
            "OK"
        }
    }

    fun screenOrientation(){
        onCleared()
    }

    fun unExpectedError(){
        _unExceptedError.value = Event(Unit)
        successCall()
    }

    fun noNetWork() {
        _noNetworkConnect.value = Event(Unit)
        successCall()
    }
    }