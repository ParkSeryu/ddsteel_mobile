package com.micromos.knpmobile.ui.productcoilstock

import android.app.DatePickerDialog
import android.content.Context
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
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.micromos.knpmobile.CustomDialog
import com.micromos.knpmobile.MainActivity
import com.micromos.knpmobile.MainActivity.Companion.autoCompleteTextViewCustom
import com.micromos.knpmobile.R
import com.micromos.knpmobile.databinding.FragmentCoilStockBinding
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_coil_stock.*
import kotlinx.android.synthetic.main.fragment_coil_stock.change_stock_auto_tv
import kotlinx.android.synthetic.main.fragment_coil_stock.progress_bar
import java.util.*

class ProductCoilStockFragment : Fragment() {

    private lateinit var productCoilStockViewModel: ProductCoilStockViewModel
    private lateinit var coilStockDataBinding: FragmentCoilStockBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pos_label_input_layout.visibility = View.INVISIBLE

        outer_layout_coil_stock.setOnClickListener { hideKeyboard() }
        recyclerView.setOnClickListener { hideKeyboard() }
        (requireActivity() as MainActivity).setTextChangedListener(change_stock_auto_tv)
        (requireActivity() as MainActivity).setTextChangedListener(label_no_edt_stock)

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

        change_stock_auto_tv.setOnClickListener {
            change_stock_auto_tv.text = change_stock_auto_tv.text
            val eText = change_stock_auto_tv.text
            Selection.setSelection(eText, eText.length)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setToolbar()
        productCoilStockViewModel =
            ViewModelProvider(this).get(ProductCoilStockViewModel::class.java)

        coilStockDataBinding = DataBindingUtil.inflate<FragmentCoilStockBinding>(
            inflater,
            R.layout.fragment_coil_stock,
            container,
            false
        ).apply {
            this.viewModel = productCoilStockViewModel
            this.lifecycleOwner = this@ProductCoilStockFragment
        }
        setRecyclerView()

        productCoilStockViewModel.showDatePickerDialogEvent.observe(viewLifecycleOwner, Observer {
            val calendar = Calendar.getInstance()
            val Year = calendar.get(Calendar.YEAR)
            val Month = calendar.get(Calendar.MONTH)
            val Day = calendar.get(Calendar.DAY_OF_MONTH)
            val dateListener =
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    productCoilStockViewModel.inDate.value =
                        "$year / ${String.format("%02d", month + 1)} / ${
                            String.format(
                                "%02d",
                                dayOfMonth
                            )
                        }"
                }
            DatePickerDialog(requireContext(), dateListener, Year, Month, Day).show()
        })

        productCoilStockViewModel.noLabelNo.observe(viewLifecycleOwner, Observer {
            context?.let { view ->
                CustomDialog(view, R.layout.dialog_incorrect)
                    .setTitle(R.string.prompt_notification)
                    .setMessage(R.string.prompt_no_label_no)
                    .setPositiveButton(R.string.dialog_ok) {
                    }.show()
            }
        })

        productCoilStockViewModel.noPosCdNo.observe(viewLifecycleOwner, Observer {
            context?.let { view ->
                CustomDialog(view, R.layout.dialog_incorrect)
                    .setTitle(R.string.prompt_notification)
                    .setMessage(R.string.prompt_no_pos_cd)
                    .setPositiveButton(R.string.dialog_ok) {
                    }.show()
            }
        })

        productCoilStockViewModel.noPosCdMatch.observe(viewLifecycleOwner, Observer {
            context?.let { view ->
                CustomDialog(view, R.layout.dialog_incorrect)
                    .setTitle(R.string.prompt_notification)
                    .setMessage(R.string.prompt_no_pos_cd_match)
                    .setPositiveButton(R.string.dialog_ok) {
                    }.show()
            }
        })

        productCoilStockViewModel.noNetWorkConnect.observe(viewLifecycleOwner, Observer {
            context?.let { view ->
                CustomDialog(view, R.layout.dialog_incorrect)
                    .setTitle(R.string.prompt_error)
                    .setMessage(R.string.network_failed)
                    .setPositiveButton(R.string.dialog_ok) {
                    }.show()
            }
        })

        productCoilStockViewModel.isLoading.observe(viewLifecycleOwner, Observer {
            if (it) {
                Log.d("visibleProgressBarOn", "${productCoilStockViewModel.isLoading.value}")
                activity?.window?.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                );
                progress_bar.visibility = View.VISIBLE
            } else {
                Log.d("visibleProgressBarOff", "${productCoilStockViewModel.isLoading.value}")
                activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                progress_bar.visibility = View.INVISIBLE
                hideKeyboard()
            }
        })

        productCoilStockViewModel.selectDateEvent.observe(viewLifecycleOwner, Observer {
            visibility(true)
        })

        return coilStockDataBinding.root
    }

    private fun visibility(flag: Boolean) {
        if (flag) {
            pos_label_input_layout.visibility = View.VISIBLE
            date_input_layout.visibility = View.GONE
        } else {
            pos_label_input_layout.visibility = View.GONE
            date_input_layout.visibility = View.VISIBLE
        }

    }

    private fun setToolbar() {
        (requireActivity() as MainActivity).toolbar_title.text = getString(R.string.menu_coil_stock)
        (requireActivity() as MainActivity).toolbar_numer.text = ""
        (requireActivity() as MainActivity).toolbar_hyphen.text = ""
        (requireActivity() as MainActivity).toolbar_denom.text = ""
    }

    private fun setRecyclerView() {
        val adapter = ProductCoilStockAdapter(productCoilStockViewModel, requireContext())
        coilStockDataBinding.recyclerView.adapter = adapter

        productCoilStockViewModel.cardItemListDataUpdate.observe(viewLifecycleOwner, Observer {
            if( it != null){
                it.update = 1
                adapter.item.add(0, it)
                adapter.notifyDataSetChanged()
            }
        })

        productCoilStockViewModel.cardItemListDataInsert.observe(viewLifecycleOwner, Observer {
            if( it != null){
                it.update = 0
                adapter.item.add(0, it)
                adapter.notifyDataSetChanged()
            }
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (pos_label_input_layout.visibility == View.VISIBLE) {
                visibility(false)
            } else {
                //(requireActivity() as MainActivity).logout()
            }
        }
    }

    private fun hideKeyboard() {
        val imm =
            activity?.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(input_layout_label.windowToken, 0)
        imm.hideSoftInputFromWindow(input_layout_pos.windowToken, 0)
        imm.hideSoftInputFromWindow(input_layout_date.windowToken, 0)
    }

}