package com.micromos.ddsteelmobile.ui.productchangepos

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.micromos.ddsteelmobile.Event
import com.micromos.ddsteelmobile.MainActivity.Companion.posList
import com.micromos.ddsteelmobile.MainActivity.Companion.pos_nm_list
import com.micromos.ddsteelmobile.ViewModelBase
import com.micromos.ddsteelmobile.network.ApiResult
import com.micromos.ddsteelmobile.repository.ChangePosRepositoryImpl
import com.micromos.ddsteelmobile.ui.login.LoginViewModel.Companion.user_id

class ProductChangePosViewModel : ViewModelBase() {

    private val _noNetworkConnect = MutableLiveData<Event<Unit>>()
    val noNetWorkConnect: LiveData<Event<Unit>> = _noNetworkConnect

    private val _unExceptedError = MutableLiveData<Event<Unit>>()
    val unExceptedError: LiveData<Event<Unit>> = _unExceptedError

    private val _noMatchLabel = MutableLiveData<Event<Unit>>()
    val noMatchLabel: LiveData<Event<Unit>> = _noMatchLabel

    private val _noMatchPos = MutableLiveData<Event<Unit>>()
    val noMatchPos: LiveData<Event<Unit>> = _noMatchPos

    private val _updateImpossible = MutableLiveData<Event<Unit>>()
    val updateImpossible: LiveData<Event<Unit>> = _updateImpossible

    private val _focusChangeEvent = MutableLiveData<Event<Unit>>()
    val focusChangeEvent: LiveData<Event<Unit>> = _focusChangeEvent

    val autoCompleteTextView = MutableLiveData<String?>()
    val _labelNo = MutableLiveData<String?>()
    var labelNo: String? = ""

    val coilNo = MutableLiveData<String>()
    val coilSeq = MutableLiveData<String>()
    val posCd = MutableLiveData<String>()

    var notificationLabelNo = MutableLiveData<String>()
    var notificationCurrentPosCd = MutableLiveData<String>()
    var notificationChangePosCd = MutableLiveData<String>()
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
            repository.getLabelPosCd(labelNo!!, object : ApiResult {
                override fun onResult() {
                    val data = repository.getPosCd()
                    coilNo.value = data.value?.coilNo
                    coilSeq.value = data.value?.coilSeq
                    posCd.value = data.value?.posCd ?: ""
                    if (posCd.value != "") {
                        posCd.value = posList[posCd.value]
                        notificationCurrentPosCd.value = posCd.value!!
                    }else{
                        notificationCurrentPosCd.value = ""
                    }
                    notificationLabelNo.value = "${coilNo.value}-${coilSeq.value}"
                    _focusChangeEvent.value = Event(Unit)
                    _isLoading.value = false
                }

                override fun nullBody() {
                    posCd.value = ""
                    coilNo.value = null
                    coilSeq.value = null
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
        if (autoCompleteTextView.value.toString() in pos_nm_list) {
            if (coilNo.value != null && coilSeq.value != null) {
                _isLoading.value = true
                posList.forEach {
                    if (it.value == autoCompleteTextView.value) {
                        codeCd = it.key
                    }
                }
                repository.changePosCd(
                    codeCd,
                    user_id.toString(),
                    coilNo.value.toString(),
                    coilSeq.value.toString(),
                    object : ApiResult {
                        override fun onResult() {
                            notificationChangePosCd.value = autoCompleteTextView.value!!
                            notificationChangePosTextViewVisibility.value = View.VISIBLE
                            posCd.value = ""
                            coilNo.value = null
                            coilSeq.value = null
                            successCall()
                        }

                        override fun nullBody() {
                            unExpectedError()
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

    fun unExpectedError() {
        _unExceptedError.value = Event(Unit)
        successCall()
    }

    fun screenOrientation() {
        onCleared()
    }

//    fun scanBarCode() {
//        _onClickScanButton.value = Event(Unit)
//    }
}


