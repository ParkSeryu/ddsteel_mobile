package com.micromos.knpmobile.ui.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.micromos.knpmobile.CustomDialog
import com.micromos.knpmobile.MainActivity
import com.micromos.knpmobile.R
import com.micromos.knpmobile.UpdateActivity
import com.micromos.knpmobile.databinding.ActivityLoginBinding
import com.micromos.knpmobile.network.KNPApi
import com.micromos.knpmobile.ui.login.LoginViewModel.Companion.user_id
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var LoginViewModel: LoginViewModel
    private lateinit var LoginViewDataBinding: ActivityLoginBinding
    private val api = KNPApi.create()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LoginViewDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        LoginViewModel =
            ViewModelProvider(this).get(com.micromos.knpmobile.ui.login.LoginViewModel::class.java)
        window.statusBarColor = ContextCompat.getColor(this, R.color.main_color);
        LoginViewDataBinding.viewModel = LoginViewModel
        LoginViewDataBinding.lifecycleOwner = this

        val sharedPreferences: SharedPreferences = getSharedPreferences("ID", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        checkVersion()
        LoginViewModel._id.value = sharedPreferences.getString("ID","")


        LoginViewModel.loginSuccessEvent.observe(this, Observer
        {
            val preferencesID = sharedPreferences.getString("ID", "")
            if(preferencesID != user_id){
                editor.putString("ID", user_id)
                editor.apply()
            }
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        })

        LoginViewModel.loginDeniedEvent.observe(this, Observer
        {
            CustomDialog(this, R.layout.dialog_incorrect)
                .setTitle(R.string.denied_login)
                .setMessage(R.string.message_denied_login)
                .setPositiveButton(R.string.dialog_ok) {
                }.show()
        })

        LoginViewModel.loginFailedEvent.observe(this, Observer
        {
            CustomDialog(this, R.layout.dialog_incorrect)
                .setTitle(R.string.failed_login)
                .setMessage(R.string.message_login_failed)
                .setPositiveButton(R.string.dialog_ok) {
                }.show()
        })

        LoginViewModel.NotConnectedServer.observe(this, Observer
        {
            CustomDialog(this, R.layout.dialog_incorrect)
                .setTitle(R.string.failed_login)
                .setMessage(R.string.message_not_connected)
                .setPositiveButton(R.string.dialog_ok) {
                }.show()
        })


        outer_layout_login.setOnClickListener { hideKeyboard() }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(userID.windowToken, 0)
        imm.hideSoftInputFromWindow(password.windowToken, 0)
    }

    private fun checkVersion() {
        val info =
            applicationContext.packageManager.getPackageInfo(applicationContext.packageName, 0)
        val version = info.versionName
        api.checkVersion(version).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.code() == 200) {
                    CustomDialog(this@LoginActivity, R.layout.dialog_app_update)
                        .setMessage(R.string.prompt_update_exists)
                        .setPositiveButton("확인") {
                           val intent = Intent(applicationContext, UpdateActivity::class.java)
                            startActivity(intent)

                        }.setNegativeButton("종료"){
                            finish()
                        }.show()
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                CustomDialog(this@LoginActivity, R.layout.dialog_app_not_connected)
                    .setMessage(R.string.message_not_connected_server)
                    .setPositiveButton("재시도") {
                        finish()
                    }.setNegativeButton("종료") {
                        checkVersion()
                    }.show()
            }
        })
    }
}

