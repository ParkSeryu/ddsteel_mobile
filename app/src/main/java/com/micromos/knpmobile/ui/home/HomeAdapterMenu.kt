package com.micromos.knpmobile.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.micromos.knpmobile.databinding.MenuItemBinding

class HomeAdapterMenu(val viewModel: HomeViewModel) :
    RecyclerView.Adapter<ViewHolder>() {
    var items = listOf<MenuItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            MenuItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(viewModel, items[position])
    }
}

class ViewHolder(val binding: MenuItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(viewModel: HomeViewModel, item: MenuItem) {
        binding.viewModel = viewModel
        binding.menuItemList = item
        binding.executePendingBindings()
    }
}