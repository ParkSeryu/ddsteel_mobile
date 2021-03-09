package com.micromos.knpmobile.ui.scan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.zxing.integration.android.IntentIntegrator
import com.micromos.knpmobile.R
import com.micromos.knpmobile.databinding.FragmentContinousScanBinding
import com.micromos.knpmobile.ui.productcoilin.ProductCoilInFragment

class LabelContinuousCaptureFragment : Fragment() {
    private lateinit var labelContinuousCaptureFragmentViewModel: LabelContinuousCaptureViewModel
    private lateinit var continuousCaptureBinding: FragmentContinousScanBinding

    companion object {
        fun newInstance() = ProductCoilInFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


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

        //IntentIntegrator.forSupportFragment(this).initiateScan()

        return continuousCaptureBinding.root
    }
}