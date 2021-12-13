//package com.micromos.ddsteelmobile.ui.scanproductcoilin
//
//import android.content.res.Configuration
//import android.media.AudioManager
//import android.media.ToneGenerator
//import android.os.Bundle
//import android.util.Log
//import android.view.KeyEvent
//import android.view.View
//import android.view.WindowManager
//import androidx.appcompat.app.AppCompatActivity
//import androidx.databinding.DataBindingUtil
//import androidx.lifecycle.Observer
//import androidx.lifecycle.ViewModelProvider
//import com.google.zxing.BarcodeFormat
//import com.google.zxing.ResultPoint
//import com.google.zxing.client.android.BeepManager
//import com.journeyapps.barcodescanner.BarcodeCallback
//import com.journeyapps.barcodescanner.BarcodeResult
//import com.journeyapps.barcodescanner.DefaultDecoderFactory
//import com.micromos.ddsteelmobile.CustomDialog
//import com.micromos.ddsteelmobile.R
//import com.micromos.ddsteelmobile.databinding.ActivityCoilInScanBinding
//import kotlinx.android.synthetic.main.activity_coil_in_scan.*
//import kotlinx.android.synthetic.main.activity_coil_in_scan.progress_bar
//
//
//class ScanProductCoilInActivity : AppCompatActivity() {
//    private lateinit var scanProductCoilInViewModel: ScanProductCoilInViewModel
//    private lateinit var coilInScanBinding: ActivityCoilInScanBinding
//    private lateinit var adapter: ProductCoilInCaptureAdapter
//    private lateinit var beepManager: BeepManager
//    private var lastText: String? = null
//
//    private val formats: Collection<BarcodeFormat> = listOf(
//        BarcodeFormat.QR_CODE,
//        BarcodeFormat.CODE_39
//    )
//
//
//    private val callback: BarcodeCallback = object : BarcodeCallback {
//        override fun barcodeResult(result: BarcodeResult) {
//            if (result.text == null || result.text == lastText || result.barcodeFormat !in formats || progress_bar.visibility == View.VISIBLE) {
//                // Prevent duplicate scans
//                return
//            }
//            lastText = result.text
//            barcode_scanner.setStatusText(result.text)
//            scanProductCoilInViewModel._requestNo.value = result.text
//            scanProductCoilInViewModel.shipNoRetrieve(scanProductCoilInViewModel._requestNo.value)
//            val tone = ToneGenerator(AudioManager.STREAM_MUSIC, ToneGenerator.MAX_VOLUME)
//            tone.startTone(ToneGenerator.TONE_PROP_BEEP2 , 500)
//        }
//
//        override fun possibleResultPoints(resultPoints: List<ResultPoint>) {}
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        coilInScanBinding =
//            DataBindingUtil.setContentView(this, R.layout.activity_coil_in_scan)
//        scanProductCoilInViewModel =
//            ViewModelProvider(this).get(ScanProductCoilInViewModel::class.java)
//        coilInScanBinding.viewModel = scanProductCoilInViewModel
//        coilInScanBinding.lifecycleOwner = this
//
//        barcode_scanner.barcodeView.decoderFactory = DefaultDecoderFactory(formats)
//        barcode_scanner.initializeFromIntent(intent)
//        barcode_scanner.decodeContinuous(callback)
//        beepManager = BeepManager(this)
//
//        setRecyclerView()
//
//        scanProductCoilInViewModel.noRetrieve.observe(this, Observer {
//            CustomDialog(this, R.layout.dialog_incorrect)
//                .setTitle("알림")
//                .setMessage(R.string.prompt_no_retrieve_ship_in)
//                .setPositiveButton(R.string.dialog_ok) {
//                }.show()
//        })
//
//        scanProductCoilInViewModel.noNetWorkConnect.observe(this, Observer {
//            CustomDialog(this, R.layout.dialog_incorrect)
//                .setTitle(R.string.prompt_error)
//                .setMessage(R.string.network_failed)
//                .setPositiveButton(R.string.dialog_ok) {
//                }.show()
//        })
//
//        scanProductCoilInViewModel.unExceptedError.observe(this, Observer {
//            CustomDialog(this, R.layout.dialog_incorrect)
//                .setTitle(R.string.prompt_error)
//                .setMessage(R.string.unexpected_error_pda_in_scan)
//                .setPositiveButton(R.string.dialog_ok) {
//                }.show()
//        })
//
//        scanProductCoilInViewModel.isLoading.observe(this, Observer {
//            if (it) {
//                this.window.setFlags(
//                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
//                )
//                progress_bar.visibility = View.VISIBLE
//            } else {
//                this.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//                progress_bar.visibility = View.INVISIBLE
//            }
//        })
//
//        scanProductCoilInViewModel.noLabelNo.observe(this, Observer {
//            CustomDialog(this, R.layout.dialog_incorrect)
//                .setTitle(R.string.prompt_notification)
//                .setMessage(R.string.prompt_no_label_ship_in)
//                .setPositiveButton(R.string.dialog_ok) {
//                }.show()
//            Log.d("test", "${scanProductCoilInViewModel.noLabelNo.value}")
//        })
//
//        scanProductCoilInViewModel.dateTimeOverlap.observe(this, Observer {
//            CustomDialog(this, R.layout.dialog_incorrect)
//                .setTitle(R.string.prompt_notification)
//                .setMessage(R.string.prompt_label_overlap_ship_in)
//                .setPositiveButton(R.string.dialog_ok) {
//                }.show()
//        })
//
//        scanProductCoilInViewModel.noCompleteAllLabel.observe(this, Observer {
//            if (scanProductCoilInViewModel.numerator != scanProductCoilInViewModel.denomiator) {
//                CustomDialog(this, R.layout.dialog_request)
//                    .setTitle(R.string.prompt_notification)
//                    .setMessage(R.string.prompt_label_no_success_ship_in)
//                    .setPositiveButton(R.string.dialog_ok) {
//                        scanProductCoilInViewModel.prevShipNo.value =
//                            scanProductCoilInViewModel._requestNo.value?.trim()
//                        scanProductCoilInViewModel.shipNoRetrieve(
//                            scanProductCoilInViewModel._requestNo.value
//                        )
//                    }.setNegativeButton(R.string.dialog_ng) {
//                        scanProductCoilInViewModel.successCall()
//                    }.show()
//            } else {
//                scanProductCoilInViewModel.prevShipNo.value =
//                    scanProductCoilInViewModel._requestNo.value?.trim()
//                scanProductCoilInViewModel.shipNoRetrieve(scanProductCoilInViewModel._requestNo.value)
//            }
//        })
//
//        scanProductCoilInViewModel.noModifyEvent.observe(this, Observer {
//            CustomDialog(this, R.layout.dialog_incorrect)
//                .setTitle(R.string.prompt_notification)
//                .setMessage(R.string.prompt_label_no_modify_ship_in)
//                .setPositiveButton(R.string.dialog_ok) {
//                }.show()
//        })
//
//
//    }
//
//    private fun setRecyclerView() {
//        adapter = ProductCoilInCaptureAdapter(scanProductCoilInViewModel)
//        scanProductCoilInViewModel.shipOrderList.observe(this, Observer {
//            coilInScanBinding.recyclerView.adapter = adapter
//            if (it != null) {
//                adapter.items = it
//            } else {
//                adapter.items = emptyList()
//                adapter.notifyDataSetChanged()
//            }
//        })
//    }
//
//
//    override fun onResume() {
//        super.onResume()
//        barcode_scanner.resume()
//    }
//
//    override fun onPause() {
//        super.onPause()
//        barcode_scanner.pause()
//    }
//
//    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
//        return super.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event)
//    }
//
//    override fun onConfigurationChanged(newConfig: Configuration) {
//        super.onConfigurationChanged(newConfig)
//        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
//            scanProductCoilInViewModel.screenOrientation()
//        }else{
//            scanProductCoilInViewModel.screenOrientation()
//        }
//    }
//}