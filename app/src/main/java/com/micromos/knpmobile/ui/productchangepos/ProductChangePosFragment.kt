package com.micromos.knpmobile.ui.productchangepos

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.Selection
import android.text.TextWatcher
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
import com.micromos.knpmobile.MainActivity.Companion.autoCompleteTextViewCustom
import com.micromos.knpmobile.MainActivity.Companion.codeNmList
import com.micromos.knpmobile.R
import com.micromos.knpmobile.databinding.FragmentChangePosBinding
import com.micromos.knpmobile.ui.home.HomeFragment
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_change_pos.*
import kotlinx.android.synthetic.main.fragment_change_pos.input_layout
import kotlinx.android.synthetic.main.fragment_change_pos.progress_bar
import java.util.*

class ProductChangePosFragment : Fragment(), ReaderDevice.OnConnectionCompletedListener,
    ReaderDevice.ReaderDeviceListener {

    private lateinit var productChangePosViewModel: ProductChangePosViewModel
    private lateinit var coilChangePosDataBinding: FragmentChangePosBinding
    private lateinit var readerDevice: ReaderDevice

    companion object {
        fun newInstance() = ProductChangePosFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        outer_layout_change_pos.setOnClickListener { hideKeyboard() }
        (requireActivity() as MainActivity).setTextChangedListener(label_no_edt_pos)

        change_stock_auto_tv.inputType = 0
        change_stock_auto_tv.setRawInputType(InputType.TYPE_CLASS_TEXT)

        change_stock_auto_tv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                (requireActivity() as MainActivity).transToUpperCase(s, change_stock_auto_tv)
                context?.let { autoCompleteTextViewCustom(change_stock_auto_tv, it) }
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        productChangePosViewModel =
            ViewModelProvider(this).get(ProductChangePosViewModel::class.java)

        coilChangePosDataBinding = DataBindingUtil.inflate<FragmentChangePosBinding>(
            inflater,
            R.layout.fragment_change_pos,
            container,
            false
        ).apply {
            this.viewModel = productChangePosViewModel
            this.lifecycleOwner = this@ProductChangePosFragment
        }
        setToolbar()

        coilChangePosDataBinding.changeStockAutoTv.setOnClickListener {
            val view = coilChangePosDataBinding.changeStockAutoTv
            view.text = view.text
            val etext = view.text
            Selection.setSelection(etext, etext.length)
        }

        if (codeNmList.size == 0) {
            context?.let { view ->
                CustomDialog(view, R.layout.dialog_incorrect)
                    .setTitle(R.string.prompt_error)
                    .setMessage(R.string.failed_get_code)
                    .setPositiveButton(R.string.dialog_ok) {
                    }.show()

            }
        }

        productChangePosViewModel.noNetWorkConnect.observe(viewLifecycleOwner, Observer {
            context?.let { view ->
                CustomDialog(view, R.layout.dialog_incorrect)
                    .setTitle(R.string.prompt_error)
                    .setMessage(R.string.network_failed)
                    .setPositiveButton(R.string.dialog_ok) {
                    }.show()
            }
        })

        productChangePosViewModel.unExceptedError.observe(viewLifecycleOwner, Observer {
            context?.let { view ->
                CustomDialog(view, R.layout.dialog_incorrect)
                    .setTitle(R.string.prompt_error)
                    .setMessage(R.string.unexpected_error_pda_change_pos)
                    .setPositiveButton(R.string.dialog_ok) {
                    }.show()
            }
        })

        productChangePosViewModel.isLoading.observe(viewLifecycleOwner, Observer {
            if (it) {
                Log.d("visible", "${productChangePosViewModel.isLoading.value}")
                activity?.getWindow()?.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                );
                progress_bar.visibility = View.VISIBLE
            } else {
                Log.d("visible", "${productChangePosViewModel.isLoading.value}")
                activity?.getWindow()?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                progress_bar.visibility = View.INVISIBLE
                hideKeyboard()
            }
        })


        productChangePosViewModel.noMatchLabel.observe(viewLifecycleOwner, Observer {
            context?.let { view ->
                CustomDialog(view, R.layout.dialog_incorrect)
                    .setTitle(R.string.prompt_notification)
                    .setMessage(R.string.prompt_no_match_label)
                    .setPositiveButton(R.string.dialog_ok) {
                    }.show()
            }
        })

        productChangePosViewModel.noMatchPos.observe(viewLifecycleOwner, Observer {
            context?.let { view ->
                CustomDialog(view, R.layout.dialog_incorrect)
                    .setTitle(R.string.button_save_failed)
                    .setMessage(R.string.prompt_no_match_pos)
                    .setPositiveButton(R.string.dialog_ok) {
                    }.show()
            }
        })

        productChangePosViewModel.focusChangeEvent.observe(viewLifecycleOwner, Observer {
            coilChangePosDataBinding.changeStockAutoTv.requestFocus()
        })

        productChangePosViewModel.clearInputLayout.observe(viewLifecycleOwner, Observer {
            label_no_edt_pos.setText("")
            change_stock_auto_tv.setText("")
            label_no_edt_pos.requestFocus()
        })

        productChangePosViewModel.changePosEvent.observe(viewLifecycleOwner, Observer {
            notification_label_no_tv.text = getString(R.string.label_no, productChangePosViewModel.labelNo)
            notification_pos_no_tv.text = getString(R.string.change_pos, productChangePosViewModel.notificationCurrentPosCd, productChangePosViewModel.notificationChangePosCd)
        })

        productChangePosViewModel.updateImpossible.observe(viewLifecycleOwner, Observer {
            context?.let { view ->
                CustomDialog(view, R.layout.dialog_incorrect)
                    .setTitle(R.string.button_save_failed)
                    .setMessage(R.string.prompt_update_impossible)
                    .setPositiveButton(R.string.dialog_ok) {
                    }.show()
            }
        })

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (label_no_edt_pos.text.toString() != "") {
                (requireActivity() as MainActivity).replaceFragment(newInstance())
                input_layout.requestFocus()
                label_no_edt_pos.requestFocus()
            } else {
                (requireActivity() as MainActivity).replaceFragment(HomeFragment.newInstance())
            }

        }

        readerDevice = ReaderDevice.getMXDevice(context)
        readerDevice.startAvailabilityListening()
        readerDevice.setReaderDeviceListener(this)
        readerDevice.connect(this@ProductChangePosFragment)

        return coilChangePosDataBinding.root
    }


    private fun hideKeyboard() {
        val imm =
            activity?.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(input_layout.windowToken, 0)
    }

    private fun setToolbar() {
        (requireActivity() as MainActivity).toolbar_title.text = getString(R.string.menu_change_pos)
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
            if (results.count <= 0 || results.getResultAt(0).readString!!.equals("") || progress_bar.visibility == View.VISIBLE)
                return

            var resultString = results.getResultAt(0).readString!!
            Log.d("scanTest", resultString)
            if (resultString.contains("\\000026")) {
                resultString = resultString.split("\\000026")[1]
            }
            productChangePosViewModel._labelNo.value = resultString.toUpperCase(Locale.ROOT)
            productChangePosViewModel.retrievePos(productChangePosViewModel._labelNo.value)
        } catch (e: Exception) {
            Toast.makeText(context, "읽은값 : ${e.message.toString()}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        readerDevice.startAvailabilityListening()
        readerDevice.setReaderDeviceListener(this)
        readerDevice.connect(this@ProductChangePosFragment)
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
}