package com.micromos.knpmobile.ui.scanproductcoilout

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.micromos.knpmobile.databinding.CardItemShipOutScanBinding
import com.micromos.knpmobile.dto.ShipOrder

class ProductCoilInCaptureAdapter(val viewModel : ScanProductCoilOutViewModel) :
    RecyclerView.Adapter<ViewHolder>() {
    var items = listOf<ShipOrder>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            CardItemShipOutScanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(viewModel, items[position])
    }
}

class ViewHolder(val binding: CardItemShipOutScanBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(viewModel: ScanProductCoilOutViewModel, item: ShipOrder) {
        binding.viewModel = viewModel
        binding.shipOutItem = item
        binding.executePendingBindings()
    }
}