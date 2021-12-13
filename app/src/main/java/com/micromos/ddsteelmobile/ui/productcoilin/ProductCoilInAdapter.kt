package com.micromos.ddsteelmobile.ui.productcoilin

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.micromos.ddsteelmobile.databinding.CardItemShipInBinding
import com.micromos.ddsteelmobile.dto.ShipOrder

class ProductCoilInAdapter(val viewModel: ProductCoilInViewModel) :
    RecyclerView.Adapter<ViewHolder>() {
    var items = listOf<ShipOrder>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            CardItemShipInBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("testPosition",position.toString())
        holder.bind(viewModel, items[position])
    }
}

class ViewHolder(val binding: CardItemShipInBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(viewModel: ProductCoilInViewModel, item: ShipOrder) {
        binding.viewModel = viewModel
        binding.shipInItem = item
        binding.executePendingBindings()
    }
}

//class ProductCoilInAdapter(val viewModel: ProductCoilInViewModel) : ListAdapter<ShipOrder, ViewHolder>(MyDiffCallback) {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val binding =
//            CardItemShipInBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        Log.d("test345", currentList.toString())
//        return ViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        Log.d("test23", position.toString())
//        Log.d("test234", getItem(position).toString())
//        holder.bind(viewModel, getItem(position))
//    }
//}
//
//class ViewHolder(val binding: CardItemShipInBinding) : RecyclerView.ViewHolder(binding.root) {
//    fun bind(viewModel : ProductCoilInViewModel, item: ShipOrder) {
//        Log.d("testadapter","test")
//        binding.viewModel = viewModel
//        binding.shipInItem = item
//        binding.executePendingBindings()
//    }
//}
//
//object MyDiffCallback : DiffUtil.ItemCallback<ShipOrder>() {
//    override fun areItemsTheSame(oldItem: ShipOrder, newItem: ShipOrder): Boolean {
//        Log.d("test123", "{ $oldItem / ${newItem}}")
//        return oldItem.shipNo == newItem.shipNo
//    }
//
//    override fun areContentsTheSame(oldItem: ShipOrder, newItem: ShipOrder): Boolean {
//        Log.d("test124", "{ $oldItem / $newItem }")
//        return oldItem == newItem
//    }
//}
