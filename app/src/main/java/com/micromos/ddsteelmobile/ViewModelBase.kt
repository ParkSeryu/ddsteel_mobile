package com.micromos.ddsteelmobile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class ViewModelBase : ViewModel() {
    @Suppress("PropertyName")
    protected val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _clearInputLayout = MutableLiveData<Event<Unit>>()
    val clearInputLayout : LiveData<Event<Unit>> = _clearInputLayout

    init {
        _isLoading.value = false
    }

    fun successCall(){
        _isLoading.value = false
        _clearInputLayout.value = Event(Unit)
    }
}