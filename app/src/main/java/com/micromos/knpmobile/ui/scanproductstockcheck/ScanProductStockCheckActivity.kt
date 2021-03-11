package com.micromos.knpmobile.ui.scanproductstockcheck

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
import com.micromos.knpmobile.CustomDialog
import com.micromos.knpmobile.MainActivity
import com.micromos.knpmobile.R
import com.micromos.knpmobile.databinding.ActivityCoilStockScanBinding
import kotlinx.android.synthetic.main.activity_coil_stock_scan.barcode_scanner
import kotlinx.android.synthetic.main.activity_coil_stock_scan.change_stock_auto_tv
import kotlinx.android.synthetic.main.activity_coil_stock_scan.commonLayout
import kotlinx.android.synthetic.main.activity_coil_stock_scan.progress_bar

class ScanProductStockCheckActivity : AppCompatActivity() {

    private lateinit var scanProductStockCheckViewModel: ScanProductStockCheckViewModel
    private lateinit var productStockCheckScanBinding: ActivityCoilStockScanBinding
    private lateinit var adapter: ScanProductStockCheckAdapter

    private lateinit var beepManager: BeepManager
    private var lastText: String? = null

    private val formats: Collection<BarcodeFormat> = listOf(
        BarcodeFormat.QR_CODE,
        BarcodeFormat.CODE_39
    )

    private val callback: BarcodeCallback = object : BarcodeCallback {
        override fun barcodeResult(result: BarcodeResult) {
            if (result.text == null || result.text == lastText || result.barcodeFormat !in formats) {
                // Prevent duplicate scans
                return
            }
            lastText = result.text
            barcode_scanner.setStatusText(result.text)

            scanProductStockCheckViewModel.labelNo.value = result.text
            scanProductStockCheckViewModel.labelRetrieve()
            val tone = ToneGenerator(AudioManager.STREAM_MUSIC, ToneGenerator.MAX_VOLUME)
            tone.startTone(ToneGenerator.TONE_PROP_BEEP2, 500)
        }

        override fun possibleResultPoints(resultPoints: List<ResultPoint>) {}
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productStockCheckScanBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_coil_stock_scan)
        scanProductStockCheckViewModel =
            ViewModelProvider(this).get(ScanProductStockCheckViewModel::class.java)
        productStockCheckScanBinding.viewModel = scanProductStockCheckViewModel
        productStockCheckScanBinding.lifecycleOwner = this

        barcode_scanner.barcodeView.decoderFactory = DefaultDecoderFactory(formats)
        barcode_scanner.initializeFromIntent(intent)
        barcode_scanner.decodeContinuous(callback)
        beepManager = BeepManager(this)

        val intent = intent.getStringExtra("stockDate")!!
        scanProductStockCheckViewModel.stockDate = intent

        setRecyclerView()

        scanProductStockCheckViewModel.noLabelNo.observe(this, Observer {
            CustomDialog(this, R.layout.dialog_incorrect)
                .setTitle(R.string.prompt_notification)
                .setMessage(R.string.prompt_no_label_no)
                .setPositiveButton(R.string.dialog_ok) {
                }.show()
        })

        scanProductStockCheckViewModel.noPosCdNo.observe(this, Observer {
            CustomDialog(this, R.layout.dialog_incorrect)
                .setTitle(R.string.prompt_notification)
                .setMessage(R.string.prompt_no_pos_cd)
                .setPositiveButton(R.string.dialog_ok) {
                }.show()
        })

        scanProductStockCheckViewModel.noPosCdMatch.observe(this, Observer {
            CustomDialog(this, R.layout.dialog_incorrect)
                .setTitle(R.string.prompt_notification)
                .setMessage(R.string.prompt_no_pos_cd_match)
                .setPositiveButton(R.string.dialog_ok) {
                }.show()
        })

        scanProductStockCheckViewModel.unExceptedError.observe(this, Observer {
            CustomDialog(this, R.layout.dialog_incorrect)
                .setTitle(R.string.prompt_error)
                .setMessage(R.string.unexpected_error_pda_stock_scan)
                .setPositiveButton(R.string.dialog_ok) {
                }.show()
        })

        scanProductStockCheckViewModel.noNetWorkConnect.observe(this, Observer {
            CustomDialog(this, R.layout.dialog_incorrect)
                .setTitle(R.string.prompt_error)
                .setMessage(R.string.network_failed)
                .setPositiveButton(R.string.dialog_ok) {
                }.show()
        })

        scanProductStockCheckViewModel.isLoading.observe(this, Observer {
            if (it) {
                Log.d("visibleProgressBarOn", "${scanProductStockCheckViewModel.isLoading.value}")
                this.window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                );
                progress_bar.visibility = View.VISIBLE
            } else {
                Log.d("visibleProgressBarOff", "${scanProductStockCheckViewModel.isLoading.value}")
                this.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                progress_bar.visibility = View.INVISIBLE
                hideKeyboard()
            }
        })

        commonLayout.setOnClickListener { hideKeyboard() }

        change_stock_auto_tv.setOnClickListener {
            change_stock_auto_tv.text = change_stock_auto_tv.text
            val eText = change_stock_auto_tv.text
            Selection.setSelection(eText, eText.length)
        }

        change_stock_auto_tv.inputType = 0
        change_stock_auto_tv.setRawInputType(InputType.TYPE_CLASS_TEXT)
        change_stock_auto_tv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                MainActivity.transToUpperCase(s, change_stock_auto_tv)
                MainActivity.autoCompleteTextViewCustom(
                    change_stock_auto_tv,
                    this@ScanProductStockCheckActivity
                )
            }
        })
    }

    private fun setRecyclerView() {
        adapter = ScanProductStockCheckAdapter(scanProductStockCheckViewModel, this)
        productStockCheckScanBinding.recyclerView.adapter = adapter

        scanProductStockCheckViewModel.cardItemListDataUpdate.observe(this, Observer {
            if (it != null) {
                it.updateFlag = 1
                adapter.item.add(0, it)
                adapter.notifyDataSetChanged()
            }
        })

        scanProductStockCheckViewModel.cardItemListDataInsert.observe(this, Observer {
            if (it != null) {
                it.updateFlag = 0
                adapter.item.add(0, it)
                adapter.notifyDataSetChanged()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        barcode_scanner.resume()
    }

    override fun onPause() {
        super.onPause()
        barcode_scanner.pause()
    }

    private fun hideKeyboard() {
        val imm =
            this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(commonLayout.windowToken, 0)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return super.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            scanProductStockCheckViewModel.screenOrientation()
        }else{
            scanProductStockCheckViewModel.screenOrientation()
        }
    }
}

