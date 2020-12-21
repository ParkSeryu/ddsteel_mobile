package com.micromos.knp_mobile.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.micromos.knp_mobile.CustomDialog
import com.micromos.knp_mobile.MainActivity
import com.micromos.knp_mobile.R
import com.micromos.knp_mobile.databinding.ActivityLoginBinding
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var LoginViewModel: LoginViewModel
    private lateinit var LoginViewDataBinding: ActivityLoginBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Binding 객체를 가져온다, ActivityLoginBinding과 같은 클래스는 자동 생성되므로
        // 인스턴스를 설정하고 데이터를 바인딩 할 수 있다.
        LoginViewDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        LoginViewModel =
            ViewModelProvider(this).get(com.micromos.knp_mobile.ui.login.LoginViewModel::class.java)
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.main_color));
        LoginViewDataBinding.viewModel = LoginViewModel
        LoginViewDataBinding.lifecycleOwner = this

        LoginViewModel.loginSuccessEvent.observe(this, Observer {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        })

        LoginViewModel.loginDeniedEvent.observe(this, Observer {
            CustomDialog(this, R.layout.dialog_incorrect_login)
                .setTitle(R.string.denied_login)
                .setMessage(R.string.message_denied_login)
                .setPositiveButton(R.string.dialog_ok) {
                }.show()
        })

        LoginViewModel.loginFailedEvent.observe(this, Observer {
            CustomDialog(this, R.layout.dialog_incorrect_login)
                .setTitle(R.string.failed_login)
                .setMessage(R.string.message_login_failed)
                .setPositiveButton(R.string.dialog_ok) {
                }.show()
        })

        outer_layout_login.setOnClickListener { hideKeyboard() }
    }

    fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(userID.windowToken, 0)
        imm.hideSoftInputFromWindow(password.windowToken, 0)
    }
}

