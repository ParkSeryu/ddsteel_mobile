/*
package com.micromos.knpmobile.ui.scan

import android.os.Bundle
import android.view.KeyEvent
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.zxing.BarcodeFormat
import com.google.zxing.client.android.BeepManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import com.micromos.knpmobile.
import com.micromos.knpmobile.databinding.ActivityContinousScanBinding
import kotlinx.android.synthetic.main.activity_continous_scan.*

class LabelContinuousCaptureActivity : AppCompatActivity() {
    private lateinit var loginViewModel: LabelContinuousCaptureViewModel
    private lateinit var LoginViewDataBinding: ActivityContinousScanBinding

    private var barcodeView: DecoratedBarcodeView? = null
    private var beepManager: BeepManager? = null
    var lastText: String? = null
    */
/*val callback: BarcodeCallback = object : BarcodeCallback {
        override fun barcodeResult(result: BarcodeResult) {
            Log.d("test", "test12345")
            if (result.text == null || result.text == "123") {
                // Prevent duplicate scans
                return
            }


            beepManager?.playBeepSoundAndVibrate()

            //Added preview of scanned barcode

        }

        override fun possibleResultPoints(resultPoints: List<ResultPoint>) {
            Log.d("test", "test2")
        }
    }*//*


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LoginViewDataBinding =
            DataBindingUtil.setContentView(this, R.layout.fragment_continous_scan)
        loginViewModel =
            ViewModelProvider(this).get(com.micromos.knpmobile.ui.scan.LabelContinuousCaptureViewModel::class.java)
        LoginViewDataBinding.viewModel = loginViewModel
        LoginViewDataBinding.lifecycleOwner = this

        val formats = listOf(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39)
        barcode_scanner.barcodeView.decoderFactory = DefaultDecoderFactory(formats)
        barcode_scanner.decodeContinuous(loginViewModel.callback)
        val imageView = findViewById<ImageView>(R.id.barcodePreview)
        imageView.setImageBitmap(loginViewModel.test.value)

        loginViewModel.noRetrieve.observe(this, androidx.lifecycle.Observer {

        })
        beepManager = BeepManager(this)
    }
    */
/*  override fun onCreate(savedInstanceState: Bundle?) {
          super.onCreate(savedInstanceState)
         *//*
*/
/*

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
    }*//*



    override fun onResume() {
        super.onResume()
        barcode_scanner.resume()
    }

    override fun onPause() {
        super.onPause()
        barcode_scanner.pause()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return super.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event)
    }
}*/
