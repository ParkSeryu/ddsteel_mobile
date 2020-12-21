package com.micromos.knp_mobile.ui.productcoilin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.micromos.knp_mobile.R
import com.micromos.knp_mobile.databinding.FragmentCoilInBinding
import com.micromos.knp_mobile.dto.CoilIn
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
        return coilInDataBinding.root
    }

    private fun setRecyclerView() {
        val adapter = ProductCoilinAdapter(requireContext())
        coilInDataBinding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        coilInDataBinding.recyclerView.adapter = adapter

        productCoilInViewModel.coilInData.observe(viewLifecycleOwner, Observer {
            adapter.items = it
            adapter.notifyDataSetChanged()
        })

    }

    private fun hideKeyboard() {
        val imm =
            activity?.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(input_layout.windowToken, 0)
    }
}