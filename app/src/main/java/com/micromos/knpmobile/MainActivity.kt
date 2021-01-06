package com.micromos.knpmobile

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.widget.*
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.micromos.knpmobile.dto.GetCodeCd
import com.micromos.knpmobile.dto.GetCodeCdFeed
import com.micromos.knpmobile.network.KNPApi
import com.micromos.knpmobile.ui.login.LoginViewModel.Companion.name
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.fragment_change_pos.*
import kotlinx.android.synthetic.main.fragment_coil_stock.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private val api = KNPApi.create()
    private val code_kind = "SYS05"
    private val use_cls = "1"

    companion object {
        val codeCdList = mutableListOf<String?>() as ArrayList<String>
        val codeNmList = mutableListOf<String?>() as ArrayList<String>
        val codeList = mutableMapOf<String, String>()
        fun autoCompleteTextViewCustom(AT: AutoCompleteTextView, context: Context) {
            var text = AT.text.toString()
            val rstList = java.util.ArrayList<String>()

            if (text.isNotEmpty()) {
                if (text.length >= 3 && text.toCharArray()[2] != '-') {
                    val char = text.substring(0, 2)
                    text = char + "-" + text.substring(2)
                }
                Log.d("testMatchText", text)
                for (i in 0 until codeNmList.size) {
                    if (codeNmList[i].contains(text)) {
                        Log.d("testMatchList", codeNmList[i])
                        rstList.add(codeNmList[i])
                    }

                }

                val adapter =
                    ArrayAdapter<String>(
                        context,
                        android.R.layout.simple_list_item_1,
                        rstList
                    )
                Log.d("testMatchShow", "$rstList")
                if (rstList.size == 0) {
                    AT.dismissDropDown()
                } else {
                    AT.setAdapter(adapter)
                    AT.showDropDown()
                }
            } else {
                val adapter = ArrayAdapter<String>(
                    context,
                    android.R.layout.simple_list_item_1,
                    codeNmList
                )
                AT.setAdapter(adapter)
                AT.showDropDown()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        //val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        //val navView: NavigationView = findViewById(R.id.nav_view)
        //val header = navView.getHeaderView(0)
        //val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        /*   appBarConfiguration = AppBarConfiguration(
               setOf(
                   R.id.nav_home,
                   R.id.nav_ship_in,
                   R.id.nav_ship_out,
                   R.id.nav_coil_stock,
                   R.id.nav_pos_change
               ), drawerLayout
           )*/

        toolbar_name.text = name
        //val headerText = header.findViewById(R.id.headerText) as TextView
        //headerText.text = String.format(getString(R.string.prompt_hello), name)
        getCodeCd()
        HomeButton.setOnClickListener {
            goHome()
        }
        //setupActionBarWithNavController(navController, appBarConfiguration)
        //navView.setupWithNavController(navController)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun getCodeCd() {
        //_isLoading.value = true
        api.getCodeCd(code_kind, use_cls).enqueue(object : Callback<GetCodeCdFeed> {
            override fun onResponse(call: Call<GetCodeCdFeed>, response: Response<GetCodeCdFeed>) {
                codeCdList.clear()
                codeNmList.clear()
                Log.d("testGetCode", response.body().toString())
                response.body()?.items?.forEach {
                    codeCdList.add(it.codeCd)
                    codeNmList.add(it.codeNm)
                    codeList[it.codeCd] = it.codeNm
                }
            }

            override fun onFailure(call: Call<GetCodeCdFeed>, t: Throwable) {
                Log.d("testFailedGetCode", t.message.toString())
            }
        })
    }

    fun transToUpperCase(s: Editable?, editText: EditText) {
        var text = s.toString()
        if (text != text.toUpperCase(Locale.ROOT)) {
            text = text.toUpperCase(Locale.ROOT)
            editText.setText(text)
            editText.setSelection(editText.length())
        }
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

    /*override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
       //back(취소)키가 눌렸을때 종료여부를 묻는 다이얼로그 띄움
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            CustomDialog(this, R.layout.dialog_app_finish)
                .setMessage(R.string.prompt_exit)
                .setPositiveButton("예") {
                    finish()
                }.setNegativeButton("아니오") {
                }.show()
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
*/
    fun goHome() {
        Toast.makeText(this, "go Home", Toast.LENGTH_LONG).show()
    }

    fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.nav_host_fragment, fragment).commit()
    }

    fun logout() {
        //back(취소)키가 눌렸을때 종료여부를 묻는 다이얼로그 띄움
        CustomDialog(this, R.layout.dialog_app_finish)
            .setMessage(R.string.prompt_exit)
            .setPositiveButton("예") {
                finish()
            }.setNegativeButton("아니오") {
            }.show()
    }
}