package com.micromos.ddsteelmobile.ui.productstockcheck

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.micromos.ddsteelmobile.databinding.CardItemListStockInsertBinding
import com.micromos.ddsteelmobile.databinding.CardItemListStockUpdateBinding
import com.micromos.ddsteelmobile.dto.GetCardInfo

class ProductCoilStockAdapter(private val checkViewModel: ProductStockCheckViewModel, val context: Context) :
    RecyclerView.Adapter<ViewHolder>() {
    var item = mutableListOf<GetCardInfo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == 1) {
            val binding =
                CardItemListStockUpdateBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            return ViewHolder(binding)

        } else {
            val binding =
                CardItemListStockInsertBinding.inflate(
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
    lateinit var bindingUpdate: CardItemListStockUpdateBinding
    lateinit var bindingInsert: CardItemListStockInsertBinding

    constructor(binding: CardItemListStockUpdateBinding) : super(binding.root) {
        bindingUpdate = binding
    }

    constructor(binding: CardItemListStockInsertBinding) : super(binding.root) {
        bindingInsert = binding
    }

    fun bindUpdate(checkViewModel: ProductStockCheckViewModel, item: GetCardInfo) {
        bindingUpdate.viewModel = checkViewModel
        bindingUpdate.cardItem = item
        bindingUpdate.executePendingBindings()
    }

    fun bindInsert(checkViewModel: ProductStockCheckViewModel, item: GetCardInfo) {
        bindingInsert.viewModel = checkViewModel
        bindingInsert.cardItem = item
        bindingInsert.executePendingBindings()
    }
}