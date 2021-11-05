package com.summer.math_and_go_assignment.viewmodel

import androidx.lifecycle.ViewModel
import com.summer.math_and_go_assignment.data.api.model.CreateTestDetail
import com.summer.math_and_go_assignment.data.api.model.GetTestSeriesResponse
import com.summer.math_and_go_assignment.data.local.LocalUserDataStorage
import com.summer.math_and_go_assignment.data.repository.TestScoresRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
public class MainViewModel @Inject constructor(private val testScoresRepository: TestScoresRepository) :
    ViewModel() {
    suspend fun getTestSeries(): GetTestSeriesResponse? = testScoresRepository.getTestSeries()


    suspend fun createTestScore(createTestDetail: CreateTestDetail) =
        testScoresRepository.createTestScore(createTestDetail)

    suspend fun deleteTestScore(id: String) =
        testScoresRepository.deleteTestScoreWithId(id)

    fun testScoresList(email: String) =
        testScoresRepository.getTestScoresPaging(email)
}