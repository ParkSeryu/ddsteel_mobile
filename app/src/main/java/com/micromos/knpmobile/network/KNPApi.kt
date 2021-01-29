package com.micromos.knpmobile.network

import android.util.Log
import com.micromos.knpmobile.BuildConfig
import com.micromos.knpmobile.dto.*
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface KNPApi {

    @GET("code/change_pos")
    fun changePosCd(
        @Query("pos_cd") posCd: String,
        @Query("user_id") userId: String,
        @Query("coil_no") coilNo: String,
        @Query("coil_seq") coilSeq: String,
        @Query("stk_type") stkType: String
    ): Call<Unit>

    @GET("code/get_code_cd")
    fun getCodeCd(
        @Query("code_kind") codeKind: String,
        @Query("use_cls") useCls: String
    ): Call<GetCodeCdFeed>

    @GET("code/get_pos_cd")
    fun getPosCd(
        @Query("label_no") labelNo: String
    ): Call<GetPosCd>

    @GET("product/get_cust_cd")
    fun getCustCd(
        @Query("ship_no") shipNo: String
    ): Call<GetCustCd>

    @GET("product/get_ship_order")
    fun getShipOrder(
        @Query("ship_no") shipNo: String
    ): Call<ShipOrderFeed>

    @GET("product/update_PDA_in")
    fun updatePDAin(
        @Query("label_no") labelNo: String,
        @Query("ship_no") shipNo: String
    ): Call<Unit>

    @GET("product/update_PDA_out")
    fun updatePDAout(
        @Query("label_no") labelNo: String,
        @Query("ship_no") shipNo: String
    ): Call<Unit>

    @GET("stock/get_label_no")
    fun getLabelNo(
        @Query("in_date") inDate: String,
        @Query("label_no") labelNo: String
    ): Call<GetLabelNo>

    @GET("stock/get_card_Info")
    fun getCardInfo(
        @Query("label_no") labelNo: String,
        @Query("in_date") inDate: String
    ): Call<GetCardInfo>

    @FormUrlEncoded
    @POST("stock/insert_coil_stock")
    fun insertCoilStock(
        @Field("in_date") inDate: String,
        @Field("stock_no") stockNo: String,
        @Field("user_id") userId: String,
        @Field("label_no") labelNo: String,
        @Field("pos_cd") posCd: String
    ): Call<Unit>

    @GET("stock/update_coil_stock")
    fun updateCoilStock(
        @Query("pos_cd") posCd: String,
        @Query("label_no") labelNo: String,
        @Query("in_date") inDate: String
    ): Call<Unit>


    @GET("version/getTime")
    fun getDateTime(): Call<ResponseBody>

    @GET("version/checkVersion")
    fun checkVersion(
        @Query("version") version : String
    ): Call<Unit>

    @GET("version/updateApp")
    @Streaming
    fun downloadApk(
    ): Call<ResponseBody>


    @GET("login")
    fun login(
        @Query("user_id") userId: String,
        @Query("password") password: String
    ): Call<User>


    companion object Factory {
        fun create(): KNPApi {

            val uri = if(BuildConfig.DEBUG) "http://119.205.209.23/KNP_API/Developer/index.php/"
            else "http://119.205.209.23/KNP_API/Real/index.php/"

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
