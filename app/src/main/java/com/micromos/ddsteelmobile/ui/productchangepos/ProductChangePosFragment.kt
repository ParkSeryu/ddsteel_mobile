package com.micromos.ddsteelmobile.ui.productchangepos

import android.content.res.Configuration
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
import com.micromos.ddsteelmobile.CustomDialog
import com.micromos.ddsteelmobile.M3Receiver
import com.micromos.ddsteelmobile.MainActivity
import com.micromos.ddsteelmobile.MainActivity.Companion.autoCompleteTextViewCustomPosCd
import com.micromos.ddsteelmobile.MainActivity.Companion.pos_nm_list
import com.micromos.ddsteelmobile.MainActivity.Companion.transToUpperCase
import com.micromos.ddsteelmobile.R
import com.micromos.ddsteelmobile.databinding.FragmentChangePosBinding
import com.micromos.ddsteelmobile.ui.home.HomeFragment
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_change_pos.*
import kotlinx.android.synthetic.main.fragment_change_pos.change_stock_auto_tv
import kotlinx.android.synthetic.main.fragment_change_pos.input_layout
import kotlinx.android.synthetic.main.fragment_change_pos.progress_bar
import kotlinx.android.synthetic.main.fragment_material_in.*
import java.util.*

class ProductChangePosFragment : Fragment(), M3Receiver.ScanListener {

    private lateinit var productChangePosViewModel: ProductChangePosViewModel
    private lateinit var coilChangePosDataBinding: FragmentChangePosBinding
    private val m3Receiver: M3Receiver by lazy { M3Receiver.getInstance() }

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
                transToUpperCase(s, change_stock_auto_tv)
                context?.let { autoCompleteTextViewCustomPosCd(change_stock_auto_tv, it) }
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

        if (pos_nm_list.size == 0) {
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
                activity?.window?.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                );
                progress_bar.visibility = View.VISIBLE
            } else {
                Log.d("visible", "${productChangePosViewModel.isLoading.value}")
                activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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
            label_no_edt_pos.requestFocus()
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

//        productChangePosViewModel.onClickScanButton.observe(viewLifecycleOwner, Observer {
//            val intent = Intent(
//                context, ScanProductChangePosActivity::class.java
//            )
//            startActivity(intent)
//        })

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (label_no_edt_pos.text.toString() != "") {
                (requireActivity() as MainActivity).replaceFragment(newInstance())
                input_layout.requestFocus()
                label_no_edt_pos.requestFocus()
            } else {
                (requireActivity() as MainActivity).replaceFragment(HomeFragment.newInstance())
            }

        }

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

    override fun onScan(scanResult: String) {
        if (progress_bar.visibility == View.VISIBLE || scanResult.toCharArray().isEmpty())
            return
        val resultScan = scanResult.toUpperCase(Locale.ROOT)

        label_no_edt_pos.setText(resultScan)

        productChangePosViewModel.retrievePos(resultScan)
    }

    override fun onResume() {
        super.onResume()
        Log.d("onResumePosChange", "onResumePosChange")
        m3Receiver.register(this)
    }

    override fun onPause() {
        super.onPause()
        Log.d("onPausePosChange", "onPausePosChange")
        m3Receiver.unRegister()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            productChangePosViewModel.screenOrientation()
        }else{
            productChangePosViewModel.screenOrientation()
        }
    }
}