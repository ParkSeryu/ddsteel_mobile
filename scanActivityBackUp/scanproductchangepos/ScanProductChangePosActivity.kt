package com.micromos.ddsteelmobile.ui.scanproductchangepos

import android.content.res.Configuration
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.Selection
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.zxing.BarcodeFormat
import com.google.zxing.ResultPoint
import com.google.zxing.client.android.BeepManager
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import com.micromos.ddsteelmobile.CustomDialog
import com.micromos.ddsteelmobile.MainActivity
import com.micromos.ddsteelmobile.MainActivity.Companion.transToUpperCase
import com.micromos.ddsteelmobile.R
import com.micromos.ddsteelmobile.databinding.ActivityChangePosScanBinding
import kotlinx.android.synthetic.main.activity_change_pos_scan.*
import kotlinx.android.synthetic.main.activity_change_pos_scan.progress_bar

class ScanProductChangePosActivity : AppCompatActivity() {
    private lateinit var scanProductChangePosViewModel: ScanProductChangePosViewModel
    private lateinit var productChangeScanBinding: ActivityChangePosScanBinding
    private lateinit var beepManager: BeepManager
    private var lastText: String? = null

    private val formats: Collection<BarcodeFormat> = listOf(
        BarcodeFormat.QR_CODE,
        BarcodeFormat.CODE_39
    )

    private val callback: BarcodeCallback = object : BarcodeCallback {
        override fun barcodeResult(result: BarcodeResult) {
            if (result.text == null || result.text == lastText || result.barcodeFormat !in formats || progress_bar.visibility == View.VISIBLE) {
                // Prevent duplicate scans
                return
            }
            lastText = result.text
            barcode_scanner.setStatusText(result.text)

            scanProductChangePosViewModel._labelNo.value = result.text
            scanProductChangePosViewModel.retrievePos(scanProductChangePosViewModel._labelNo.value)

            val tone = ToneGenerator(AudioManager.STREAM_MUSIC, ToneGenerator.MAX_VOLUME)
            tone.startTone(ToneGenerator.TONE_PROP_BEEP2, 500)
        }

        override fun possibleResultPoints(resultPoints: List<ResultPoint>) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productChangeScanBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_change_pos_scan)
        scanProductChangePosViewModel =
            ViewModelProvider(this).get(ScanProductChangePosViewModel::class.java)
        productChangeScanBinding.viewModel = scanProductChangePosViewModel
        productChangeScanBinding.lifecycleOwner = this

        barcode_scanner.barcodeView.decoderFactory = DefaultDecoderFactory(formats)
        barcode_scanner.initializeFromIntent(intent)
        barcode_scanner.decodeContinuous(callback)
        beepManager = BeepManager(this)

        productChangeScanBinding.changeStockAutoTv.setOnClickListener {
            val view = productChangeScanBinding.changeStockAutoTv
            view.text = view.text
            val eText = view.text
            Selection.setSelection(eText, eText.length)
        }

        if (MainActivity.codeNmList.size == 0) {
            CustomDialog(this, R.layout.dialog_incorrect)
                .setTitle(R.string.prompt_error)
                .setMessage(R.string.failed_get_code)
                .setPositiveButton(R.string.dialog_ok) {
                }.show()
        }

        scanProductChangePosViewModel.noNetWorkConnect.observe(this, Observer {
            CustomDialog(this, R.layout.dialog_incorrect)
                .setTitle(R.string.prompt_error)
                .setMessage(R.string.network_failed)
                .setPositiveButton(R.string.dialog_ok) {
                }.show()
        })

        scanProductChangePosViewModel.unExceptedError.observe(this, Observer {
            CustomDialog(this, R.layout.dialog_incorrect)
                .setTitle(R.string.prompt_error)
                .setMessage(R.string.unexpected_error_pda_change_pos_scan)
                .setPositiveButton(R.string.dialog_ok) {
                }.show()
        })

        scanProductChangePosViewModel.isLoading.observe(this, Observer {
            if (it) {
                Log.d("visible", "${scanProductChangePosViewModel.isLoading.value}")
                this.window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                );
                progress_bar.visibility = View.VISIBLE
            } else {
                Log.d("visible", "${scanProductChangePosViewModel.isLoading.value}")
                this.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                progress_bar.visibility = View.INVISIBLE
            }
        })

        scanProductChangePosViewModel.noMatchLabel.observe(this, Observer {
            CustomDialog(this, R.layout.dialog_incorrect)
                .setTitle(R.string.prompt_notification)
                .setMessage(R.string.prompt_no_match_label)
                .setPositiveButton(R.string.dialog_ok) {
                }.show()
        })

        scanProductChangePosViewModel.noMatchPos.observe(this, Observer {
            CustomDialog(this, R.layout.dialog_incorrect)
                .setTitle(R.string.button_save_failed)
                .setMessage(R.string.prompt_no_match_pos)
                .setPositiveButton(R.string.dialog_ok) {
                }.show()
        })

        scanProductChangePosViewModel.changePosEvent.observe(this, Observer {
            notification_label_no_tv.text =
                getString(R.string.label_no, scanProductChangePosViewModel.labelNo)
            notification_pos_no_tv.text = getString(
                R.string.change_pos,
                scanProductChangePosViewModel.notificationCurrentPosCd,
                scanProductChangePosViewModel.notificationChangePosCd
            )
        })

        scanProductChangePosViewModel.updateImpossible.observe(this, Observer {
            CustomDialog(this, R.layout.dialog_incorrect)
                .setTitle(R.string.button_save_failed)
                .setMessage(R.string.prompt_update_impossible)
                .setPositiveButton(R.string.dialog_ok) {
                }.show()
        })

        scanProductChangePosViewModel.clearInputLayout.observe(this, Observer {
            hideKeyboard()
        })

        commonLayout.setOnClickListener { hideKeyboard() }
        change_stock_auto_tv.inputType = 0
        change_stock_auto_tv.setRawInputType(InputType.TYPE_CLASS_TEXT)
        change_stock_auto_tv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                transToUpperCase(s, change_stock_auto_tv)
                MainActivity.autoCompleteTextViewCustom(
                    change_stock_auto_tv,
                    this@ScanProductChangePosActivity
                )
            }
        })
    }

    private fun hideKeyboard() {
        val imm =
            this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(commonLayout.windowToken, 0)
    }

    override fun onResume() {
        super.onResume()
        barcode_scanner.resume()
    }

    override fun onPause() {
        super.onPause()
        barcode_scanner.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        CustomDialog(this, R.layout.dialog_incorrect)
            .dismiss()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return super.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            scanProductChangePosViewModel.screenOrientation()
        }else{
            scanProductChangePosViewModel.screenOrientation()
        }
    }
}