package com.micromos.knp_mobile.ui.productcoilin

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.micromos.knp_mobile.databinding.ShipItemListBinding
import com.micromos.knp_mobile.dto.CoilIn

class ProductCoilinAdapter(private val context : Context) : RecyclerView.Adapter<ViewHolder>() {
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
        holder.bind(items[position])
    }
}

class ViewHolder(val binding : ShipItemListBinding) : RecyclerView.ViewHolder(binding.root){
    fun bind(item : CoilIn){
        binding.shipInItem = item
    }
}