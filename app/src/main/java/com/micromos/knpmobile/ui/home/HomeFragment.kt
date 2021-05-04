package com.micromos.knpmobile.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.micromos.knpmobile.EventObserver
import com.micromos.knpmobile.MainActivity
import com.micromos.knpmobile.R
import com.micromos.knpmobile.databinding.FragmentHomeBinding
import com.micromos.knpmobile.ui.productMaterialCheck.ProductMaterialCheckFragment
import com.micromos.knpmobile.ui.productchangepos.ProductChangePosFragment
import com.micromos.knpmobile.ui.productcoilin.ProductCoilInFragment
import com.micromos.knpmobile.ui.productcoilout.ProductCoilOutFragment
import com.micromos.knpmobile.ui.productstockcheck.ProductStockCheckFragment
import kotlinx.android.synthetic.main.app_bar_main.*

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var homeViewDataBinding: FragmentHomeBinding

    companion object {
        fun newInstance() = HomeFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        homeViewDataBinding = DataBindingUtil.inflate<FragmentHomeBinding>(
            inflater,
            R.layout.fragment_home,
            container,
            false
        ).apply {
            this.viewModel = homeViewModel
            this.lifecycleOwner = this@HomeFragment
        }
        setToolbar()
        setRecyclerView()

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            (requireActivity() as MainActivity).logout()
        }

        homeViewModel.goToAnotherView.observe(viewLifecycleOwner, EventObserver {
            when (it) {
                MenuItem.ProductCoilIn -> {
                    (requireActivity() as MainActivity).replaceFragment(ProductCoilInFragment.newInstance())
                }
                MenuItem.ProductCoilOut -> {
                    (requireActivity() as MainActivity).replaceFragment(ProductCoilOutFragment.newInstance())
                }
                MenuItem.ProductStockCheck -> {
                    (requireActivity() as MainActivity).replaceFragment(ProductStockCheckFragment.newInstance())
                }
                MenuItem.ProductChangePos -> {
                    (requireActivity() as MainActivity).replaceFragment(ProductChangePosFragment.newInstance())
                }
                MenuItem.ProductMaterialCheck -> {
                    (requireActivity() as MainActivity).replaceFragment(ProductMaterialCheckFragment.newInstance())
                }
            }
        })

        return homeViewDataBinding.root
    }

    private fun setToolbar() {
        (requireActivity() as MainActivity).toolbar_title.text = getString(R.string.app_name)
        (requireActivity() as MainActivity).toolbar_numer.text = ""
        (requireActivity() as MainActivity).toolbar_hyphen.text = ""
        (requireActivity() as MainActivity).toolbar_denom.text = ""
    }

    private fun setRecyclerView() {
        val adapter = HomeAdapterMenu(homeViewModel)
        homeViewDataBinding.menuRecyclerView.adapter = adapter
        homeViewDataBinding.menuRecyclerView.hasFixedSize() // 성능 향상
        homeViewModel.menuItemList.observe(viewLifecycleOwner, Observer {
            adapter.items = it
        }
        )
    }
}