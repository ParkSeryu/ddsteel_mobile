package com.micromos.knpmobile.ui.productstockcheck

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.micromos.knpmobile.Event
import com.micromos.knpmobile.MainActivity.Companion.codeList
import com.micromos.knpmobile.MainActivity.Companion.codeNmList
import com.micromos.knpmobile.ViewModelBase
import com.micromos.knpmobile.dto.GetCardInfo
import com.micromos.knpmobile.dto.GetLabelNo
import com.micromos.knpmobile.network.KNPApi
import com.micromos.knpmobile.ui.login.LoginViewModel.Companion.user_id
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductStockCheckViewModel : ViewModelBase() {

    private val api = KNPApi.create()

    private val _noNetworkConnect = MutableLiveData<Event<Unit>>()
    val noNetWorkConnect: LiveData<Event<Unit>> = _noNetworkConnect

    private val _noLabelNo = MutableLiveData<Event<Unit>>()
    val noLabelNo: LiveData<Event<Unit>> = _noLabelNo

    private val _noPosCdMatch = MutableLiveData<Event<Unit>>()
    val noPosCdMatch: LiveData<Event<Unit>> = _noPosCdMatch

    private val _noPosCdNo = MutableLiveData<Event<Unit>>()
    val noPosCdNo: LiveData<Event<Unit>> = _noPosCdNo

    private val _selectDateEvent = MutableLiveData<Event<Unit>>()
    val selectDateEvent: LiveData<Event<Unit>> = _selectDateEvent

    private val _showDatePickerDialogEvent = MutableLiveData<Event<Unit>>()
    val showDatePickerDialogEvent: LiveData<Event<Unit>> = _showDatePickerDialogEvent

    val labelNo = MutableLiveData<String?>()
    val inDate = MutableLiveData<String>()
    val posCd = MutableLiveData<String?>()

    private lateinit var stockDate: String
    private lateinit var posCdTrim: String
    private lateinit var labelNoTrim: String
    private lateinit var codeCd: String

    val cardItemListDataUpdate = MutableLiveData<GetCardInfo>()
    val cardItemListDataInsert = MutableLiveData<GetCardInfo>()

    init {
        getServerTime()
    }

    private fun getServerTime() {
        api.getDateTime().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == 200) {
                    val date = response.body()!!.string().trim().substring(0, 8)
                    val year = date.substring(0, 4)
                    val month = date.substring(4, 6)
                    val day = date.substring(6, 8)
                    inDate.value = "$year / $month / $day"
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("testFailedGetDate", t.message.toString())
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
        codeList.forEach {
            if (it.value == posCdTrim) {
                codeCd = it.key
            }
        }

        Log.d("test", "$posCdTrim,$labelNoTrim")

        if (posCdTrim != "null") {
            if (posCdTrim in codeNmList) {
                if (labelNoTrim != "null") {
                    _isLoading.value = true
                    api.getLabelNo(stockDate, labelNoTrim)
                        .enqueue(object : Callback<GetLabelNo> {
                            override fun onResponse(
                                call: Call<GetLabelNo>,
                                response: Response<GetLabelNo>
                            ) {
                                Log.d("testlabelNo", response.body().toString())
                                if (response.code() == 200) {
                                    updateCoilStock()
                                } else if (response.code() == 204) {
                                    insertCoilStock()
                                }
                                successCall()
                            }

                            override fun onFailure(call: Call<GetLabelNo>, t: Throwable) {
                                Log.d("testFailedGetLabelNo", t.message.toString())
                                noNetWork()
                            }
                        }
                        )
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
        api.updateCoilStock(codeCd, labelNoTrim, stockDate)
            .enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (response.code() == 200) {
                        api.getCardInfo(labelNoTrim, stockDate)
                            .enqueue(object : Callback<GetCardInfo> {
                                override fun onResponse(
                                    call: Call<GetCardInfo>,
                                    response: Response<GetCardInfo>
                                ) {
                                    if (response.code() == 200) {
                                        cardItemListDataUpdate.value = response.body()

                                        Log.d("testUpdate", "${cardItemListDataUpdate.value}")
                                    }
                                    Log.d("testUpdate", "${response.body()}")
                                }

                                override fun onFailure(call: Call<GetCardInfo>, t: Throwable) {
                                    Log.d("testFailedUpdateStock", t.message.toString())
                                    noNetWork()
                                }
                            })

                    }
                    successCall()
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    Log.d("testFailedUpdateStock", t.message.toString())
                    noNetWork()
                }
            }
            )

    }

    private fun insertCoilStock() {
        _isLoading.value = true
        api.insertCoilStock(
            inDate = stockDate,
            stockNo = stockDate,
            userId = user_id ?: "empty",
            labelNo = labelNoTrim,
            posCd = codeCd
        ).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                Log.d("testInsert", "$stockDate,$labelNoTrim, $codeCd")
                if (response.code() == 200) {
                    api.getCardInfo(labelNoTrim, stockDate).enqueue(object : Callback<GetCardInfo> {
                        override fun onResponse(
                            call: Call<GetCardInfo>,
                            response: Response<GetCardInfo>
                        ) {
                            cardItemListDataInsert.value = response.body()
                            Log.d("testInsert", "${cardItemListDataInsert.value}")
                        }

                        override fun onFailure(call: Call<GetCardInfo>, t: Throwable) {
                            Log.d("testFailedInsertStock", t.message.toString())
                            noNetWork()
                        }
                    })
                }
                successCall()
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Log.d("testFailedInsertStock", t.message.toString())
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
            "신규"
        } else {
            "OK"
        }
    }

    fun noNetWork() {
        _noNetworkConnect.value = Event(Unit)
        successCall()
    }

}


