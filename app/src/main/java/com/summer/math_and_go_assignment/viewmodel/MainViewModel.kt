package com.summer.math_and_go_assignment.viewmodel

import androidx.lifecycle.ViewModel
import com.summer.math_and_go_assignment.data.api.model.CreateTestScore
import com.summer.math_and_go_assignment.data.api.model.GetTestSeriesResponse
import com.summer.math_and_go_assignment.data.api.model.Scores
import com.summer.math_and_go_assignment.data.api.model.UpdateTestScore
import com.summer.math_and_go_assignment.data.repository.TestScoresRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val testScoresRepository: TestScoresRepository) :
    ViewModel() {
    suspend fun getTestSeries(): GetTestSeriesResponse? = testScoresRepository.getTestSeries()

    suspend fun updateTestScore(id: String, updateTestScore: UpdateTestScore) =
        testScoresRepository.updateTestScore(id, updateTestScore)

    suspend fun createTestScore(createTestScore: CreateTestScore) =
        testScoresRepository.createTestScore(createTestScore)

    suspend fun deleteTestScore(id: String) =
        testScoresRepository.deleteTestScoreWithId(id)

    fun testScoresList(email: String) =
        testScoresRepository.getTestScoresPaging(email)
}