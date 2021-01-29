package com.micromos.knpmobile.ui.productchangepos

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.micromos.knpmobile.Event
import com.micromos.knpmobile.MainActivity.Companion.codeList
import com.micromos.knpmobile.MainActivity.Companion.codeNmList
import com.micromos.knpmobile.ViewModelBase
import com.micromos.knpmobile.network.ApiResult
import com.micromos.knpmobile.repository.ChangePosRepositoryImpl
import com.micromos.knpmobile.ui.login.LoginViewModel.Companion.user_id

class ProductChangePosViewModel : ViewModelBase() {

    private val _noNetworkConnect = MutableLiveData<Event<Unit>>()
    val noNetWorkConnect: LiveData<Event<Unit>> = _noNetworkConnect

    private val _noMatchLabel = MutableLiveData<Event<Unit>>()
    val noMatchLabel: LiveData<Event<Unit>> = _noMatchLabel

    private val _noMatchPos = MutableLiveData<Event<Unit>>()
    val noMatchPos: LiveData<Event<Unit>> = _noMatchPos

    private val _updateImpossible = MutableLiveData<Event<Unit>>()
    val updateImpossible: LiveData<Event<Unit>> = _updateImpossible

    private val _focusChangeEvent = MutableLiveData<Event<Unit>>()
    val focusChangeEvent: LiveData<Event<Unit>> = _focusChangeEvent

    private val _changePosEvent = MutableLiveData<Event<Unit>>()
    val changePosEvent : LiveData<Event<Unit>> = _changePosEvent

    val atText = MutableLiveData<String?>()
    val _labelNo = MutableLiveData<String?>()
    var labelNo: String? = ""
    val coilNo = MutableLiveData<String>()
    val coilSeq = MutableLiveData<String>()
    val stkType = MutableLiveData<String>()
    val posCd = MutableLiveData<String>()
    var notificationCurrentPosCd : String = ""
    var notificationChangePosCd : String = ""
    val notificationChangePosTextViewVisibility = MutableLiveData<Int>()
    private val repository = ChangePosRepositoryImpl()

    init {
        notificationChangePosTextViewVisibility.value = View.INVISIBLE
    }

    fun retrievePos(_labelNo: String?) {
        labelNo = _labelNo?.trim()
        notificationChangePosTextViewVisibility.value = View.INVISIBLE
        if (labelNo != null) {
            _isLoading.value = true
            repository.sendRequestPosCd(labelNo!!, object : ApiResult {
                override fun onResult() {
                    val data = repository.getPosCd()
                    coilNo.value = data.value?.coilNo
                    coilSeq.value = data.value?.coilSeq
                    stkType.value = data.value?.stkType
                    posCd.value = data.value?.posCd ?: ""
                    if (posCd.value != "") {
                        posCd.value = codeList[posCd.value]
                        notificationCurrentPosCd = posCd.value!!
                    }
                    _focusChangeEvent.value = Event(Unit)
                    _isLoading.value = false
                }

                override fun nullBody() {
                    posCd.value = ""
                    coilNo.value = null
                    coilSeq.value = null
                    stkType.value = null
                    _noMatchLabel.value = Event(Unit)
                    _isLoading.value = false
                }

                override fun onFailure() {
                    noNetWork()
                }
            })
        }
    }

    fun save() {
        var codeCd = String()
        if (atText.value.toString() in codeNmList) {
            if (coilNo.value != null && coilSeq.value != null && stkType.value != null) {
                _isLoading.value = true
                codeList.forEach {
                    if (it.value == atText.value) {
                        codeCd = it.key
                    }
                }
                repository.changePosCd(
                    codeCd,
                    user_id.toString(),
                    coilNo.value.toString(),
                    coilSeq.value.toString(),
                    stkType.value.toString(),
                    object : ApiResult {
                        override fun onResult() {
                            notificationChangePosCd = atText.value!!
                            _changePosEvent.value = Event(Unit)
                            notificationChangePosTextViewVisibility.value = View.VISIBLE
                            posCd.value = ""
                            coilNo.value = null
                            coilSeq.value = null
                            stkType.value = null
                            successCall()
                        }

                        override fun nullBody() {
                            TODO("Not yet implemented")
                        }

                        override fun onFailure() {
                            noNetWork()
                        }
                    })
            } else {
                _updateImpossible.value = Event(Unit)
            }
        } else {
            _noMatchPos.value = Event(Unit)
        }
    }

    fun noNetWork() {
        _noNetworkConnect.value = Event(Unit)
        successCall()
    }
}


