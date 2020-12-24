package com.micromos.knpmobile.ui.productcoilout

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.micromos.knpmobile.CustomDialog
import com.micromos.knpmobile.MainActivity
import com.micromos.knpmobile.R
import com.micromos.knpmobile.databinding.FragmentCoilOutBinding
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_coil_in.*

class ProductCoilOutFragment : Fragment() {

    private lateinit var productCoilOutViewModel: ProductCoilOutViewModel
    private lateinit var coilOutDataBinding: FragmentCoilOutBinding

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
                Log.d("visib", "${productCoilOutViewModel.isLoading.value}")
                activity?.getWindow()?.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                );
                progress_bar.visibility = View.VISIBLE
            } else {
                Log.d("visib", "${productCoilOutViewModel.isLoading.value}")
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
                            productCoilOutViewModel.shipRetrieve(productCoilOutViewModel._requestNo.value)
                        }.setNegativeButton(R.string.dialog_ng) {
                            productCoilOutViewModel.successCall()
                        }.show()
                }
            } else {
                productCoilOutViewModel.prevShipNo.value =
                    productCoilOutViewModel._requestNo.value?.trim()
                productCoilOutViewModel.shipRetrieve(productCoilOutViewModel._requestNo.value)
            }
        })
        return coilOutDataBinding.root
    }

    private fun setRecyclerView() {
        val adapter = ProductCoilinAdapter(productCoilOutViewModel, requireContext())
        var recyclerViewState: Parcelable? = null

        productCoilOutViewModel._recyclerViewState.observe(viewLifecycleOwner, Observer {
            recyclerViewState = recyclerView.layoutManager?.onSaveInstanceState()
        })

        productCoilOutViewModel.coilInData.observe(viewLifecycleOwner, Observer {
            Log.d("ttt", "${productCoilOutViewModel.recyclerViewStateFlag}")

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

    fun setToolbar() {
        (requireActivity() as MainActivity).toolbar_numer.text =
            productCoilOutViewModel.numerator.toString()
        (requireActivity() as MainActivity).toolbar_hyphen.text = "/"
        (requireActivity() as MainActivity).toolbar_denom.text =
            productCoilOutViewModel.denomiator.toString()
    }

}