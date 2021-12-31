package com.micromos.ddsteelmobile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class M3Receiver : BroadcastReceiver() {
    val TAG: String by lazy { "TAG ${this.javaClass.simpleName}" }
    var scanListener: ScanListener? = null

    interface ScanListener{
        fun onScan(scanResult: String)
    }

    companion object{
        @Volatile private var instance: M3Receiver? = null

        fun getInstance(): M3Receiver{
            if(instance == null){
                synchronized(this){
                    instance = M3Receiver()
                }
            }
            return instance!!
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action.equals("com.android.server.scannerservice.broadcast")){
            val scanText = intent?.getStringExtra("m3scannerdata")?:""
            scanListener?.onScan(scanText)
        }
    }

    fun register(_scanListener: ScanListener){
        scanListener = _scanListener
    }

    fun unRegister(){
        scanListener = null
    }
}