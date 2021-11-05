package com.summer.math_and_go_assignment.data.api.apiservice


import com.summer.math_and_go_assignment.data.api.model.*
import retrofit2.http.*

interface TestDetailsApi {
    @GET("test-series")
    suspend fun getTestSeries(
    ): GetTestSeriesResponse?

    @GET("test-scores")
    suspend fun getTestScores(
        @Query("email") email: String,
        @Query("page") apiKey: Int,
        @Query("limit") limit: Int

    ): GetTestScoresResponse

    @POST("test-scores")
    suspend fun createTestScore(
        @Body createTestScore: CreateTestScore
    ): CreateOrUpdateTestScoreResponse

    @DELETE("test-scores/{id}")
    suspend fun deleteTestScoreWithId(@Path("id") id: String)

    @PATCH("test-scores/{id}")
    suspend fun updateTestScore(
        @Path("id") id: String,
        @Body scores: UpdateTestScore
    ): CreateOrUpdateTestScoreResponse

}