package com.micromos.ddsteelmobile.ui.productmaterialIn

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.cognex.mobile.barcode.sdk.ReadResults
import com.cognex.mobile.barcode.sdk.ReaderDevice
import com.micromos.ddsteelmobile.CustomDialog
import com.micromos.ddsteelmobile.MainActivity
import com.micromos.ddsteelmobile.R
import com.micromos.ddsteelmobile.databinding.FragmentMaterialInBinding
import com.micromos.ddsteelmobile.dto.GetMaterialCardInfo
import com.micromos.ddsteelmobile.ui.home.HomeFragment
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_coil_in.*
import kotlinx.android.synthetic.main.fragment_material_in.*
import kotlinx.android.synthetic.main.fragment_material_in.progress_bar


class ProductMaterialInFragment : Fragment(), ReaderDevice.OnConnectionCompletedListener,
    ReaderDevice.ReaderDeviceListener {

    private lateinit var productMaterialInViewModel: ProductMaterialInViewModel
    private lateinit var materialCheckDataBinding: FragmentMaterialInBinding
    private lateinit var adapter: ProductMaterialInAdapter
    private lateinit var readerDevice: ReaderDevice

    companion object {
        fun newInstance() = ProductMaterialInFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layout_material_in.setOnClickListener { hideKeyboard() }

        (requireActivity() as MainActivity).setTextChangedListener(trans_car_no)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        productMaterialInViewModel =
            ViewModelProvider(this).get(ProductMaterialInViewModel::class.java)

        materialCheckDataBinding = DataBindingUtil.inflate<FragmentMaterialInBinding>(
            inflater,
            R.layout.fragment_material_in,
            container,
            false
        ).apply {
            this.viewModel = productMaterialInViewModel
            this.lifecycleOwner = this@ProductMaterialInFragment
        }
        setToolbar()

        setRecyclerView()

        productMaterialInViewModel.noNetWorkConnect.observe(viewLifecycleOwner, Observer {
            context?.let { view ->
                CustomDialog(view, R.layout.dialog_incorrect)
                    .setTitle(R.string.prompt_error)
                    .setMessage(R.string.network_failed)
                    .setPositiveButton(R.string.dialog_ok) {
                    }.show()
            }
        })

        productMaterialInViewModel.notCompleteInformation.observe(viewLifecycleOwner, Observer {
            context?.let { view ->
                CustomDialog(view, R.layout.dialog_incorrect)
                    .setTitle(R.string.prompt_notice)
                    .setMessage(R.string.prompt_not_complete_in)
                    .setPositiveButton(R.string.dialog_ok) {
                    }.show()
            }
        })

        productMaterialInViewModel.noMatchLabel.observe(viewLifecycleOwner, Observer {
            context?.let { view ->
                CustomDialog(view, R.layout.dialog_incorrect)
                    .setTitle(R.string.prompt_notice)
                    .setMessage(R.string.prompt_no_match_mill_no)
                    .setPositiveButton(R.string.dialog_ok) {
                    }.show()
            }
        })

        productMaterialInViewModel.duplicateLabel.observe(viewLifecycleOwner, Observer {
            Toast.makeText(context, "동일한 MILL_NO 존재하여 처리 불가합니다.", Toast.LENGTH_LONG).show()
        })

        productMaterialInViewModel.commonErrorEvent.observe(viewLifecycleOwner, Observer{
            context?.let { view ->
                CustomDialog(view, R.layout.dialog_incorrect)
                    .setTitle(R.string.prompt_notice)
                    .setMessage(String.format(getString(R.string.prompt_common_error), productMaterialInViewModel.errorCode))
                    .setPositiveButton(R.string.dialog_ok) {
                    }.show()
            }
        })

        productMaterialInViewModel.clearInputLayout.observe(viewLifecycleOwner, Observer {
            label_no.setText("")
        })

        productMaterialInViewModel.focusChangeEvent.observe(viewLifecycleOwner, Observer {
            //input_layout_trans_car_no.requestFocus()
            //Toast.makeText(context, "test", Toast.LENGTH_LONG).show()
            trans_car_no.requestFocus()
        })

        productMaterialInViewModel.isLoading.observe(viewLifecycleOwner, Observer {
            if (it) {
                Log.d("visible", "${productMaterialInViewModel.isLoading.value}")
                activity?.window?.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                );
                progress_bar.visibility = View.VISIBLE
            } else {
                Log.d("visible", "${productMaterialInViewModel.isLoading.value}")
                activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                progress_bar.visibility = View.INVISIBLE
                hideKeyboard()
            }
        })

        productMaterialInViewModel.requestInNoFailedEvent.observe(viewLifecycleOwner, Observer {
            context?.let { view ->
                CustomDialog(view, R.layout.dialog_incorrect)
                    .setTitle(R.string.prompt_notification)
                    .setMessage(R.string.prompt_fail_get_in_no)
                    .setPositiveButton(R.string.dialog_ok) {
                    }.show()
            }
        })

        productMaterialInViewModel.updateLabelInFailedEvent.observe(viewLifecycleOwner, Observer {
            context?.let { view ->
                CustomDialog(view, R.layout.dialog_incorrect)
                    .setTitle(R.string.prompt_notification)
                    .setMessage(R.string.prompt_fail_update_label)
                    .setPositiveButton(R.string.dialog_ok) {
                    }.show()
            }
        })

        productMaterialInViewModel.getCardInfoFailedEvent.observe(viewLifecycleOwner, Observer {
            context?.let { view ->
                CustomDialog(view, R.layout.dialog_incorrect)
                    .setTitle(R.string.prompt_notification)
                    .setMessage(R.string.prompt_fail_get_card_info)
                    .setPositiveButton(R.string.dialog_ok) {
                    }.show()
            }
        })

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (productMaterialInViewModel.endFlag) {
                (requireActivity() as MainActivity).replaceFragment(HomeFragment.newInstance())
            } else {
                // 허수 생기기 방지 ( 완료가 되지 않았을때 )
                CustomDialog(requireContext(), R.layout.dialog_app_finish)
                    .setMessage(R.string.prompt_not_finished)
                    .setPositiveButton("예") {
                        (requireActivity() as MainActivity).replaceFragment(HomeFragment.newInstance())
                    }.setNegativeButton("아니오") {
                    }.show()
            }
        }

        readerDevice = ReaderDevice.getMXDevice(context)
        readerDevice.startAvailabilityListening()
        readerDevice.setReaderDeviceListener(this)
        readerDevice.connect(this@ProductMaterialInFragment)

        return materialCheckDataBinding.root
    }

    private fun setRecyclerView() {
        adapter = ProductMaterialInAdapter(productMaterialInViewModel)
        materialCheckDataBinding.recyclerView.adapter = adapter
        //var recyclerViewState: Parcelable? = null

        /*productCoilInViewModel.recyclerViewState.observe(viewLifecycleOwner, Observer {
            recyclerViewSt ate = recyclerView.layoutManager?.onSaveInstanceState()
        })*/

        productMaterialInViewModel.cardInfo.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                adapter.items.add(0, it)
                adapter.notifyItemChanged(0)
            } else {
                //mutableListOf<GetMaterialCardInfo>()
                adapter.items = mutableListOf()
                adapter.notifyDataSetChanged()
            }
        })
    }

    private fun hideKeyboard() {
        val imm =
            activity?.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(input_layout_label.windowToken, 0)

    }

    private fun setToolbar() {
        (requireActivity() as MainActivity).toolbar_title.text =
            getString(R.string.menu_material_in)
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

    override fun onReadResultReceived(
        readerDevice: ReaderDevice?,
        results: ReadResults
    ) {
        try {
            if (results.count <= 0 || results.getResultAt(0).readString!! == "" || progress_bar.visibility == View.VISIBLE)
                return

            val resultString = results.getResultAt(0).readString!!
            Log.d("scanTest", resultString)
//            if (resultString.contains("\\000026")) {
//                resultString = resultString.split("\\000026")[1]
//            }
//            if (resultString.contains("http")) {
//                productMaterialInViewModel.labelNoHyundai.value = resultString
//                productMaterialInViewModel.hyundaiLabelRetrieve(productMaterialInViewModel.labelNoHyundai.value)
//            } else {
//                productMaterialInViewModel.labelNoKnp.value = resultString
//                productMaterialInViewModel.knpLabelRetrieve(productMaterialInViewModel.labelNoKnp.value)
//            }

        } catch (e: Exception) {
            Toast.makeText(context, "읽은값 : ${e.message.toString()}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        readerDevice.startAvailabilityListening()
        readerDevice.setReaderDeviceListener(this)
        readerDevice.connect(this@ProductMaterialInFragment)
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
            productMaterialInViewModel.screenOrientation()
        } else {
            productMaterialInViewModel.screenOrientation()
        }
    }
}