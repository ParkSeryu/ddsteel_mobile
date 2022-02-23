package com.micromos.ddsteelmobile.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.micromos.ddsteelmobile.*
import com.micromos.ddsteelmobile.dto.User
import com.micromos.ddsteelmobile.network.DDsteelApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel : ViewModelBase() {
    val TAG = "loginTest"
    val loginSuccessEvent = SingleLiveEvent<Any>()
    private val _loginFailedEvent = MutableLiveData<Event<Unit>>()
    val loginFailedEvent: LiveData<Event<Unit>> = _loginFailedEvent

    private val _loginDeniedEvent = MutableLiveData<Event<Unit>>()
    val loginDeniedEvent: LiveData<Event<Unit>> = _loginDeniedEvent

    private val _NotConnectedServer = MutableLiveData<Event<Unit>>()
    val NotConnectedServer: LiveData<Event<Unit>> = _NotConnectedServer

    private val api = DDsteelApi.create()

    val _id = MutableLiveData<String>()
    val _password = MutableLiveData<String>()


    companion object {
        var user_id: String? = null
        var name: String? = null
        var work_place_cd: String? = null
        var work_place_nm: String? = null
        val program_id = mutableListOf<String?>()
    }

    init {
        _id.value = ""
        _password.value = ""
    }

    fun onClickLogin() {
        _isLoading.value = true
        val id = _id.value ?: ""
        val pw = _password.value ?: ""
        program_id.clear()
// Debug 테스트용 주석
//        api.login(id, pw).enqueue(object : Callback<User> {
//            override fun onResponse(call: Call<User>, response: Response<User>) {
//                if (response.code() == 200) {
//                    user_id = response.body()?.id
//                    name = response.body()?.name
//                    work_place_cd = response.body()?.workPlaceCd
//                    work_place_nm = response.body()?.workPlaceNm
//                    program_id.addAll(response.body()?.program!!.split(","))
//                    Log.d("programListTest", "$program_id")
//                    loginSuccessEvent.call()
//                } else if (response.code() == 203) {
//                    _loginFailedEvent.value = Event(Unit)
//                }
//            }
//
//            override fun onFailure(call: Call<User>, t: Throwable) {
//                Log.d(TAG, t.message.toString())
//                _NotConnectedServer.value = Event(Unit)
//            }
//        })

        if (BuildConfig.DEBUG) {
            user_id = "test"
            work_place_cd = "A9999"
            name = "홍길동"
            work_place_nm = "본사"
            successCall()
            loginSuccessEvent.call()
        } else {
            api.login(id, pw).enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.code() == 200) {
                        user_id = response.body()?.id
                        name = response.body()?.name
                        work_place_cd = response.body()?.workPlaceCd
                        work_place_nm = response.body()?.workPlaceNm
                        program_id.addAll(response.body()?.program!!.split(","))
                        Log.d("programListTest", "$program_id")
                        successCall()
                        loginSuccessEvent.call()
                    } else if (response.code() == 203) {
                        successCall()
                        _loginFailedEvent.value = Event(Unit)
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.d(TAG, t.message.toString())
                    successCall()
                    _NotConnectedServer.value = Event(Unit)
                }
            })
        }
    }

    fun btnEnabled(id: String, pw: String): Boolean {
        return if (BuildConfig.DEBUG) true else id.isNotBlank() && pw.isNotBlank()
    }


}