package com.micromos.knpmobile.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.micromos.knpmobile.Event

class HomeViewModel : ViewModel() {
    private val _menuItemList = MutableLiveData<List<MenuItem>>()
    val menuItemList: LiveData<List<MenuItem>> = _menuItemList

    private val _goToAnotherView = MutableLiveData<Event<MenuItem>>()
    val goToAnotherView: LiveData<Event<MenuItem>> = _goToAnotherView

    init {
        _menuItemList.value = listOf(
            MenuItem.ProductCoilIn,
            MenuItem.ProductCoilOut,
            MenuItem.ProductStockCheck,
            MenuItem.ProductChangePos
        )
    }

    fun goToAnotherView(menuItemList: MenuItem) {
        _goToAnotherView.value = Event(menuItemList)
    }

}