package com.micromos.knpmobile.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _menuItemList = MutableLiveData<List<MenuItem>>()
    val menuItemList: LiveData<List<MenuItem>> = _menuItemList

    init {
        _menuItemList.value = listOf(
            MenuItem.ProductCoilIn,
            MenuItem.ProductCoilOut,
            MenuItem.ProductStockCheck,
            MenuItem.ProductChangePos
        )
    }

}