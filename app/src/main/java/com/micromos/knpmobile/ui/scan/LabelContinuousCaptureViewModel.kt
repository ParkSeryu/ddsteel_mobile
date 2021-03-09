package com.micromos.knpmobile.ui.scan

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.zxing.ResultPoint
import com.google.zxing.client.android.BeepManager
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.micromos.knpmobile.Event
import com.micromos.knpmobile.R
import com.micromos.knpmobile.ViewModelBase


class LabelContinuousCaptureViewModel : ViewModelBase() {
    private val _noRetrieve = MutableLiveData<String>()
    val noRetrieve: LiveData<String> = _noRetrieve

    private val _unExceptedError = MutableLiveData<Event<Unit>>()
    val unExceptedError : LiveData<Event<Unit>> = _unExceptedError


     val test = MutableLiveData<Bitmap>()
    private lateinit var beepManager : BeepManager


    val callback: BarcodeCallback = object : BarcodeCallback {
        override fun barcodeResult(result: BarcodeResult) {
            Log.d("test","test12345")
            if (result.text == null || result.text == "123") {
                // Prevent duplicate scans
                return
            }

            _noRetrieve.value = result.text
            test.value = result.getBitmapWithResultPoints(Color.YELLOW)

//            beepManager.playBeepSoundAndVibrate()

            //Added preview of scanned barcode

        }

        override fun possibleResultPoints(resultPoints: List<ResultPoint>) {        Log.d("test","test2")
        }

    }
}