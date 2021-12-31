package com.micromos.ddsteelmobile.network

import android.util.Log
import com.micromos.ddsteelmobile.BuildConfig
import com.micromos.ddsteelmobile.dto.*
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface DDsteelApi {

    @GET("version/getTime")
    fun getDateTime(): Call<ResponseBody>

    @GET("version/checkVersion")
    fun checkVersion(
        @Query("version") version: String
    ): Call<Unit>

    @GET("version/updateApp")
    @Streaming
    fun downloadApk(
    ): Call<ResponseBody>

    @GET("posChange/get_common_pos_cd")
    fun getCommonPosCd(): Call<GetCommonPosCdFeed>

    @GET("posChange/get_label_pos_cd")
    fun getLabelPosCd(
        @Query("label_no") labelNo: String,
    ): Call<GetLabelPosCd>

    @GET("posChange/change_pos")
    fun changePosCd(
        @Query("pos_cd") posCd: String,
        @Query("user_id") userId: String,
        @Query("coil_no") coilNo: String,
        @Query("coil_seq") coilSeq: String
    ): Call<Unit>

    @GET("product/get_cust_cd")
    fun getCustCd(
        @Query("ship_no") shipNo: String
    ): Call<GetCustCd>

    @GET("product/get_ship_order")
    fun getShipOrder(
        @Query("ship_no") shipNo: String
    ): Call<ShipOrderFeed>

    @GET("product/update_scan_date")
    fun updateScanDate(
        @Query("user_id") userId: String,
        @Query("label_no") labelNo: String,
        @Query("ship_no") shipNo: String
    ): Call<GetUpdateScanCardInfo>

    @GET("product/forced_update")
    fun forcedUpdate(
        @Query("user_id") userId: String,
        @Query("ship_no") shipNo: String,
        @Query("ship_seq") shipSeq: String
    ): Call<GetUpdateScanCardInfo>

    @GET("product/rollback_update")
    fun rollBackUpdate(
        @Query("ship_no") shipNo: String,
        @Query("ship_seq") shipSeq: String
    ): Call<GetUpdateScanCardInfo>


    @GET("stock/get_label_no")
    fun getLabelNo(
        @Query("stock_date") stockDate: String,
        @Query("label_no") labelNo: String
    ): Call<GetLabelNo>

    @GET("stock/get_card_Info")
    fun getCardInfo(
        @Query("label_no") labelNo: String,
        @Query("in_date") inDate: String
    ): Call<GetCardInfo>

    @GET("stock/insert_coil_stock")
    fun insertCoilStock(
        @Query("in_date") inDate: String,
        @Query("stock_no") stockNo: String,
        @Query("user_id") userId: String,
        @Query("label_no") labelNo: String,
        @Query("pos_cd") posCd: String
    ): Call<Unit>

    @GET("stock/update_coil_stock")
    fun updateCoilStock(
        @Query("pos_cd") posCd: String,
        @Query("label_no") labelNo: String,
        @Query("in_date") inDate: String,
        @Query("pack_cls") packCls : Int
    ): Call<Unit>

    @GET("materialIn/getNewSequence")
    fun getNewSequence(): Call<GetSequence>

    @GET("materialIn/updateMaterialIn")
    fun updateMaterialIn(
        @Query("loginId")loginId: String,
        @Query("workPlaceCd")workPlaceCd: String,
        @Query("transNo")transNo: String,
        @Query("transCarNo")transCarNo: String,
        @Query("transMan")transMan: String?,
        @Query("transManPhone")transManPhone: String?,
        @Query("labelNo")labelNo: String
    ): Call<GetLabelUpdateInfo>

    @GET("materialIn/getCardInfo")
    fun getMaterialCardInfo(
        @Query("millNo")millNo: String,
        @Query("inNo")inNo: String
    ): Call<GetMaterialCardInfo>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("user_id") userId: String,
        @Field("password") password: String
    ): Call<User>

    companion object Factory {
        fun create(): DDsteelApi {
            val uri = if (BuildConfig.DEBUG) "http://121.171.250.65/DDSTEEL_API/Developer/index.php/"
                //"http://121.171.250.65/DDSTEEL_API/Developer/index.php/"
            else "http://121.171.250.65/DDSTEEL_API/Real/index.php/"
            // local
            // "http://192.168.0.168/DDSTEEL_API/Developer/index.php/"

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
            return retrofit.create(DDsteelApi::class.java)
        }

        private fun httpLoggingInterceptor(): Interceptor {
            val interceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message ->
                Log.v("HTTP", message)
            })
            return interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        }
    }
}
