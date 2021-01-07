package com.micromos.knpmobile.ui.productstockcheck

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.micromos.knpmobile.databinding.CardItemListStockInsertBinding
import com.micromos.knpmobile.databinding.CardItemListStockUpdateBinding
import com.micromos.knpmobile.dto.GetCardInfo

class ProductCoilStockAdapter(val checkViewModel: ProductStockCheckViewModel, val context: Context) :
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
            holder.bindUpdate(checkViewModel, item[position], context)
        } else {
            holder.bindInsert(checkViewModel, item[position], context)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (item[position].update == 1) {
            return 1 // update
        } else
            return 2 // insert
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

    fun bindUpdate(checkViewModel: ProductStockCheckViewModel, item: GetCardInfo, context: Context) {
        bindingUpdate.viewModel = checkViewModel
        bindingUpdate.cardItem = item
        bindingUpdate.executePendingBindings()
    }

    fun bindInsert(checkViewModel: ProductStockCheckViewModel, item: GetCardInfo, context: Context) {
        bindingInsert.viewModel = checkViewModel
        bindingInsert.cardItem = item
        bindingInsert.executePendingBindings()
    }
}