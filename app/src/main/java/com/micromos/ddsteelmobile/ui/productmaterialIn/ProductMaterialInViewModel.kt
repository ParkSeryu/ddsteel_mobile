package com.micromos.ddsteelmobile.ui.productmaterialIn

import android.os.Handler
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.micromos.ddsteelmobile.Event
import com.micromos.ddsteelmobile.ViewModelBase
import com.micromos.ddsteelmobile.dto.GetMaterialCardInfo
import com.micromos.ddsteelmobile.network.ApiResult
import com.micromos.ddsteelmobile.repository.MaterialInRepositoryImpl
import com.micromos.ddsteelmobile.ui.login.LoginViewModel.Companion.user_id
import com.micromos.ddsteelmobile.ui.login.LoginViewModel.Companion.work_place_cd

class ProductMaterialInViewModel : ViewModelBase() {

    private val _noNetworkConnect = MutableLiveData<Event<Unit>>()
    val noNetWorkConnect: LiveData<Event<Unit>> = _noNetworkConnect

    private val _requestInNoFailedEvent = MutableLiveData<Event<Unit>>()
    val requestInNoFailedEvent: LiveData<Event<Unit>> = _requestInNoFailedEvent

    private val _updateLabelInFailedEvent = MutableLiveData<Event<Unit>>()
    val updateLabelInFailedEvent: LiveData<Event<Unit>> = _updateLabelInFailedEvent

    private val _getCardInfoFailedEvent = MutableLiveData<Event<Unit>>()
    val getCardInfoFailedEvent: LiveData<Event<Unit>> = _getCardInfoFailedEvent

    private val _noMatchLabel = MutableLiveData<Event<Unit>>()
    val noMatchLabel: LiveData<Event<Unit>> = _noMatchLabel

    private val _duplicateLabel = MutableLiveData<Event<Unit>>()
    val duplicateLabel: LiveData<Event<Unit>> = _duplicateLabel

    private val _alreadyFinishEvent = MutableLiveData<Event<Unit>>()
    val alreadyFinishEvent: LiveData<Event<Unit>> = _alreadyFinishEvent

    private val _commonErrorEvent = MutableLiveData<Event<Unit>>()
    val commonErrorEvent: LiveData<Event<Unit>> = _commonErrorEvent

    var errorCode: Int? = null

    private val _notLabelNoInput = MutableLiveData<Event<Unit>>()
    val notLabelNoInput: LiveData<Event<Unit>> = _notLabelNoInput

    private val _notTransNoInput = MutableLiveData<Event<Unit>>()
    val notTransNoInput: LiveData<Event<Unit>> = _notTransNoInput

    private val _notTransCarNoInput = MutableLiveData<Event<Unit>>()
    val notTransCarNoInput: LiveData<Event<Unit>> = _notTransCarNoInput

//    private val _notTransManInput = MutableLiveData<Event<Unit>>()
//    val notTransManInput: LiveData<Event<Unit>> = _notTransManInput
//
//    private val _notTransManPhoneInput = MutableLiveData<Event<Unit>>()
//    val notTransManPhoneInput: LiveData<Event<Unit>> = _notTransManPhoneInput

    private val _focusChangeEvent = MutableLiveData<Event<Unit>>()
    val focusChangeEvent: LiveData<Event<Unit>> = _focusChangeEvent

    val _labelNo = MutableLiveData<String?>()
    var labelNo: String? = null
    var endFlag: Boolean = true
    lateinit var  transNo: String
    val transCarNo = MutableLiveData<String>()
    val transMan = MutableLiveData<String?>()
    val transManPhone = MutableLiveData<String?>()

    val transEnabled = MutableLiveData<Boolean>()
    val labelEnabled = MutableLiveData<Boolean>()
    val btnNewEnabled = MutableLiveData<Boolean>()
    val btnLabelInEnabled = MutableLiveData<Boolean>()

    var cardInfo = MutableLiveData<GetMaterialCardInfo>()


    private val repository = MaterialInRepositoryImpl()

    init {
        btnNewEnabled.value = true
        transEnabled.value = false
        labelEnabled.value = false
        btnLabelInEnabled.value = false
        onClickBtnNew()
    }


