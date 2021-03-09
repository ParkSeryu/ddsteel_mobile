package com.micromos.knpmobile.ui.scan

import com.journeyapps.barcodescanner.CaptureActivity
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.micromos.knpmobile.R
import kotlinx.android.synthetic.main.activity_scan.*

class LabelCaptureActivity : CaptureActivity() {
    override fun initializeContent(): DecoratedBarcodeView {
        setContentView(R.layout.activity_scan)
        return zxing_barcode_scanner
    }
}