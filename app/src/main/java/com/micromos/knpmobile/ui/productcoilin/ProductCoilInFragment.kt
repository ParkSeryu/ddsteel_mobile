package com.micromos.knpmobile.ui.productcoilin

import android.os.Bundle
import android.os.Parcelable
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
import com.micromos.knpmobile.databinding.FragmentCoilInBinding
import com.micromos.knpmobile.ui.home.HomeFragment
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_coil_in.*
import kotlinx.android.synthetic.main.fragment_coil_in.input_layout
import kotlinx.android.synthetic.main.fragment_coil_in.progress_bar
import kotlinx.android.synthetic.main.fragment_coil_in.recyclerView
import java.util.*

class ProductCoilInFragment : Fragment(), ReaderDevice.OnConnectionCompletedListener,
    ReaderDevice.ReaderDeviceListener {

    private lateinit var productCoilInViewModel: ProductCoilInViewModel
    private lateinit var coilInDataBinding: FragmentCoilInBinding
    private lateinit var adapter: ProductCoilInAdapter
    private lateinit var readerDevice: ReaderDevice

    companion object {
        fun newInstance() = ProductCoilInFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        outer_layout_ship.setOnClickListener { hideKeyboard() }
        (requireActivity() as MainActivity).setTextChangedListener(ship_no_edt)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        productCoilInViewModel =
            ViewModelProvider(this).get(ProductCoilInViewModel::class.java)

        coilInDataBinding = DataBindingUtil.inflate<FragmentCoilInBinding>(
            inflater,
            R.layout.fragment_coil_in,
            container,
            false
        ).apply {
            this.viewModel = productCoilInViewModel
            this.lifecycleOwner = this@ProductCoilInFragment
        }

        setRecyclerView()

        productCoilInViewModel.clearInputLayout.observe(viewLifecycleOwner, Observer {
            ship_no_edt.setText("")
        })

        productCoilInViewModel.noRetrieve.observe(viewLifecycleOwner, Observer {
            context?.let { view ->
                CustomDialog(view, R.layout.dialog_incorrect)
                    .setTitle("알림")
                    .setMessage(R.string.prompt_no_retrieve_ship_in)
                    .setPositiveButton(R.string.dialog_ok) {
                    }.show()
            }

        })

        productCoilInViewModel.noNetWorkConnect.observe(viewLifecycleOwner, Observer {
            context?.let { view ->
                CustomDialog(view, R.layout.dialog_incorrect)
                    .setTitle(R.string.prompt_error)
                    .setMessage(R.string.network_failed)
                    .setPositiveButton(R.string.dialog_ok) {
                    }.show()
            }
        })

        productCoilInViewModel.isLoading.observe(viewLifecycleOwner, Observer {
            if (it) {
                activity?.window?.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                );
                progress_bar.visibility = View.VISIBLE
            } else {
                activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                progress_bar.visibility = View.INVISIBLE
                hideKeyboard()
                setToolbar()
            }
        })

        productCoilInViewModel.noLabelNo.observe(viewLifecycleOwner, Observer {
            context?.let { view ->
                CustomDialog(view, R.layout.dialog_incorrect)
                    .setTitle(R.string.prompt_notification)
                    .setMessage(R.string.prompt_no_label_ship_in)
                    .setPositiveButton(R.string.dialog_ok) {
                    }.show()
            }
        })

        productCoilInViewModel.dateTimeOverlap.observe(viewLifecycleOwner, Observer {
            context?.let { view ->
                CustomDialog(view, R.layout.dialog_incorrect)
                    .setTitle(R.string.prompt_notification)
                    .setMessage(R.string.prompt_label_overlap_ship_in)
                    .setPositiveButton(R.string.dialog_ok) {
                    }.show()
            }
        })

        productCoilInViewModel.cardClick.observe(viewLifecycleOwner, Observer {
            ship_no_edt.setText(it)
        })

        productCoilInViewModel.noCompleteAllLabel.observe(viewLifecycleOwner, Observer {
            if ((requireActivity() as MainActivity).toolbar_numer.text != (requireActivity() as MainActivity).toolbar_denom.text) {
                context?.let { view ->
                    CustomDialog(view, R.layout.dialog_request)
                        .setTitle(R.string.prompt_notification)
                        .setMessage(R.string.prompt_label_no_success_ship_in)
                        .setPositiveButton(R.string.dialog_ok) {
                            productCoilInViewModel.prevShipNo.value =
                                productCoilInViewModel._requestNo.value?.trim()
                            productCoilInViewModel.shipNoRetrieve(productCoilInViewModel._requestNo.value)
                        }.setNegativeButton(R.string.dialog_ng) {
                            productCoilInViewModel.successCall()
                        }.show()
                }

            } else {
                productCoilInViewModel.prevShipNo.value =
                    productCoilInViewModel._requestNo.value?.trim()
                productCoilInViewModel.shipNoRetrieve(productCoilInViewModel._requestNo.value)
            }
        })

        productCoilInViewModel.noModifyEvent.observe(viewLifecycleOwner, Observer {
            context?.let { view ->
                CustomDialog(view, R.layout.dialog_incorrect)
                    .setTitle(R.string.prompt_notification)
                    .setMessage(R.string.prompt_label_no_modify_ship_in)
                    .setPositiveButton(R.string.dialog_ok) {
                    }.show()
            }
        })

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (adapter.itemCount != 0) {
                (requireActivity() as MainActivity).replaceFragment(newInstance())
            } else {
                (requireActivity() as MainActivity).replaceFragment(HomeFragment.newInstance())
            }
        }

        readerDevice = ReaderDevice.getMXDevice(context)
        readerDevice.startAvailabilityListening()
        readerDevice.setReaderDeviceListener(this)
        readerDevice.connect(this@ProductCoilInFragment)

        return coilInDataBinding.root
    }

    private fun setRecyclerView() {
        adapter = ProductCoilInAdapter(productCoilInViewModel)
        //var recyclerViewState: Parcelable? = null

        /*productCoilInViewModel.recyclerViewState.observe(viewLifecycleOwner, Observer {
            recyclerViewState = recyclerView.layoutManager?.onSaveInstanceState()
        })*/

        productCoilInViewModel.shipOrderList.observe(viewLifecycleOwner, Observer {
          /*  if (productCoilInViewModel.recyclerViewStateFlag)
                coilInDataBinding.recyclerView.layoutManager?.onRestoreInstanceState(
                    recyclerViewState
                )*/
            coilInDataBinding.recyclerView.adapter = adapter
            if (it != null) {
                adapter.items = it
            } else {
                adapter.items = emptyList()
                adapter.notifyDataSetChanged()
            }
        })
    }

    private fun hideKeyboard() {
        val imm =
            activity?.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(input_layout.windowToken, 0)
    }

    private fun setToolbar() {
        (requireActivity() as MainActivity).toolbar_title.text = getString(R.string.menu_ship_in)
        (requireActivity() as MainActivity).toolbar_numer.text =
            productCoilInViewModel.numerator.toString()
        (requireActivity() as MainActivity).toolbar_hyphen.text = "/"
        (requireActivity() as MainActivity).toolbar_denom.text =
            productCoilInViewModel.denomiator.toString()
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
            if (results.count <= 0 || results.getResultAt(0).readString!!.equals("") || progress_bar.visibility == View.VISIBLE )
                return

            var resultString = results.getResultAt(0).readString!!
            Log.d("scanTest", resultString)
            if (resultString.contains("\\000026")) {
                resultString = resultString.split("\\000026")[1]
            }
            productCoilInViewModel._requestNo.value = resultString.toUpperCase(Locale.ROOT)
            productCoilInViewModel.shipNoRetrieve(productCoilInViewModel._requestNo.value)
        } catch (e: Exception) {
            Toast.makeText(context, "읽은값 : ${e.message.toString()}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        readerDevice.startAvailabilityListening()
        readerDevice.setReaderDeviceListener(this)
        readerDevice.connect(this@ProductCoilInFragment)
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