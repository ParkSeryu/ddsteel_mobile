package com.micromos.knpmobile

import android.view.View
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

    fun loadingVisibilityConverter(loading: Boolean): Int {
        return if(loading) View.VISIBLE else View.INVISIBLE
    }

    fun successCall(){
        _isLoading.value = false
    }
}