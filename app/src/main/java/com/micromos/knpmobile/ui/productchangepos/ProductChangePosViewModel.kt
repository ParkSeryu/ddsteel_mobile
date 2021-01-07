package com.micromos.knpmobile.ui.productchangepos

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.micromos.knpmobile.Event
import com.micromos.knpmobile.MainActivity.Companion.codeList
import com.micromos.knpmobile.MainActivity.Companion.codeNmList
import com.micromos.knpmobile.ViewModelBase
import com.micromos.knpmobile.dto.GetPosCd
import com.micromos.knpmobile.network.KNPApi
import com.micromos.knpmobile.ui.login.LoginViewModel.Companion.user_id
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductChangePosViewModel : ViewModelBase() {

    private val api = KNPApi.create()

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

    val atText = MutableLiveData<String?>()
    val _labelNo = MutableLiveData<String?>()
    var labelNo: String? = ""
    val _coilNo = MutableLiveData<String>()
    val _coilSeq = MutableLiveData<String>()
    val _stkType = MutableLiveData<String>()
    val _pos_cd = MutableLiveData<String>()

    fun retrievePos(_labelNo: String?) {
        labelNo = _labelNo?.trim()
        if (labelNo != null) {
            _isLoading.value = true
            api.getPosCd(labelNo!!).enqueue(object : Callback<GetPosCd> {
                override fun onResponse(call: Call<GetPosCd>, response: Response<GetPosCd>) {
                    if (response.code() == 200) {
                        _coilNo.value = response.body()?.coilNo
                        _coilSeq.value = response.body()?.coilSeq
                        _stkType.value = response.body()?.stkType
                        _pos_cd.value = response.body()?.posCd ?: ""
                        if (_pos_cd.value != "") {
                            _pos_cd.value = codeList[_pos_cd.value]
                        }
                        _focusChangeEvent.value = Event(Unit)
                    } else if (response.code() == 204) {
                        _pos_cd.value = ""
                        _noMatchLabel.value = Event(Unit)
                    }
                    successCall()
                }

                override fun onFailure(call: Call<GetPosCd>, t: Throwable) {
                    Log.d("testPosRetrieve", t.message.toString())
                    noNetWork()
                }
            })
        }
    }

    fun save() {
        _isLoading.value = true
        var codeCd = String()
        Log.d("test", atText.value.toString())
        if (atText.value.toString() in codeNmList) {
            if (!(_coilNo.value.equals(null) && _coilSeq.value.equals(null) && _stkType.equals(null))) {
                codeList.forEach{
                    if(it.value == atText.value){
                        codeCd = it.key
                    }
                }
                api.changePos(
                    codeCd,
                    user_id.toString(),
                    _coilNo.value.toString(),
                    _coilSeq.value.toString(),
                    _stkType.value.toString()
                ).enqueue(
                    object : Callback<Unit> {
                        override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                            Log.d("testPosSave", response.body().toString())
                            if(response.code() == 200){
                                retrievePos(labelNo)
                            }
                        }

                        override fun onFailure(call: Call<Unit>, t: Throwable) {
                            Log.d("testFailedPosSave", t.message.toString())
                            noNetWork()
                        }
                    }
                )
            } else {
                _updateImpossible.value = Event(Unit)
            }
        } else {
            _noMatchPos.value = Event(Unit)

        }
        successCall()
    }


    fun noNetWork() {
        _noNetworkConnect.value = Event(Unit)
        successCall()
    }
}


