package com.micromos.knpmobile;

import android.content.Context;
import android.util.AttributeSet;

public class CustomAutoCompleteTextView extends androidx.appcompat.widget.AppCompatAutoCompleteTextView {

    public CustomAutoCompleteTextView(Context context) {
        super(context);
    }

    public CustomAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public CustomAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void onFilterComplete(int count) {
    }

    @Override
    protected void performFiltering(CharSequence text, int keyCode) {
        super.performFiltering(text, keyCode);
    }

    @Override
    public boolean enoughToFilter() {
        return true;
    }
}
