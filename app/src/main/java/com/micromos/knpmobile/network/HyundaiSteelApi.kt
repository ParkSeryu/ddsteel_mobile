package com.micromos.knpmobile.network

import android.util.Log
import com.micromos.knpmobile.BuildConfig
import com.micromos.knpmobile.dto.*
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface HyundaiSteelApi {

    @FormUrlEncoded
    @POST("stppsales/stppship/selectEleMcIssueQrcodeCr.do")
    fun getLabelInfo(
        @Field("coil_no") coilNo: String
    ): Call<HyundaiLabelInfoFeed>

    companion object Factory {
        fun create(): HyundaiSteelApi {

            val uri =
                if (BuildConfig.DEBUG) "https://cp.hyundai-steel.com/"
                else "https://cp.hyundai-steel.com/"

            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(httpLoggingInterceptor())
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(uri)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(HyundaiSteelApi::class.java)
        }

        private fun httpLoggingInterceptor(): Interceptor {
            val interceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message ->
                Log.v("HTTP", message)
            })
            return interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        }
    }
}