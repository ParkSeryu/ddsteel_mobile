package com.micromos.knpmobile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.micromos.knpmobile.network.KNPApi
import kotlinx.android.synthetic.main.activity_update.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URL

class UpdateActivity : AppCompatActivity() {
    private val api = KNPApi.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)

        downloadApk()
    }

    private fun downloadApk() {
        val buildType = if (BuildConfig.DEBUG) {
            "DEBUG"
        } else {
            "RELEASE"
        }

        api.downloadApk(buildType).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                val apk = response.body()

                lifecycleScope.launch(Dispatchers.IO) {
                    val url = if (BuildConfig.DEBUG) {
                        URL("http://192.168.0.137/index.php/version/updateApp?buildType=DEBUG")
                    } else {
                        URL("http://192.168.0.137/index.php/version/updateApp?buildType=RELEASE")
                    }
                    val connection = url.openConnection()
                    connection.connect()

                    val fileLength = connection.contentLength // 파일 크기를 가져옴

                    Log.d(
                        "downloadLifeCycle",
                        "$fileLength"
                    )

                    val data = ByteArray(1024 * 4)
                    val inputStream: InputStream =
                        BufferedInputStream(apk?.byteStream(), 1024 * 8)
                    val path = filesDir
                    val outputFile = if (BuildConfig.DEBUG) {
                        File(path, "knp-debug.apk")
                    } else {
                        File(path, "knp-release.apk")
                    }

                    if (outputFile.exists())
                        outputFile.delete()

                    val fileOutputStream = FileOutputStream(outputFile)
                    var fileSizeDownload = 0L


                    do {
                        val count = inputStream.read(data)
                        if (count == -1)
                            break

                        fileOutputStream.write(data, 0, count)
                        fileSizeDownload += count
                        lifecycleScope.launch(Dispatchers.Main) {
                            if (fileLength > 0) { // 파일 총 크기가 0 보다
                                val calc = "${(fileSizeDownload * 100 / fileLength).toInt()}%"
                                percent?.text = calc
                                Log.d(
                                    "downloadPercent",
                                    "${(fileSizeDownload * 100 / fileLength).toInt()}%"
                                )
                            }
                        }

                    } while (true)
                    Log.d(
                        "downloadServerApk",
                        "$fileSizeDownload"
                    )

                    fileOutputStream.flush()
                    fileOutputStream.close()
                    inputStream.close()
                    installApk(outputFile)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("downloadFail", t.message.toString())
            }
        })
    }

    private fun installApk(apk: File) {
        val intent = Intent(Intent.ACTION_VIEW)
        val apkUri =
            FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", apk)
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun onBackPressed() {

    }


}