    fun btnLabelIn(_labelNo: String?) {
        labelNo = _labelNo?.trim()

        if (!(labelNo.isNullOrEmpty() || transNo.isEmpty() || transCarNo.value.isNullOrEmpty())) {
            _isLoading.value = true
            repository.labelIn(
                user_id!!,
                work_place_cd!!,
                transNo,
                transCarNo.value!!,
                transMan.value,
                transManPhone.value,
                labelNo!!,
                object : ApiResult {
                    override fun onResult() {
                        val data = repository.getLabelUpdateResult()
                        Log.d("returnResultTest", "{${data.value!!.returnResult}}")
                        if (data.value!!.returnResult == "0") {
                            repository.sendRequestMaterialCardInfo(
                                labelNo!!,
                                transNo,
                                object : ApiResult {
                                    override fun onResult() {
                                        cardInfo.value = repository.getMaterialCardInfo()
                                        endFlag = true
                                        Log.d("cardInfoTest", cardInfo.value.toString())
                                        successCall()
                                    }

                                    override fun nullBody() {
                                        _getCardInfoFailedEvent.value = Event(Unit)
                                        _isLoading.value = false
                                    }

                                    override fun onFailure() {
                                        noNetWork()
                                    }
                                })
                        } else {
                            when (data.value!!.returnResult) {
                                "1" -> {
                                    _noMatchLabel.value = Event(Unit)
                                }
                                "2" -> {
                                    _duplicateLabel.value = Event(Unit)
                                }
                                "3" -> {
                                    _commonErrorEvent.value = Event(Unit)
                                    errorCode = 3
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
                                "8" -> {
                                    _commonErrorEvent.value = Event(Unit)
                                    errorCode = 8
                                }
                                "9" -> {
                                    _commonErrorEvent.value = Event(Unit)
                                    errorCode = 9
                                }
                                "10" -> {
                                    _alreadyFinishEvent.value = Event(Unit)

                                }
                            }
                            successCall()
                        }
                        transEnabled.value = false
                        btnNewEnabled.value = true
                    }

                    override fun nullBody() {
                        _updateLabelInFailedEvent.value = Event(Unit)
                        _isLoading.value = false
                    }

                    override fun onFailure() {
                        noNetWork()
                    }
                }
            )
        } else {
            when {
                labelNo.isNullOrEmpty() -> {
                    _notLabelNoInput.value = Event(Unit)
                }
                transNo.isEmpty() -> {
                    _notTransNoInput.value = Event(Unit)
                }
                transCarNo.value.isNullOrEmpty() -> {
                    _notTransCarNoInput.value = Event(Unit)
                }
//                transMan.value.isNullOrEmpty() -> {
//                    _notTransManInput.value = Event(Unit)
//                }
//                transManPhone.value.isNullOrEmpty() -> {
//                    _notTransManPhoneInput.value = Event(Unit)
//                }
            }

        }
    }

    fun onClickBtnNew() {
        _isLoading.value = true
        btnNewEnabled.value = false

        repository.sendRequestKeySequenceNo(object : ApiResult {
            override fun onResult() {
                val data = repository.getKeySequenceNo()
                transNo = "${data.value!!.keyKind}${data.value!!.keyData}${data.value!!.sequenceNo}"
                Log.d("transNoTest", transNo)
                transEnabled.value = true
                labelEnabled.value = true
                btnLabelInEnabled.value = true
                transCarNo.value = ""
                transMan.value = ""
                _labelNo.value = ""
                endFlag = false
                Handler().postDelayed(Runnable {
                    //딜레이 후 시작할 코드 작성
                    _focusChangeEvent.value = Event(Unit)
                }, 100) // 0.1초 정도 딜레이를 준 후 시작
                transManPhone.value = ""
                cardInfo.value = null
                successCall()
            }

            override fun nullBody() {
                _requestInNoFailedEvent.value = Event(Unit)
                _isLoading.value = false
            }

            override fun onFailure() {
                noNetWork()
            }
        })


    }

    fun noNetWork() {
        _noNetworkConnect.value = Event(Unit)
        successCall()
    }

    fun screenOrientation() {
        onCleared()
    }

}
