package com.micromos.knpmobile.ui.productchangepos

import android.os.Bundle
import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.micromos.knpmobile.CustomDialog
import com.micromos.knpmobile.MainActivity
import com.micromos.knpmobile.MainActivity.Companion.autoCompleteTextViewCustom
import com.micromos.knpmobile.MainActivity.Companion.codeNmList
import com.micromos.knpmobile.R
import com.micromos.knpmobile.databinding.FragmentChangePosBinding
import com.micromos.knpmobile.ui.home.HomeFragment
import com.micromos.knpmobile.ui.productcoilout.ProductCoilOutFragment
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_change_pos.*

class ProductChangePosFragment : Fragment() {

    private lateinit var productChangePosViewModel: ProductChangePosViewModel
    private lateinit var coilChangePosDataBinding: FragmentChangePosBinding

    companion object {
        fun newInstance() = ProductChangePosFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        outer_layout_change_pos.setOnClickListener { hideKeyboard() }
        (requireActivity() as MainActivity).setTextChangedListener(label_no_edt_pos)

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
}