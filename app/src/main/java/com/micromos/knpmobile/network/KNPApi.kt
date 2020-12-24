package com.micromos.knpmobile.network

import android.util.Log
import com.micromos.knpmobile.dto.*
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface KNPApi {

    @GET("product/get_cust_cd")
    fun getCustCd(
        @Query("ship_no") shipNo: String,
    ): Call<GetCustCd>

    @GET("product/get_ship_order")
    fun getShipOrder(
        @Query("ship_no") shipNo: String,
    ): Call<ShipOrderFeed>

    @GET("product/update_PDA_in")
    fun updatePDAin(
        @Query("label_no") labelNo: String,
    ): Call<Unit>

    @GET("product/update_PDA_out")
    fun updatePDAout(
        @Query("label_no") labelNo: String,
    ): Call<Unit>


    @GET("login/test_server")
    fun testServer(): Call<Unit>

    @GET("login")
    fun login(
        @Query("user_id") userId: String,
        @Query("password") password: String
    ): Call<User>


    companion object Factory {
        fun create(): KNPApi {
            val uri = "http://192.168.25.55/index.php/"

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
            return retrofit.create(KNPApi::class.java)
        }

        private fun httpLoggingInterceptor(): Interceptor {
            val interceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message ->
                Log.v("HTTP", message)
            })
            return interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        }
    }
}
