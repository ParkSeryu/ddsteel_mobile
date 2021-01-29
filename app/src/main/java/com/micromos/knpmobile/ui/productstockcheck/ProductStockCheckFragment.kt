package com.micromos.knpmobile.ui.productstockcheck

import android.app.DatePickerDialog
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
import com.micromos.knpmobile.R
import com.micromos.knpmobile.databinding.FragmentCoilStockBinding
import com.micromos.knpmobile.ui.home.HomeFragment
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_coil_stock.*
import kotlinx.android.synthetic.main.fragment_coil_stock.change_stock_auto_tv
import kotlinx.android.synthetic.main.fragment_coil_stock.progress_bar
import kotlinx.android.synthetic.main.fragment_coil_stock.recyclerView
import java.util.*

class ProductStockCheckFragment : Fragment(), ReaderDevice.OnConnectionCompletedListener,
    ReaderDevice.ReaderDeviceListener {

    private lateinit var productStockCheckViewModel: ProductStockCheckViewModel
    private lateinit var coilStockDataBinding: FragmentCoilStockBinding
    private lateinit var adapter: ProductCoilStockAdapter
    private lateinit var readerDevice: ReaderDevice

    companion object {
        fun newInstance() = ProductStockCheckFragment()
    }


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
        productStockCheckViewModel =
            ViewModelProvider(this).get(ProductStockCheckViewModel::class.java)

        coilStockDataBinding = DataBindingUtil.inflate<FragmentCoilStockBinding>(
            inflater,
            R.layout.fragment_coil_stock,
            container,
            false
        ).apply {
            this.viewModel = productStockCheckViewModel
            this.lifecycleOwner = this@ProductStockCheckFragment
        }
        setRecyclerView()

        productStockCheckViewModel.showDatePickerDialogEvent.observe(viewLifecycleOwner, Observer {
            val calendar = Calendar.getInstance()
            val Year = calendar.get(Calendar.YEAR)
            val Month = calendar.get(Calendar.MONTH)
            val Day = calendar.get(Calendar.DAY_OF_MONTH)
            val dateListener =
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    productStockCheckViewModel.inDate.value =
                        "$year / ${String.format("%02d", month + 1)} / ${
                            String.format(
                                "%02d",
                                dayOfMonth
                            )
                        }"
                }
            DatePickerDialog(requireContext(), dateListener, Year, Month, Day).show()
        })

        productStockCheckViewModel.noLabelNo.observe(viewLifecycleOwner, Observer {
            context?.let { view ->
                CustomDialog(view, R.layout.dialog_incorrect)
                    .setTitle(R.string.prompt_notification)
                    .setMessage(R.string.prompt_no_label_no)
                    .setPositiveButton(R.string.dialog_ok) {
                    }.show()
            }
        })

        productStockCheckViewModel.noPosCdNo.observe(viewLifecycleOwner, Observer {
            context?.let { view ->
                CustomDialog(view, R.layout.dialog_incorrect)
                    .setTitle(R.string.prompt_notification)
                    .setMessage(R.string.prompt_no_pos_cd)
                    .setPositiveButton(R.string.dialog_ok) {
                    }.show()
            }
        })

        productStockCheckViewModel.noPosCdMatch.observe(viewLifecycleOwner, Observer {
            context?.let { view ->
                CustomDialog(view, R.layout.dialog_incorrect)
                    .setTitle(R.string.prompt_notification)
                    .setMessage(R.string.prompt_no_pos_cd_match)
                    .setPositiveButton(R.string.dialog_ok) {
                    }.show()
            }
        })

        productStockCheckViewModel.noNetWorkConnect.observe(viewLifecycleOwner, Observer {
            context?.let { view ->
                CustomDialog(view, R.layout.dialog_incorrect)
                    .setTitle(R.string.prompt_error)
                    .setMessage(R.string.network_failed)
                    .setPositiveButton(R.string.dialog_ok) {
                    }.show()
            }
        })

        productStockCheckViewModel.isLoading.observe(viewLifecycleOwner, Observer {
            if (it) {
                Log.d("visibleProgressBarOn", "${productStockCheckViewModel.isLoading.value}")
                activity?.window?.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                );
                progress_bar.visibility = View.VISIBLE
            } else {
                Log.d("visibleProgressBarOff", "${productStockCheckViewModel.isLoading.value}")
                activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                progress_bar.visibility = View.INVISIBLE
                hideKeyboard()
            }
        })

        productStockCheckViewModel.selectDateEvent.observe(viewLifecycleOwner, Observer {
            visibility(true)
        })

        productStockCheckViewModel.clearInputLayout.observe(viewLifecycleOwner, Observer {
            label_no_edt_stock.setText("")
            label_no_edt_stock.requestFocus()
        })

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (adapter.itemCount == 0) {
                if (pos_label_input_layout.visibility == View.VISIBLE) {
                    visibility(false)
                } else {
                    (requireActivity() as MainActivity).replaceFragment(HomeFragment.newInstance())
                }
            } else {
                adapter.item.clear()
                coilStockDataBinding.changeStockAutoTv.setText("")
                coilStockDataBinding.labelNoEdtStock.setText("")
                coilStockDataBinding.changeStockAutoTv.requestFocus()
                coilStockDataBinding.changeStockAutoTv.dismissDropDown()
                adapter.notifyDataSetChanged()
            }
        }

        readerDevice = ReaderDevice.getMXDevice(context)
        readerDevice.startAvailabilityListening()
        readerDevice.setReaderDeviceListener(this)
        readerDevice.connect(this@ProductStockCheckFragment)

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
        adapter = ProductCoilStockAdapter(productStockCheckViewModel, requireContext())
        coilStockDataBinding.recyclerView.adapter = adapter

        productStockCheckViewModel.cardItemListDataUpdate.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                it.updateFlag = 1
                adapter.item.add(0, it)
                adapter.notifyDataSetChanged()
            }
        })

        productStockCheckViewModel.cardItemListDataInsert.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                it.updateFlag = 0
                adapter.item.add(0, it)
                adapter.notifyDataSetChanged()
            }
        })
    }

    private fun hideKeyboard() {
        val imm =
            activity?.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(input_layout_label.windowToken, 0)
        imm.hideSoftInputFromWindow(input_layout_pos.windowToken, 0)
        imm.hideSoftInputFromWindow(input_layout_date.windowToken, 0)
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
            if (results.count <= 0 || results.getResultAt(0).readString!!.equals("") || progress_bar.visibility == View.VISIBLE  || date_input_layout.visibility == View.VISIBLE)
                return

            var resultString = results.getResultAt(0).readString!!
            Log.d("scanTest", resultString)
            if (resultString.contains("\\000026")) {
                resultString = resultString.split("\\000026")[1]
            }
            productStockCheckViewModel.labelNo.value = resultString.toUpperCase(Locale.ROOT)
            productStockCheckViewModel.labelRetrieve()

        } catch (e: Exception) {
            Toast.makeText(context, "읽은값 : ${e.message.toString()}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        readerDevice.startAvailabilityListening()
        readerDevice.setReaderDeviceListener(this)
        readerDevice.connect(this@ProductStockCheckFragment)
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