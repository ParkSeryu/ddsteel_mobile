package com.micromos.knpmobile.ui.productMaterialCheck

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.micromos.knpmobile.Event
import com.micromos.knpmobile.ViewModelBase
import com.micromos.knpmobile.network.ApiResult
import com.micromos.knpmobile.repository.MaterialCheckRepositoryImpl

class ProductMaterialCheckViewModel : ViewModelBase() {

    private val _noNetworkConnect = MutableLiveData<Event<Unit>>()
    val noNetWorkConnect: LiveData<Event<Unit>> = _noNetworkConnect

    private val _noMatchLabel = MutableLiveData<Event<Unit>>()
    val noMatchLabel: LiveData<Event<Unit>> = _noMatchLabel

    private val _matchedLabelEvent = MutableLiveData<Event<Unit>>()
    val matchedLabelEvent: LiveData<Event<Unit>> = _matchedLabelEvent

    private val _unMatchedLabelEvent = MutableLiveData<Event<Unit>>()
    val unMatchedLabelInfo: LiveData<Event<Unit>> = _unMatchedLabelEvent

    private val _focusChangeEvent = MutableLiveData<Event<Unit>>()
    val focusChangeEvent: LiveData<Event<Unit>> = _focusChangeEvent

    val labelNoHyundai = MutableLiveData<String?>()
    val labelNoKnp = MutableLiveData<String?>()
    var labelNo: String? = ""

    val productNoHyundai = MutableLiveData<String>()
    val sizeNoHyundai = MutableLiveData<String>()
    val weightHyundai = MutableLiveData<Int>()

    val productNoKnp = MutableLiveData<String>()
    val sizeNoKnp = MutableLiveData<String>()
    val weightKnp = MutableLiveData<Int>()

    val hyundaiLabelVisibility = MutableLiveData<Int>()
    val knpLabelVisibility = MutableLiveData<Int>()
    val materialCheckVisibility = MutableLiveData<Int>()


    private val repository = MaterialCheckRepositoryImpl()

    init {
        materialCheckVisibility.value = View.INVISIBLE
        knpLabelVisibility.value = View.INVISIBLE
        hyundaiLabelVisibility.value = View.INVISIBLE
    }

    fun hyundaiLabelRetrieve(_labelNo: String?) {
        labelNo = _labelNo?.trim()
        materialCheckVisibility.value = View.INVISIBLE
        if (labelNo != null && labelNo!!.isNotEmpty()) {
            _isLoading.value = true
            labelNo = labelNo!!.substring(labelNo!!.lastIndexOf("=") + 1)
            val replaceWord = """[/]""".toRegex()
            repository.requestHyundaiLabelInfo(labelNo!!, object : ApiResult {
                override fun onResult() {
                    val data = repository.getHyundaiLabelInfo()
                    productNoHyundai.value = data.value?.get(0)?.goodsNo
                    sizeNoHyundai.value = (data.value?.get(0)?.spec)!!.replace(replaceWord, "x")
                    weightHyundai.value = data.value?.get(0)?.wgt
                    //_focusChangeEvent.value = Event(Unit)
                    hyundaiLabelVisibility.value = View.VISIBLE
                    successCall()
                    if(knpLabelVisibility.value == View.VISIBLE)
                    matchLabelInfo()
                }

                override fun nullBody() {
                    hyundaiLabelVisibility.value = View.INVISIBLE
                    productNoHyundai.value = null
                    sizeNoHyundai.value = null
                    weightHyundai.value = null
                    _noMatchLabel.value = Event(Unit)
                    _isLoading.value = false
                }

                override fun onFailure() {
                    noNetWork()
                }
            })
        }
    }


    fun knpLabelRetrieve(_labelNo: String?) {
        labelNo = _labelNo?.trim()
        materialCheckVisibility.value = View.INVISIBLE
        if (labelNo != null && labelNo!!.isNotEmpty()) {
            _isLoading.value = true
            repository.requestKnpLabelInfo(labelNo!!, object : ApiResult {
                override fun onResult() {
                    val data = repository.getKnpLabelInfo()
                    productNoKnp.value = data.value?.millNo
                    sizeNoKnp.value = data.value?.sizeNo
                    weightKnp.value = data.value?.weight
                    //_focusChangeEvent.value = Event(Unit)
                    knpLabelVisibility.value = View.VISIBLE
                    successCall()
                    if(hyundaiLabelVisibility.value == View.VISIBLE)
                    matchLabelInfo()
                }

                override fun nullBody() {
                    knpLabelVisibility.value = View.INVISIBLE
                    productNoKnp.value = null
                    sizeNoKnp.value = null
                    weightKnp.value = null
                    _noMatchLabel.value = Event(Unit)
                    _isLoading.value = false
                }

                override fun onFailure() {
                    noNetWork()
                }
            })
        }
    }

    fun matchLabelInfo(){
        if(hyundaiLabelVisibility.value == View.VISIBLE && knpLabelVisibility.value == View.VISIBLE){
            if(productNoHyundai.value.equals(productNoHyundai.value) && weightHyundai.value == weightKnp.value) {
                _matchedLabelEvent.value = Event(Unit)
            }else{
                _unMatchedLabelEvent.value = Event(Unit)
            }
            materialCheckVisibility.value = View.VISIBLE
        }
    }

    fun noNetWork() {
        _noNetworkConnect.value = Event(Unit)
        successCall()
    }

    fun screenOrientation() {
        onCleared()
    }

}
