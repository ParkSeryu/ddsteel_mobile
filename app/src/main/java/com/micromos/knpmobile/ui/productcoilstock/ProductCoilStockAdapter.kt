package com.micromos.knpmobile.ui.productcoilstock

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.micromos.knpmobile.databinding.CardItemListBinding
import com.micromos.knpmobile.dto.GetCardInfo
import com.micromos.knpmobile.dto.ShipOrder

class ProductCoilStockAdapter(val viewModel: ProductCoilStockViewModel, val context: Context) :
    RecyclerView.Adapter<ViewHolder>() {
    var items = mutableListOf<GetCardInfo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            CardItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(viewModel, items[position], context)
    }
}

class ViewHolder(val binding: CardItemListBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(viewModel: ProductCoilStockViewModel, item: GetCardInfo, context: Context) {
        binding.viewModel = viewModel
        binding.cardItem = item
        binding.executePendingBindings()
    }
}