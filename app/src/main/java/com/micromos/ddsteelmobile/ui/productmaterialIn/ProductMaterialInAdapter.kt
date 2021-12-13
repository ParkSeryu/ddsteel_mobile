package com.micromos.ddsteelmobile.ui.productmaterialIn

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.micromos.ddsteelmobile.databinding.CardItemMaterialInBinding
import com.micromos.ddsteelmobile.dto.GetMaterialCardInfo

class ProductMaterialInAdapter(val viewModel: ProductMaterialInViewModel) :
    RecyclerView.Adapter<ViewHolder>() {
    var items = mutableListOf<GetMaterialCardInfo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            CardItemMaterialInBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(viewModel, items[position])
    }
}

class ViewHolder(val binding: CardItemMaterialInBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(viewModel: ProductMaterialInViewModel, item: GetMaterialCardInfo) {
        binding.viewModel = viewModel
        binding.materialInItem = item
        binding.executePendingBindings()
    }
}