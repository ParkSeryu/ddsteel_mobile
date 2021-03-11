package com.micromos.knpmobile.ui.scanproductcoilin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.micromos.knpmobile.databinding.CardItemShipInScanBinding
import com.micromos.knpmobile.dto.ShipOrder

class ProductCoilInCaptureAdapter(val viewModel : ScanProductCoilInViewModel) :
    RecyclerView.Adapter<ViewHolder>() {
    var items = listOf<ShipOrder>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            CardItemShipInScanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(viewModel, items[position])
    }
}

class ViewHolder(val binding: CardItemShipInScanBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(viewModel: ScanProductCoilInViewModel, item: ShipOrder) {
        binding.viewModel = viewModel
        binding.shipInItem = item
        binding.executePendingBindings()
    }
}