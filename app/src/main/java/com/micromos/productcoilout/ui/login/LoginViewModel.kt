package com.micromos.productcoilout.ui.login

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.micromos.productcoilout.BuildConfig
import com.micromos.productcoilout.SingleLiveEvent
import com.micromos.productcoilout.ViewModelBase
import com.micromos.productcoilout.dto.User
import com.micromos.productcoilout.network.KNPApi
import com.micromos.productcoilout.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel : ViewModelBase() {
    val TAG = "testLogin"
    val loginSuccessEvent = SingleLiveEvent<Any>()
    private val _loginFailedEvent = MutableLiveData<Event<Unit>>()
    val loginFailedEvent: LiveData<Event<Unit>> = _loginFailedEvent

    private val _loginDeniedEvent = MutableLiveData<Event<Unit>>()
    val loginDeniedEvent: LiveData<Event<Unit>> = _loginDeniedEvent

    private val api = KNPApi.create()
    private val _connectNetwork = MutableLiveData<Int>()
    val connectNetwork: LiveData<Int> = _connectNetwork

    val _id = MutableLiveData<String>()
    val _password = MutableLiveData<String>()

    init {
        _connectNetwork.value = View.GONE
        api.testServer().enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.code() == 200) {
                    Log.d("check", response.body().toString())
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Log.d(TAG, t.message.toString())
                _connectNetwork.value = View.VISIBLE
            }
        })
        _id.value = ""
        _password.value = ""
    }

    fun onClickLogin() {
        val id = _id.value ?: ""
        val pw = _password.value ?: ""
        if(BuildConfig.DEBUG){
            loginSuccessEvent.call()
        }else{
            api.login(id, pw).enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.code() == 200) {
                        if(response.body()?.retire?.equals("2")!!)
                        {
                            _loginDeniedEvent.value = Event(Unit)
                        }
                        else
                            loginSuccessEvent.call()
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.d(TAG, t.message.toString())
                    _loginFailedEvent.value = Event(Unit)
                }
            })
        }
    }

    fun btnEnabled(id: String, pw: String): Boolean {
        return if(BuildConfig.DEBUG) true else id.isNotBlank() && pw.isNotBlank()
    }


}