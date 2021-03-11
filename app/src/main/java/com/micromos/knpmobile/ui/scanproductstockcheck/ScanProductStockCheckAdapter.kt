package com.micromos.knpmobile.ui.scanproductstockcheck

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.micromos.knpmobile.databinding.CardItemListStockInsertScanBinding
import com.micromos.knpmobile.databinding.CardItemListStockUpdateScanBinding
import com.micromos.knpmobile.dto.GetCardInfo

class ScanProductStockCheckAdapter(val checkViewModel: ScanProductStockCheckViewModel, val context: Context) :
    RecyclerView.Adapter<ViewHolder>() {

    var item = mutableListOf<GetCardInfo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == 1) {
            val binding =
                CardItemListStockUpdateScanBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            return ViewHolder(binding)

        } else {
            val binding =
                CardItemListStockInsertScanBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            return ViewHolder(binding)
        }
    }

    override fun getItemCount(): Int {
        return item.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder.itemViewType == 1) {
            holder.bindUpdate(checkViewModel, item[position])
        } else {
            holder.bindInsert(checkViewModel, item[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (item[position].updateFlag == 1) {
            1 // update
        } else
            2 // insert
    }

}

class ViewHolder : RecyclerView.ViewHolder {
    lateinit var bindingUpdate: CardItemListStockUpdateScanBinding
    lateinit var bindingInsert: CardItemListStockInsertScanBinding

    constructor(binding: CardItemListStockUpdateScanBinding) : super(binding.root) {
        bindingUpdate = binding
    }

    constructor(binding: CardItemListStockInsertScanBinding) : super(binding.root) {
        bindingInsert = binding
    }

    fun bindUpdate(checkViewModel: ScanProductStockCheckViewModel, item: GetCardInfo) {
        bindingUpdate.viewModel = checkViewModel
        bindingUpdate.cardItem = item
        bindingUpdate.executePendingBindings()
    }

    fun bindInsert(checkViewModel: ScanProductStockCheckViewModel, item: GetCardInfo) {
        bindingInsert.viewModel = checkViewModel
        bindingInsert.cardItem = item
        bindingInsert.executePendingBindings()
    }
}