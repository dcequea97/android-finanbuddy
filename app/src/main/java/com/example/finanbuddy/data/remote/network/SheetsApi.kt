package com.example.finanbuddy.data.remote.network

import com.example.finanbuddy.data.remote.model.CategoriesResponse
import com.example.finanbuddy.data.remote.model.SheetSaveData
import com.example.finanbuddy.data.remote.model.SheetsSummaryResponse
import com.example.finanbuddy.data.remote.model.SheetsTransactionsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface SheetsApi {
    @GET("exec")
    suspend fun getCategories(
//        @Query("month") month: Int = 1,
        @Query("action") action: String = "getCategories"
    ): Response<CategoriesResponse>

    @GET("exec")
    suspend fun getAllTransactions(
        @Query("month") month: Int,
        @Query("action") action: String = "getTransactions"
    ): Response<SheetsTransactionsResponse>

    @GET("exec")
    suspend fun getSummary(
        @Query("month") month: Int,
        @Query("action") action: String = "getSummary"
    ): Response<SheetsSummaryResponse>

    @POST("exec")
    suspend fun postDataToSheets(
        @Body data: SheetSaveData
    ): Response<Unit>
}