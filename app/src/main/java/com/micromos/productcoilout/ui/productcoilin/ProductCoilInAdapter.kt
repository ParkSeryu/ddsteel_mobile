package com.micromos.productcoilout.ui.productcoilin

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.micromos.productcoilout.databinding.ShipItemListBinding
import com.micromos.productcoilout.dto.CoilIn

class ProductCoilinAdapter(val viewModel: ProductCoilInViewModel, val context: Context) : RecyclerView.Adapter<ViewHolder>() {
    var items = listOf<CoilIn>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ShipItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(viewModel, items[position], context)
    }
}

class ViewHolder(val binding : ShipItemListBinding) : RecyclerView.ViewHolder(binding.root){
    fun bind(viewModel: ProductCoilInViewModel, item : CoilIn, context: Context){
        binding.viewModel = viewModel
        binding.shipInItem = item
        binding.executePendingBindings()
    }
}