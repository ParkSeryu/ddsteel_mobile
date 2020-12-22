package com.micromos.knp_mobile.ui.productcoilin

import android.os.Bundle
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.micromos.knp_mobile.CustomDialog
import com.micromos.knp_mobile.R
import com.micromos.knp_mobile.databinding.FragmentCoilInBinding
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

            if(it){
                Log.d("visib", "${productCoilInViewModel.isLoading.value}")
                activity?.getWindow()?.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                progress_bar.visibility = View.VISIBLE
            }
            else{
                Log.d("visib", "${productCoilInViewModel.isLoading.value}")
                activity?.getWindow()?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                progress_bar.visibility = View.INVISIBLE
                hideKeyboard()
            }
        })


        return coilInDataBinding.root
    }

    private fun setRecyclerView() {
        val adapter = ProductCoilinAdapter(requireContext())
        coilInDataBinding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        coilInDataBinding.recyclerView.adapter = adapter

        productCoilInViewModel.coilInData.observe(viewLifecycleOwner, Observer {
            if(it != null) {
                adapter.items = it
            }
            else{
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
}