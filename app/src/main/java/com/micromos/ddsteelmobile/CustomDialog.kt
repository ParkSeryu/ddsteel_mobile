package com.micromos.ddsteelmobile

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import androidx.annotation.StringRes
import kotlinx.android.synthetic.main.dialog_request.view.*

class CustomDialog(private val context: Context, private val dialogName : Int) {

    private val builder: AlertDialog.Builder by lazy {
        AlertDialog.Builder(context).setView(view)
    }

    private val view: View by lazy {
        View.inflate(context, dialogName, null)
    }

    private var dialog: AlertDialog? = null

    // 터치 리스너 구현
    @SuppressLint("ClickableViewAccessibility")
    private val onTouchListener = View.OnTouchListener { _, motionEvent ->
        if (motionEvent.action == MotionEvent.ACTION_UP) {
            Handler(Looper.getMainLooper()).postDelayed({
                dismiss()
            }, 5)
        }
        false
    }

    fun setTitle(@StringRes titleId: Int): CustomDialog {
        view.titleTextView.text = context.getText(titleId)
        return this
    }

    fun setTitle(title: CharSequence): CustomDialog {
        view.titleTextView.text = title
        return this
    }

    fun setMessage(@StringRes messageId: Int): CustomDialog {
        view.messageTextView.text = context.getText(messageId)
        return this
    }

    fun setMessage(message: CharSequence): CustomDialog {
        view.messageTextView.text = message
        return this
    }

    fun setPositiveButton(@StringRes textId: Int, listener: (view: View) -> (Unit)): CustomDialog {
        view.positiveButton.apply {
            text = context.getText(textId)
            setOnClickListener(listener)
            // 터치 리스너 등록
            setOnTouchListener(onTouchListener)
        }
        return this
    }

    fun setPositiveButton(text: CharSequence, listener: (view: View) -> (Unit)): CustomDialog {
        view.positiveButton.apply {
            this.text = text
            setOnClickListener(listener)
            // 터치 리스너 등록
            setOnTouchListener(onTouchListener)
        }
        return this
    }

    fun setNegativeButton(@StringRes textId: Int, listener: (view: View) -> (Unit)): CustomDialog {
        view.negativeButton.apply {
            text = context.getText(textId)
            this.text = text
            setOnClickListener(listener)
            // 터치 리스너 등록
            setOnTouchListener(onTouchListener)
        }
        return this
    }

    fun setNegativeButton(text: CharSequence, listener: (view: View) -> (Unit)): CustomDialog {
        view.negativeButton.apply {
            this.text = text
            setOnClickListener(listener)
            // 터치 리스너 등록
            setOnTouchListener(onTouchListener)
        }
        return this
    }

    fun create() {
        dialog = builder.create()
    }

    fun show() {
        dialog = builder.create()
        if(dialogName == R.layout.dialog_request || dialogName == R.layout.dialog_app_not_connected || dialogName == R.layout.dialog_app_update)
        dialog?.setCancelable(false)
        dialog?.show()
    }

     fun dismiss() {
        dialog?.dismiss()
    }

}