package com.summer.math_and_go_assignment.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.summer.math_and_go_assignment.data.api.apiservice.TestDetailsApi
import com.summer.math_and_go_assignment.data.api.model.CreateTestDetail
import com.summer.math_and_go_assignment.data.api.model.GetTestSeriesResponse
import javax.inject.Inject

class TestScoresRepository @Inject constructor(private val testDetailsApi: TestDetailsApi) {
    suspend fun getTestSeries(): GetTestSeriesResponse? = testDetailsApi.getTestSeries()

    suspend fun getTestScores(email: String, page: Int, limit: Int) =
        testDetailsApi.getTestScores(email, page, limit)

    suspend fun createTestScore(createTestDetail: CreateTestDetail) =
        testDetailsApi.createTestScore(createTestDetail)

    fun getTestScoresPaging(email: String) =
        Pager(
            config = PagingConfig(
                pageSize = 8,
                maxSize = 104,
                enablePlaceholders = false,
                prefetchDistance = 4,
                initialLoadSize = 8
            ),
            pagingSourceFactory = {
                ScoresPagingSource(testDetailsApi, email)
            }
        ).liveData
}