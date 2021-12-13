package com.micromos.ddsteelmobile

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatAutoCompleteTextView

class CustomAutoCompleteTextView : AppCompatAutoCompleteTextView {
    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!, attrs, defStyleAttr
    ) {
    }

    override fun onFilterComplete(count: Int) {}
    override fun performFiltering(text: CharSequence, keyCode: Int) {
        super.performFiltering(text, keyCode)
    }

    override fun enoughToFilter(): Boolean {
        return true
    }
}