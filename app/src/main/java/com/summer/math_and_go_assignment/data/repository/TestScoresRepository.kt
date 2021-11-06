package com.summer.math_and_go_assignment.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.summer.math_and_go_assignment.data.api.apiservice.TestDetailsApi
import com.summer.math_and_go_assignment.data.api.model.CreateTestScore
import com.summer.math_and_go_assignment.data.api.model.GetTestSeriesResponse
import com.summer.math_and_go_assignment.data.api.model.UpdateTestScore
import javax.inject.Inject

class TestScoresRepository @Inject constructor(private val testDetailsApi: TestDetailsApi) {
    suspend fun getTestSeries(): GetTestSeriesResponse? = testDetailsApi.getTestSeries()

    suspend fun updateTestScore(id: String, updateTestScore: UpdateTestScore) =
        testDetailsApi.updateTestScore(id, updateTestScore)

    suspend fun createTestScore(createTestScore: CreateTestScore) =
        testDetailsApi.createTestScore(createTestScore)

    suspend fun deleteTestScoreWithId(id: String) =
        testDetailsApi.deleteTestScoreWithId(id)

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