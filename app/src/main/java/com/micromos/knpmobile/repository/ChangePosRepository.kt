package com.micromos.knpmobile.repository

import androidx.lifecycle.LiveData
import com.micromos.knpmobile.dto.GetPosCd
import com.micromos.knpmobile.network.ApiResult

interface ChangePosRepository {
    fun sendRequestPosCd(labelNo : String, resultCallback: ApiResult)
    fun getPosCd() : LiveData<GetPosCd>
    fun changePosCd(codeCd : String, user_id : String, coilNo : String, coilSeq : String, stkType : String, resultCallback: ApiResult)
}