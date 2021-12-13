/*
package com.micromos.knpmobile.ui.scan

import android.content.Intent.getIntent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import com.micromos.knpmobile.R
import com.micromos.knpmobile.databinding.FragmentContinousScanBinding
import kotlinx.android.synthetic.main.fragment_continous_scan.*
import java.util.*

class LabelContinuousCaptureFragment : Fragment() {
    private lateinit var labelContinuousCaptureFragmentViewModel: LabelContinuousCaptureViewModel
    private lateinit var continuousCaptureBinding: FragmentContinousScanBinding
    private val formats: Collection<BarcodeFormat> = listOf(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39)

    companion object {
        fun newInstance() = LabelContinuousCaptureFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        barcode_scanner.barcodeView.decoderFactory = DefaultDecoderFactory(formats)
        barcode_scanner.initializeFromIntent(getIntent())
        barcode_scanner.decodeContinuous(labelContinuousCaptureFragmentViewModel.callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        labelContinuousCaptureFragmentViewModel =
            ViewModelProvider(this).get(LabelContinuousCaptureViewModel::class.java)

        continuousCaptureBinding = DataBindingUtil.inflate<FragmentContinousScanBinding>(
            inflater,
            R.layout.fragment_continous_scan,
            container,
            false
        ).apply {
            this.viewModel = labelContinuousCaptureFragmentViewModel
            this.lifecycleOwner = this@LabelContinuousCaptureFragment
        }



        return continuousCaptureBinding.root
    }

    override fun onResume() {
        super.onResume()
        barcode_scanner.pause()
    }

    override fun onPause() {
        super.onPause()
        barcode_scanner.resume()
    }

}
*/
