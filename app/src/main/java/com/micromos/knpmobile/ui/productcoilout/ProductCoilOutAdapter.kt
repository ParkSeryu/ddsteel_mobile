package com.micromos.knpmobile.ui.productcoilout

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.micromos.knpmobile.databinding.CardItemShipOutBinding
import com.micromos.knpmobile.dto.ShipOrder

class ProductCoilOutAdapter(val viewModel: ProductCoilOutViewModel) :
    RecyclerView.Adapter<ViewHolder>() {
    var items = listOf<ShipOrder>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            CardItemShipOutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(viewModel, items[position])
    }
}

class ViewHolder(val binding: CardItemShipOutBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(viewModel: ProductCoilOutViewModel, item: ShipOrder) {
        binding.viewModel = viewModel
        binding.shipOutItem = item
        binding.executePendingBindings()
    }
}