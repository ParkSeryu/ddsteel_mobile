package com.micromos.knpmobile.ui.login

import android.content.pm.PackageInfo
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.micromos.knpmobile.*
import com.micromos.knpmobile.dto.User
import com.micromos.knpmobile.network.KNPApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.coroutineContext

class LoginViewModel : ViewModelBase() {
    val TAG = "testLogin"
    val loginSuccessEvent = SingleLiveEvent<Any>()
    private val _loginFailedEvent = MutableLiveData<Event<Unit>>()
    val loginFailedEvent: LiveData<Event<Unit>> = _loginFailedEvent

    private val _loginDeniedEvent = MutableLiveData<Event<Unit>>()
    val loginDeniedEvent: LiveData<Event<Unit>> = _loginDeniedEvent

    private val _NotConnectedServer = MutableLiveData<Event<Unit>>()
    val NotConnectedServer: LiveData<Event<Unit>> = _NotConnectedServer

    private val api = KNPApi.create()


    val _id = MutableLiveData<String>()
    val _password = MutableLiveData<String>()


    companion object {
        var user_id: String? = null
        var name: String? = null
    }

    init {
        _id.value = ""
        _password.value = ""
    }

    fun onClickLogin() {
        val id = _id.value ?: ""
        val pw = _password.value ?: ""
        if (BuildConfig.DEBUG) {
            loginSuccessEvent.call()
        } else {
            api.login(id, pw).enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.code() == 200) {
                        if (response.body()?.retire?.equals("2")!!) {
                            _loginDeniedEvent.value = Event(Unit)
                        } else {
                            user_id = response.body()?.id
                            name = response.body()?.name
                            loginSuccessEvent.call()
                        }
                    } else if (response.code() == 204) {
                        _loginFailedEvent.value = Event(Unit)
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.d(TAG, t.message.toString())
                    _NotConnectedServer.value = Event(Unit)
                }
            })
        }
    }

    fun btnEnabled(id: String, pw: String): Boolean {
        return if (BuildConfig.DEBUG) true else id.isNotBlank() && pw.isNotBlank()
        // return id.isNotBlank() && pw.isNotBlank()
    }


}