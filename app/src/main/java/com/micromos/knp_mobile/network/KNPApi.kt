package com.micromos.knp_mobile.network

import android.util.Log
import com.google.gson.GsonBuilder
import com.micromos.knp_mobile.dto.CoilIn
import com.micromos.knp_mobile.dto.CoilInFeed
import com.micromos.knp_mobile.dto.GetCustCd
import com.micromos.knp_mobile.dto.User
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface KNPApi {

 /*   @GET("product/check")
    fun getProductCheck(@Query("product_no") productNo: String): Call<ProductCheckData>

    @GET("product/check_master")
    fun getProductCheckMaster(@Query("product_no") productNo: String):Call<CheckDataWithSpec>

  */



    @GET("product/coil_in")
    fun coilIn(
        @Query("ship_no") shipNo : String,
    ): Call<CoilInFeed>

    @GET("product/get_cust")
    fun getCustCd(
        @Query("ship_no") shipNo : String,
    ): Call<GetCustCd>

    @GET("login/test_server")
    fun testServer(): Call<Unit>

    @GET("login")
    fun login(@Query("user_id") userId: String,
              @Query("password") password: String): Call<User>


    companion object Factory{
        fun create() : KNPApi {
            val uri = "http://192.168.0.137/index.php/"

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
            val interceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger{ message ->
                Log.v("HTTP", message)
            })
            return interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        }
    }
}
