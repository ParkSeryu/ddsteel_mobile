package com.micromos.knpmobile.ui.productMaterialCheck

import android.content.res.Configuration
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.cognex.mobile.barcode.sdk.ReadResults
import com.cognex.mobile.barcode.sdk.ReaderDevice
import com.micromos.knpmobile.CustomDialog
import com.micromos.knpmobile.MainActivity
import com.micromos.knpmobile.R
import com.micromos.knpmobile.databinding.FragmentMaterialCheckBinding
import com.micromos.knpmobile.ui.home.HomeFragment
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_material_check.*


class ProductMaterialCheckFragment : Fragment(), ReaderDevice.OnConnectionCompletedListener,
    ReaderDevice.ReaderDeviceListener {

    private lateinit var productMaterialCheckViewModel: ProductMaterialCheckViewModel
    private lateinit var materialCheckDataBinding: FragmentMaterialCheckBinding
    private lateinit var readerDevice: ReaderDevice

    companion object {
        fun newInstance() = ProductMaterialCheckFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        outer_layout_material_check.setOnClickListener { hideKeyboard() }
        (requireActivity() as MainActivity).setTextChangedListener(label_no_hyundai)
        (requireActivity() as MainActivity).setTextChangedListener(label_no_knp)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        productMaterialCheckViewModel =
            ViewModelProvider(this).get(ProductMaterialCheckViewModel::class.java)

        materialCheckDataBinding = DataBindingUtil.inflate<FragmentMaterialCheckBinding>(
            inflater,
            R.layout.fragment_material_check,
            container,
            false
        ).apply {
            this.viewModel = productMaterialCheckViewModel
            this.lifecycleOwner = this@ProductMaterialCheckFragment
        }
        setToolbar()

        productMaterialCheckViewModel.noNetWorkConnect.observe(viewLifecycleOwner, Observer {
            context?.let { view ->
                CustomDialog(view, R.layout.dialog_incorrect)
                    .setTitle(R.string.prompt_error)
                    .setMessage(R.string.network_failed)
                    .setPositiveButton(R.string.dialog_ok) {
                    }.show()
            }
        })

        productMaterialCheckViewModel.isLoading.observe(viewLifecycleOwner, Observer {
            if (it) {
                Log.d("visible", "${productMaterialCheckViewModel.isLoading.value}")
                activity?.window?.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                );
                progress_bar.visibility = View.VISIBLE
            } else {
                Log.d("visible", "${productMaterialCheckViewModel.isLoading.value}")
                activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                progress_bar.visibility = View.INVISIBLE
                hideKeyboard()
            }
        })

        productMaterialCheckViewModel.noMatchLabel.observe(viewLifecycleOwner, Observer {
            context?.let { view ->
                CustomDialog(view, R.layout.dialog_incorrect)
                    .setTitle(R.string.prompt_notification)
                    .setMessage(R.string.prompt_no_match_label)
                    .setPositiveButton(R.string.dialog_ok) {
                    }.show()
            }
        })

        productMaterialCheckViewModel.matchedLabelEvent.observe(viewLifecycleOwner, Observer {
//            notification_product_no_tv.text =
//                getString(R.string.mill_no, productMaterialCheckViewModel.productNoHyundai)
            notification_match_tv.text = Html.fromHtml(
                resources.getString(R.string.matchedLabel),
                Html.FROM_HTML_MODE_LEGACY
            )
        })

        productMaterialCheckViewModel.unMatchedLabelInfo.observe(viewLifecycleOwner, Observer {
            /*   notification_product_no_tv.text =
                   getString(R.string.mill_no, productMaterialCheckViewModel.productNoHyundai)*/
            notification_match_tv.text = Html.fromHtml(
                resources.getString(R.string.unMatchedLabel),
                Html.FROM_HTML_MODE_LEGACY
            )
        })


        productMaterialCheckViewModel.focusChangeEvent.observe(viewLifecycleOwner, Observer {
            materialCheckDataBinding.labelNoKnp.requestFocus()
        })

        productMaterialCheckViewModel.clearInputLayout.observe(viewLifecycleOwner, Observer {
            label_no_hyundai.setText("")
            label_no_knp.setText("")
        })

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (knpLabelInfoCardLayout.visibility == View.VISIBLE || hyundaiLabelInfoCardLayout.visibility == View.VISIBLE) {
                (requireActivity() as MainActivity).replaceFragment(newInstance())
                label_no_hyundai.requestFocus()
            } else {
                (requireActivity() as MainActivity).replaceFragment(HomeFragment.newInstance())
            }
        }

        readerDevice = ReaderDevice.getMXDevice(context)
        readerDevice.startAvailabilityListening()
        readerDevice.setReaderDeviceListener(this)
        readerDevice.connect(this@ProductMaterialCheckFragment)

        return materialCheckDataBinding.root
    }

    private fun hideKeyboard() {
        val imm =
            activity?.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(input_layout_hyundai_label.windowToken, 0)
        imm.hideSoftInputFromWindow(input_layout_knp.windowToken, 0)
    }

    private fun setToolbar() {
        (requireActivity() as MainActivity).toolbar_title.text =
            getString(R.string.menu_material_check)
        (requireActivity() as MainActivity).toolbar_numer.text = ""
        (requireActivity() as MainActivity).toolbar_hyphen.text = ""
        (requireActivity() as MainActivity).toolbar_denom.text = ""
    }

    override fun onConnectionCompleted(p0: ReaderDevice?, p1: Throwable?) {
        Log.i("beep", "ConnectionCompleted")
    }

    override fun onAvailabilityChanged(p0: ReaderDevice?) {
        Log.i("beep", "onAvailabilityChanged")
    }

    override fun onConnectionStateChanged(p0: ReaderDevice?) {
        Log.i("beep", p0?.connectionState.toString())
    }

    override fun onReadResultReceived(readerDevice: ReaderDevice?, results: ReadResults) {
        try {
            if (results.count <= 0 || results.getResultAt(0).readString!! == "" || progress_bar.visibility == View.VISIBLE)
                return

            var resultString = results.getResultAt(0).readString!!
            Log.d("scanTest", resultString)
            if (resultString.contains("\\000026")) {
                resultString = resultString.split("\\000026")[1]
            }
            if(resultString.contains("http")){
                productMaterialCheckViewModel.labelNoHyundai.value = resultString
                productMaterialCheckViewModel.hyundaiLabelRetrieve(productMaterialCheckViewModel.labelNoHyundai.value)
            }else{
                productMaterialCheckViewModel.labelNoKnp.value = resultString
                productMaterialCheckViewModel.knpLabelRetrieve(productMaterialCheckViewModel.labelNoKnp.value)
            }

        } catch (e: Exception) {
            Toast.makeText(context, "읽은값 : ${e.message.toString()}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        readerDevice.startAvailabilityListening()
        readerDevice.setReaderDeviceListener(this)
        readerDevice.connect(this@ProductMaterialCheckFragment)
    }

    override fun onPause() {
        super.onPause()
        readerDevice.stopAvailabilityListening()
        readerDevice.setReaderDeviceListener(null)
        readerDevice.disconnect()
    }

    override fun onDestroy() {
        super.onDestroy()
        readerDevice.stopAvailabilityListening()
        readerDevice.setReaderDeviceListener(null)
        readerDevice.disconnect()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            productMaterialCheckViewModel.screenOrientation()
        } else {
            productMaterialCheckViewModel.screenOrientation()
        }
    }
}