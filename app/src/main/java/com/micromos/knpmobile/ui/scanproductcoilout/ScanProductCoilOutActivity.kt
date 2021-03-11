package com.micromos.knpmobile.ui.scanproductcoilout

import android.content.res.Configuration
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
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
import com.micromos.knpmobile.R
import com.micromos.knpmobile.databinding.ActivityCoilOutScanBinding
import kotlinx.android.synthetic.main.activity_coil_out_scan.*
import kotlinx.android.synthetic.main.activity_coil_out_scan.progress_bar
import kotlinx.android.synthetic.main.fragment_coil_out.*


class ScanProductCoilOutActivity : AppCompatActivity() {
    private lateinit var scanProductCoilOutViewModel: ScanProductCoilOutViewModel
    private lateinit var coilOutScanBinding: ActivityCoilOutScanBinding
    private lateinit var adapter: ProductCoilInCaptureAdapter
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
            scanProductCoilOutViewModel._requestNo.value = result.text
            scanProductCoilOutViewModel.shipNoRetrieve(scanProductCoilOutViewModel._requestNo.value)
            val tone = ToneGenerator(AudioManager.STREAM_MUSIC, ToneGenerator.MAX_VOLUME)
            tone.startTone(ToneGenerator.TONE_PROP_BEEP2 , 500)
        }

        override fun possibleResultPoints(resultPoints: List<ResultPoint>) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        coilOutScanBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_coil_out_scan)
        scanProductCoilOutViewModel =
            ViewModelProvider(this).get(ScanProductCoilOutViewModel::class.java)
        coilOutScanBinding.viewModel = scanProductCoilOutViewModel
        coilOutScanBinding.lifecycleOwner = this

        barcode_scanner.barcodeView.decoderFactory = DefaultDecoderFactory(formats)
        barcode_scanner.initializeFromIntent(intent)
        barcode_scanner.decodeContinuous(callback)
        beepManager = BeepManager(this)

        setRecyclerView()

        scanProductCoilOutViewModel.noRetrieve.observe(this, Observer {
            CustomDialog(this, R.layout.dialog_incorrect)
                .setTitle("알림")
                .setMessage(R.string.prompt_no_retrieve_ship_out)
                .setPositiveButton(R.string.dialog_ok) {
                }.show()
        })

        scanProductCoilOutViewModel.noNetWorkConnect.observe(this, Observer {
            CustomDialog(this, R.layout.dialog_incorrect)
                .setTitle(R.string.prompt_error)
                .setMessage(R.string.network_failed)
                .setPositiveButton(R.string.dialog_ok) {
                }.show()
        })

        scanProductCoilOutViewModel.unExceptedError.observe(this, Observer {
            CustomDialog(this, R.layout.dialog_incorrect)
                .setTitle(R.string.prompt_error)
                .setMessage(R.string.unexpected_error_pda_out_scan)
                .setPositiveButton(R.string.dialog_ok) {
                }.show()
        })

        scanProductCoilOutViewModel.isLoading.observe(this, Observer {
            if (it) {
                this.window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
                progress_bar.visibility = View.VISIBLE
            } else {
                this.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                progress_bar.visibility = View.INVISIBLE
            }
        })

        scanProductCoilOutViewModel.noLabelNo.observe(this, Observer
        {
            CustomDialog(this, R.layout.dialog_incorrect)
                .setTitle(R.string.prompt_notification)
                .setMessage(R.string.prompt_no_label_ship_out)
                .setPositiveButton(R.string.dialog_ok) {
                }.show()
        })

        scanProductCoilOutViewModel.noModifyEvent.observe(this, Observer
        {
            CustomDialog(this, R.layout.dialog_incorrect)
                .setTitle(R.string.prompt_notification)
                .setMessage(R.string.prompt_label_no_modify_ship_out)
                .setPositiveButton(R.string.dialog_ok) {
                }.show()
        })

        scanProductCoilOutViewModel.dateTimeOverlap.observe(this, Observer
        {
            CustomDialog(this, R.layout.dialog_incorrect)
                .setTitle(R.string.prompt_notification)
                .setMessage(R.string.prompt_label_overlap_ship_out)
                .setPositiveButton(R.string.dialog_ok) {
                }.show()
        })

        scanProductCoilOutViewModel.noCompleteCoilIn.observe(this, Observer
        {
            CustomDialog(this, R.layout.dialog_incorrect)
                .setTitle(R.string.prompt_notification)
                .setMessage(R.string.prompt_no_complete_coil_out)
                .setPositiveButton(R.string.dialog_ok) {
                }.show()
        })

        scanProductCoilOutViewModel.cardClick.observe(this, Observer
        {
            ship_no_edt.setText(it)
        })

        scanProductCoilOutViewModel.noCompleteAllLabel.observe(this, Observer
        {
            if (scanProductCoilOutViewModel.numerator != scanProductCoilOutViewModel.denomiator) {
                CustomDialog(this, R.layout.dialog_request)
                    .setTitle(R.string.prompt_notification)
                    .setMessage(R.string.prompt_label_no_success_ship_out)
                    .setPositiveButton(R.string.dialog_ok) {
                        scanProductCoilOutViewModel.prevShipNo.value =
                            scanProductCoilOutViewModel._requestNo.value?.trim()
                        scanProductCoilOutViewModel.shipNoRetrieve(
                            scanProductCoilOutViewModel._requestNo.value
                        )
                    }.setNegativeButton(R.string.dialog_ng) {
                        scanProductCoilOutViewModel.successCall()
                    }.show()
            } else {
                scanProductCoilOutViewModel.prevShipNo.value =
                    scanProductCoilOutViewModel._requestNo.value?.trim()
                scanProductCoilOutViewModel.shipNoRetrieve(scanProductCoilOutViewModel._requestNo.value)
            }
        })
    }

    private fun setRecyclerView() {
        adapter = ProductCoilInCaptureAdapter(scanProductCoilOutViewModel)
        scanProductCoilOutViewModel.shipOrderList.observe(this, Observer {
            coilOutScanBinding.recyclerView.adapter = adapter
            if (it != null) {
                adapter.items = it
            } else {
                adapter.items = emptyList()
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

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return super.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event)
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            scanProductCoilOutViewModel.screenOrientation()
        }else{
            scanProductCoilOutViewModel.screenOrientation()
        }
    }
}