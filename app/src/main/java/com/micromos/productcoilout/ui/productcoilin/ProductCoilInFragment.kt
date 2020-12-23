package com.micromos.productcoilout.ui.productcoilin

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
import com.micromos.productcoilout.CustomDialog
import com.micromos.productcoilout.MainActivity
import com.micromos.productcoilout.R
import com.micromos.productcoilout.databinding.FragmentCoilInBinding
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_coil_in.*

class ProductCoilInFragment : Fragment() {

    private lateinit var productCoilInViewModel: ProductCoilInViewModel
    private lateinit var coilInDataBinding: FragmentCoilInBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        outer_layout_ship.setOnClickListener { hideKeyboard() }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

        productCoilInViewModel.noRetrieve.observe(viewLifecycleOwner, Observer {
            context?.let { view ->
                CustomDialog(view, R.layout.dialog_incorrect)
                    .setTitle("알림")
                    .setMessage(R.string.prompt_no_retrieve)
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
                Log.d("visib", "${productCoilInViewModel.isLoading.value}")
                activity?.getWindow()?.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                );
                progress_bar.visibility = View.VISIBLE
            } else {
                Log.d("visib", "${productCoilInViewModel.isLoading.value}")
                activity?.getWindow()?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                progress_bar.visibility = View.INVISIBLE
                hideKeyboard()
                setToolbar()
            }
        })

        productCoilInViewModel.noLabelNo.observe(viewLifecycleOwner, Observer {
            context?.let { view ->
                CustomDialog(view, R.layout.dialog_incorrect)
                    .setTitle(R.string.prompt_notification)
                    .setMessage(R.string.prompt_no_label)
                    .setPositiveButton(R.string.dialog_ok) {
                    }.show()
            }
        })

        productCoilInViewModel.dateTimeOverlap.observe(viewLifecycleOwner, Observer {
            context?.let { view ->
                CustomDialog(view, R.layout.dialog_incorrect)
                    .setTitle(R.string.prompt_notification)
                    .setMessage(R.string.prompt_label_overlap)
                    .setPositiveButton(R.string.dialog_ok) {
                    }.show()
            }
        })

        productCoilInViewModel._test.observe(viewLifecycleOwner, Observer {
            ship_no_edt.setText(it)
        })


        return coilInDataBinding.root
    }

    private fun setRecyclerView() {
        val adapter = ProductCoilinAdapter(productCoilInViewModel, requireContext())
        var recyclerViewState : Parcelable? = null

        productCoilInViewModel._recyclerViewState.observe(viewLifecycleOwner, Observer {
             recyclerViewState = recyclerView.layoutManager?.onSaveInstanceState()
        })

        productCoilInViewModel.coilInData.observe(viewLifecycleOwner, Observer {
            Log.d("ttt", "${productCoilInViewModel.recyclerViewStateFlag}")

            if(productCoilInViewModel.recyclerViewStateFlag)
            coilInDataBinding.recyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState)
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

    fun setToolbar() {
        (requireActivity() as MainActivity).toolbar_numer.text = productCoilInViewModel.numerator.toString()
        (requireActivity() as MainActivity).toolbar_hyphen.text = "/"
        (requireActivity() as MainActivity).toolbar_denom.text = productCoilInViewModel.denomiator.toString()
    }
}