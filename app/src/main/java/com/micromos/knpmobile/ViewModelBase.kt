package com.micromos.knpmobile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class ViewModelBase : ViewModel() {
    @Suppress("PropertyName")
    protected val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    init {
        _isLoading.value = false
    }

    fun successCall(){
        _isLoading.value = false
    }
}