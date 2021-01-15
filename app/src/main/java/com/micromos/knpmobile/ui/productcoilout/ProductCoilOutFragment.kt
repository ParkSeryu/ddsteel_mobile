package com.micromos.knpmobile.ui.productcoilout

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
import com.micromos.knpmobile.databinding.FragmentCoilOutBinding
import com.micromos.knpmobile.ui.home.HomeFragment
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_coil_in.*
import kotlinx.android.synthetic.main.fragment_coil_out.input_layout
import kotlinx.android.synthetic.main.fragment_coil_out.outer_layout_ship
import kotlinx.android.synthetic.main.fragment_coil_out.progress_bar
import kotlinx.android.synthetic.main.fragment_coil_out.recyclerView
import kotlinx.android.synthetic.main.fragment_coil_out.ship_no_edt
import java.util.*

class ProductCoilOutFragment : Fragment(), ReaderDevice.OnConnectionCompletedListener,
    ReaderDevice.ReaderDeviceListener {

    private lateinit var productCoilOutViewModel: ProductCoilOutViewModel
    private lateinit var coilOutDataBinding: FragmentCoilOutBinding
    private lateinit var adapter: ProductCoilOutAdapter
    private lateinit var readerDevice: ReaderDevice

    companion object {
        fun newInstance() = ProductCoilOutFragment()
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
        productCoilOutViewModel =
            ViewModelProvider(this).get(ProductCoilOutViewModel::class.java)

        coilOutDataBinding = DataBindingUtil.inflate<FragmentCoilOutBinding>(
            inflater,
            R.layout.fragment_coil_out,
            container,
            false
        ).apply {
            this.viewModel = productCoilOutViewModel
            this.lifecycleOwner = this@ProductCoilOutFragment
        }
        setRecyclerView()

        productCoilOutViewModel.noRetrieve.observe(viewLifecycleOwner, Observer {
            context?.let { view ->
                CustomDialog(view, R.layout.dialog_incorrect)
                    .setTitle("알림")
                    .setMessage(R.string.prompt_no_retrieve_ship_out)
                    .setPositiveButton(R.string.dialog_ok) {
                    }.show()
            }

        })

        productCoilOutViewModel.noNetWorkConnect.observe(viewLifecycleOwner, Observer {
            context?.let { view ->
                CustomDialog(view, R.layout.dialog_incorrect)
                    .setTitle(R.string.prompt_error)
                    .setMessage(R.string.network_failed)
                    .setPositiveButton(R.string.dialog_ok) {
                    }.show()
            }
        })

        productCoilOutViewModel.isLoading.observe(viewLifecycleOwner, Observer {
            if (it) {
                Log.d("visibility", "${productCoilOutViewModel.isLoading.value}")
                activity?.getWindow()?.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                );
                progress_bar.visibility = View.VISIBLE
            } else {
                Log.d("visibility", "${productCoilOutViewModel.isLoading.value}")
                activity?.getWindow()?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                progress_bar.visibility = View.INVISIBLE
                hideKeyboard()
                setToolbar()
            }
        })

        productCoilOutViewModel.noLabelNo.observe(viewLifecycleOwner, Observer {
            context?.let { view ->
                CustomDialog(view, R.layout.dialog_incorrect)
                    .setTitle(R.string.prompt_notification)
                    .setMessage(R.string.prompt_no_label_ship_out)
                    .setPositiveButton(R.string.dialog_ok) {
                    }.show()
            }
        })

        productCoilOutViewModel.noModifyEvent.observe(viewLifecycleOwner, Observer {
            context?.let { view ->
                CustomDialog(view, R.layout.dialog_incorrect)
                    .setTitle(R.string.prompt_notification)
                    .setMessage(R.string.prompt_label_no_modify_ship_out)
                    .setPositiveButton(R.string.dialog_ok) {
                    }.show()
            }
        })

        productCoilOutViewModel.dateTimeOverlap.observe(viewLifecycleOwner, Observer {
            context?.let { view ->
                CustomDialog(view, R.layout.dialog_incorrect)
                    .setTitle(R.string.prompt_notification)
                    .setMessage(R.string.prompt_label_overlap_ship_out)
                    .setPositiveButton(R.string.dialog_ok) {
                    }.show()
            }
        })

        productCoilOutViewModel.noCompleteCoilIn.observe(viewLifecycleOwner, Observer {
            context?.let { view ->
                CustomDialog(view, R.layout.dialog_incorrect)
                    .setTitle(R.string.prompt_notification)
                    .setMessage(R.string.prompt_no_complete_coil_out)
                    .setPositiveButton(R.string.dialog_ok) {
                    }.show()
            }
        })

        productCoilOutViewModel.test.observe(viewLifecycleOwner, Observer {
            ship_no_edt.setText(it)
        })

        productCoilOutViewModel.noCompleteAllLabel.observe(viewLifecycleOwner, Observer {
            if ((requireActivity() as MainActivity).toolbar_numer.text != (requireActivity() as MainActivity).toolbar_denom.text) {
                context?.let { view ->
                    CustomDialog(view, R.layout.dialog_request)
                        .setTitle(R.string.prompt_notification)
                        .setMessage(R.string.prompt_label_no_success_ship_out)
                        .setPositiveButton(R.string.dialog_ok) {
                            productCoilOutViewModel.prevShipNo.value =
                                productCoilOutViewModel._requestNo.value?.trim()
                            productCoilOutViewModel.shipNoRetrieve(productCoilOutViewModel._requestNo.value)
                        }.setNegativeButton(R.string.dialog_ng) {
                            productCoilOutViewModel.successCall()
                        }.show()
                }
            } else {
                productCoilOutViewModel.prevShipNo.value =
                    productCoilOutViewModel._requestNo.value?.trim()
                productCoilOutViewModel.shipNoRetrieve(productCoilOutViewModel._requestNo.value)
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
        readerDevice.connect(this@ProductCoilOutFragment)

        return coilOutDataBinding.root
    }

    private fun setRecyclerView() {
        adapter = ProductCoilOutAdapter(productCoilOutViewModel)
        var recyclerViewState: Parcelable? = null

        productCoilOutViewModel._recyclerViewState.observe(viewLifecycleOwner, Observer {
            recyclerViewState = recyclerView.layoutManager?.onSaveInstanceState()
        })

        productCoilOutViewModel.shipOrderList.observe(viewLifecycleOwner, Observer {
            if (productCoilOutViewModel.recyclerViewStateFlag)
                coilOutDataBinding.recyclerView.layoutManager?.onRestoreInstanceState(
                    recyclerViewState
                )
            coilOutDataBinding.recyclerView.adapter = adapter
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
        (requireActivity() as MainActivity).toolbar_title.text = getString(R.string.menu_ship_out)
        (requireActivity() as MainActivity).toolbar_numer.text =
            productCoilOutViewModel.numerator.toString()
        (requireActivity() as MainActivity).toolbar_hyphen.text = "/"
        (requireActivity() as MainActivity).toolbar_denom.text =
            productCoilOutViewModel.denomiator.toString()
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
            productCoilOutViewModel._requestNo.value = resultString.toUpperCase(Locale.ROOT)
            productCoilOutViewModel.shipNoRetrieve(productCoilOutViewModel._requestNo.value)
        } catch (e: Exception) {
            Toast.makeText(context, "읽은값 : ${e.message.toString()}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        readerDevice.startAvailabilityListening()
        readerDevice.setReaderDeviceListener(this)
        readerDevice.connect(this@ProductCoilOutFragment)
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