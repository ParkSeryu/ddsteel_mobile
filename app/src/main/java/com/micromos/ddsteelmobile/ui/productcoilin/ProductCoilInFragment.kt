package com.micromos.ddsteelmobile.ui.productcoilin

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
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
import com.micromos.ddsteelmobile.CustomDialog
import com.micromos.ddsteelmobile.M3Receiver
import com.micromos.ddsteelmobile.MainActivity
import com.micromos.ddsteelmobile.R
import com.micromos.ddsteelmobile.databinding.FragmentCoilInBinding
import com.micromos.ddsteelmobile.ui.home.HomeFragment
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_coil_in.*
import java.util.*

class ProductCoilInFragment : Fragment(), M3Receiver.ScanListener {

    private lateinit var productCoilInViewModel: ProductCoilInViewModel
    private lateinit var coilInDataBinding: FragmentCoilInBinding
    private lateinit var adapter: ProductCoilInAdapter
    private val m3Receiver: M3Receiver by lazy { M3Receiver.getInstance() }


    companion object {
        fun newInstance() = ProductCoilInFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        outer_layout_ship.setOnClickListener { hideKeyboard() }
        ship_no_edt.requestFocus()
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

//        productCoilInViewModel.clearInputLayout.observe(viewLifecycleOwner, Observer {
//            ship_no_edt.setText("")
//        })

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

        productCoilInViewModel.unExceptedError.observe(viewLifecycleOwner, Observer {
            context?.let { view ->
                CustomDialog(view, R.layout.dialog_incorrect)
                    .setTitle(R.string.prompt_error)
                    .setMessage(R.string.unexpected_error_pda_in)
                    .setPositiveButton(R.string.dialog_ok) {
                    }.show()
            }
        })

        productCoilInViewModel.clickRemarkButtonEvent.observe(viewLifecycleOwner, Observer{
            context?.let{view ->
                productCoilInViewModel.remark.value?.let { message ->
                    CustomDialog(view, R.layout.dialog_default)
                        .setMessage(message)
                        .setPositiveButton(R.string.dialog_ok) {
                        }.show()
                }
            }
        })

        productCoilInViewModel.isLoading.observe(viewLifecycleOwner, Observer {
            if (it) {
                activity?.window?.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                );
                ship_no_edt.isEnabled = false
                progress_bar.visibility = View.VISIBLE
            } else {
                activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                progress_bar.visibility = View.INVISIBLE
                ship_no_edt.isEnabled = true
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

        productCoilInViewModel.noStockEvent.observe(viewLifecycleOwner, Observer {
            context?.let { view ->
                CustomDialog(view, R.layout.dialog_incorrect)
                    .setTitle(R.string.prompt_notification)
                    .setMessage(R.string.prompt_no_stock_ship_in)
                    .setPositiveButton(R.string.dialog_ok) {
                    }.show()
            }
        })

        productCoilInViewModel.commonErrorEvent.observe(viewLifecycleOwner, Observer {
            context?.let { view ->
                CustomDialog(view, R.layout.dialog_incorrect)
                    .setTitle(R.string.prompt_notification)
                    .setMessage(
                        String.format(
                            getString(R.string.prompt_common_error),
                            productCoilInViewModel.errorCode
                        )
                    )
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

        productCoilInViewModel.onClickScanButton.observe(viewLifecycleOwner, Observer {
//            val intent = Intent(
//                context, ScanProductCoilInActivity::class.java
//            )
//            startActivity(intent)
        })

        productCoilInViewModel.forcedUpdateEvent.observe(viewLifecycleOwner, Observer {
            context?.let { view ->
                CustomDialog(view, R.layout.dialog_request)
                    .setTitle(R.string.prompt_notification)
                    .setMessage(R.string.prompt_label_forced_update)
                    .setPositiveButton(R.string.dialog_ok) {
                        productCoilInViewModel.forcedUpdate()
                    }.setNegativeButton(R.string.dialog_ng) {
                        productCoilInViewModel.successCall()
                    }.show()
            }
        })

        productCoilInViewModel.rollbackUpdateEvent.observe(viewLifecycleOwner, Observer {
            context?.let { view ->
                CustomDialog(view, R.layout.dialog_request)
                    .setTitle(R.string.prompt_notification)
                    .setMessage(R.string.prompt_label_update_rollback)
                    .setPositiveButton(R.string.dialog_ok) {
                        productCoilInViewModel.rollbackUpdate()
                    }.setNegativeButton(R.string.dialog_ng) {
                        productCoilInViewModel.successCall()
                    }.show()
            }
        })

        productCoilInViewModel.notInputShipNoEvent.observe(viewLifecycleOwner, Observer {
            context?.let { view ->
                CustomDialog(view, R.layout.dialog_incorrect)
                    .setTitle(R.string.prompt_notification)
                    .setMessage(R.string.prompt_not_input_ship_no)
                    .setPositiveButton(R.string.dialog_ok) {
                    }.show()
            }
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

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (adapter.itemCount != 0) {
                (requireActivity() as MainActivity).replaceFragment(newInstance())
            } else {
                (requireActivity() as MainActivity).replaceFragment(HomeFragment.newInstance())
            }
        }

        return coilInDataBinding.root
    }


    private fun setRecyclerView() {
        adapter = ProductCoilInAdapter(productCoilInViewModel)
        coilInDataBinding.recyclerView.adapter = adapter
        productCoilInViewModel.shipOrderList.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                adapter.items = it
                if (productCoilInViewModel.changePosition.value == null) {
                    adapter.notifyDataSetChanged()
                } else {
                    adapter.notifyItemChanged(
                        productCoilInViewModel.changePosition.value!!
                    )
                   coilInDataBinding.recyclerView.scrollToPosition(productCoilInViewModel.changePosition.value!!)
                }
            } else {
                adapter.items = emptyList()
            }
            //it?.let { adapter.submitList(it.toMutableList()) }
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
            productCoilInViewModel.numerator.value.toString()
        (requireActivity() as MainActivity).toolbar_hyphen.text = "/"
        (requireActivity() as MainActivity).toolbar_denom.setTextColor(Color.RED)
        (requireActivity() as MainActivity).toolbar_denom.text =
            productCoilInViewModel.denomiator.value.toString()

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            productCoilInViewModel.screenOrientation()
        } else {
            productCoilInViewModel.screenOrientation()
        }
    }

    override fun onScan(scanResult: String) {
        if (progress_bar.visibility == View.VISIBLE || scanResult.toCharArray().isEmpty())
            return

        val resultScan = scanResult.toUpperCase(Locale.ROOT)

        ship_no_edt.setText(resultScan)
        productCoilInViewModel.shipNoRetrieve(resultScan)
    }

    override fun onResume() {
        super.onResume()
        Log.d("onResumeCoilIn", "onResumeCoilIn")
        m3Receiver.register(this)
    }

    override fun onPause() {
        super.onPause()
        Log.d("onPauseCoilIn", "onPauseCoilIn")
        m3Receiver.unRegister()
    }
}