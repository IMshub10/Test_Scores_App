package com.summer.math_and_go_assignment.data.api.apiservice


import com.summer.math_and_go_assignment.data.api.model.CreateTestDetail
import com.summer.math_and_go_assignment.data.api.model.CreateTestDetailResponse
import com.summer.math_and_go_assignment.data.api.model.GetTestDetailsResponse
import com.summer.math_and_go_assignment.data.api.model.GetTestSeriesResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface TestDetailsApi {
    @GET("test-series")
    suspend fun getTestSeries(
    ): GetTestSeriesResponse?

    @GET("test-scores")
    suspend fun getTestScores(
        @Query("email") email: String,
        @Query("page") apiKey: Int,
        @Query("limit") limit: Int

    ): GetTestDetailsResponse

    @POST("test-scores")
    suspend fun createTestScore(
        @Body createTestDetail: CreateTestDetail
    ): CreateTestDetailResponse
}