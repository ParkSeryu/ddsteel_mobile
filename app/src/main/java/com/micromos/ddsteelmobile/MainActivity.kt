package com.micromos.ddsteelmobile

import android.Manifest
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.micromos.ddsteelmobile.dto.GetCommonPosCdFeed
import com.micromos.ddsteelmobile.network.DDsteelApi
import com.micromos.ddsteelmobile.ui.home.HomeFragment
import com.micromos.ddsteelmobile.ui.login.LoginViewModel.Companion.name
import com.micromos.ddsteelmobile.ui.login.LoginViewModel.Companion.work_place_cd
import com.micromos.ddsteelmobile.ui.login.LoginViewModel.Companion.work_place_nm
import kotlinx.android.synthetic.main.app_bar_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val api = DDsteelApi.create()
    private val FLAG_PERM_CAMERA = 98
    lateinit var m3Receiver: M3Receiver

    companion object {
        val pos_cd_list = mutableListOf<String>()
        val pos_nm_list = mutableListOf<String>()
        val posList = mutableMapOf<String, String>()

        fun autoCompleteTextViewCustomPosCd(AT: AutoCompleteTextView, context: Context) {
            val text = AT.text.toString()
            val atList = ArrayList<String>()

            if (text.isNotEmpty()) {
                for (i in 0 until pos_nm_list.size) {
                    if (pos_nm_list[i].contains(text)) {
                        Log.d("testMatchList", pos_nm_list[i])
                        atList.add(pos_nm_list[i])
                    }
                }

                if (atList.size in 1..4) {
                    while (atList.size < 5) {
                        atList.add("")
                    }
                }

                val adapter =
                    ArrayAdapter<String>(
                        context,
                        R.layout.custom_auto_complete_layout,
                        atList
                    )
                Log.d("testMatchShow", "$atList")

                if (atList.size == 0) {
                    //AT.dismissDropDown()
                } else {
                    AT.setAdapter(adapter)
                    Handler(Looper.getMainLooper()).postDelayed({
                        AT.showDropDown()
                    }, 5)
                }
            } else {
                val adapter = ArrayAdapter(
                    context,
                    R.layout.custom_auto_complete_layout,
                    pos_nm_list
                )
                AT.setAdapter(adapter)
                Handler(Looper.getMainLooper()).postDelayed({
                    AT.showDropDown()
                }, 5)
            }
        }

        fun transToUpperCase(s: Editable?, editText: EditText) {
            var text = s.toString()
            if (text != text.toUpperCase(Locale.ROOT)) {
                text = text.toUpperCase(Locale.ROOT)
                editText.setText(text)
                editText.setSelection(editText.length())
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        m3Receiver = M3Receiver.getInstance()
        toolbar_name.text = "$name($work_place_nm)"
        getCommonPosCd()
        //window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) //????????????
        HomeButton.setOnClickListener {
            goHome()
        }
        supportActionBar?.setDisplayShowTitleEnabled(false)
        permissionCheck()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            FLAG_PERM_CAMERA -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "????????? ?????? ?????? ??????", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "????????? ?????? ?????? ??????", Toast.LENGTH_LONG).show()
                }
            }
        }
        return
    }

    private fun permissionCheck() {
        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.CAMERA
                )
            ) {
            } else {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.CAMERA),
                    FLAG_PERM_CAMERA
                )
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun getCommonPosCd() {
        api.getCommonPosCd(work_place_cd!!).enqueue(object : Callback<GetCommonPosCdFeed> {
            override fun onResponse(
                call: Call<GetCommonPosCdFeed>,
                response: Response<GetCommonPosCdFeed>
            ) {
                pos_cd_list.clear()
                pos_nm_list.clear()
                Log.d("testGetPosCd", response.body().toString())
                response.body()?.items?.forEach {
                    pos_cd_list.add(it.posCd)
                    pos_nm_list.add(it.posNm)
                    posList[it.posCd] = it.posNm
                }
            }

            override fun onFailure(call: Call<GetCommonPosCdFeed>, t: Throwable) {
                Log.d("testFailedGetCode", t.message.toString())
            }
        })
    }

    fun setTextChangedListener(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                transToUpperCase(s, editText)
            }
        })
    }

    private fun goHome() {
        replaceFragment(HomeFragment.newInstance())
    }

    fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.nav_host_fragment, fragment).commit()
    }

    fun logout() {
        //back(??????)?????? ???????????? ??????????????? ?????? ??????????????? ??????
        CustomDialog(this, R.layout.dialog_app_finish)
            .setMessage(R.string.prompt_exit)
            .setPositiveButton("???") {
                moveTaskToBack(true)
                finishAndRemoveTask()
                android.os.Process.killProcess(android.os.Process.myPid())
            }.setNegativeButton("?????????") {
            }.show()
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter()
        filter.addAction("com.android.server.scannerservice.broadcast")
        registerReceiver(m3Receiver, filter)
        Log.d("onResume", "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("onPause", "onPause")
        m3Receiver.unRegister()
    }


}