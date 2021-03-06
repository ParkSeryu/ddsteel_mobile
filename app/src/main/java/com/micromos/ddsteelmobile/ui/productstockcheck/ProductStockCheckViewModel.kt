package com.micromos.ddsteelmobile.ui.productstockcheck

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.micromos.ddsteelmobile.Event
import com.micromos.ddsteelmobile.MainActivity.Companion.posList
import com.micromos.ddsteelmobile.MainActivity.Companion.pos_nm_list
import com.micromos.ddsteelmobile.ViewModelBase
import com.micromos.ddsteelmobile.dto.GetCardInfo
import com.micromos.ddsteelmobile.network.ApiResult
import com.micromos.ddsteelmobile.network.StockApiResult
import com.micromos.ddsteelmobile.repository.StockCheckRepositoryImpl
import com.micromos.ddsteelmobile.ui.login.LoginViewModel.Companion.user_id

class ProductStockCheckViewModel : ViewModelBase() {

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

    private val _selectDateEvent = MutableLiveData<Event<Unit>>()
    val selectDateEvent: LiveData<Event<Unit>> = _selectDateEvent

    private val _showDatePickerDialogEvent = MutableLiveData<Event<Unit>>()
    val showDatePickerDialogEvent: LiveData<Event<Unit>> = _showDatePickerDialogEvent

    val isScanFlag = MutableLiveData<Boolean>()
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

    fun showDatePickerDialog() {
        _showDatePickerDialogEvent.value = Event(Unit)
    }

    fun btnDateOk() {
        _selectDateEvent.value = Event(Unit)
        stockDate =
            inDate.value.toString().replace(" ", "").replace("\\p{Z}", "").replace("/", "")
    }

    fun labelRetrieve() {
        posCdTrim = posCd.value?.trim().toString()
        labelNoTrim = labelNo.value?.trim().toString()
        posList.forEach {
            if (it.value == posCdTrim) {
                codeCd = it.key
            }
        }

        if (posCdTrim != "null") {
            if (posCdTrim in pos_nm_list) {
                if (labelNoTrim != "null" && labelNoTrim != "") {
                    _isLoading.value = true
                    repository.sendRequestLabelNo(
                        stockDate,
                        labelNoTrim,
                        object : StockApiResult {
                            override fun onResult(packCls: Int) {
                                updateCoilStock(packCls)
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
        repository.insertCoilStock(
            stockDate,
            user_id,
            labelNoTrim,
            codeCd,
            object : ApiResult {
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
            if (cntCheckFlag == 1) {
                "????????? ????????????!"
            } else {
                "?????? ????????????"
            }
        } else {
            "OK"
        }
    }

    fun unExpectedError() {
        _unExceptedError.value = Event(Unit)
        successCall()
    }

    fun noNetWork() {
        _noNetworkConnect.value = Event(Unit)
        successCall()
    }

    fun screenOrientation() {
        onCleared()
    }

}


