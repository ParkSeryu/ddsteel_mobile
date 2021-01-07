package com.micromos.knpmobile.ui.productcoilout

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.micromos.knpmobile.databinding.ShipOutItemListBinding
import com.micromos.knpmobile.dto.ShipOrder

class ProductCoilOutAdapter(val viewModel: ProductCoilOutViewModel, val context: Context) :
    RecyclerView.Adapter<ViewHolder>() {
    var items = listOf<ShipOrder>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ShipOutItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(viewModel, items[position], context)
    }
}

class ViewHolder(val binding: ShipOutItemListBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(viewModel: ProductCoilOutViewModel, item: ShipOrder, context: Context) {
        binding.viewModel = viewModel
        binding.shipOutItem = item
        binding.executePendingBindings()
    }
}