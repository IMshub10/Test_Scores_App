package com.summer.math_and_go_assignment.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.summer.math_and_go_assignment.data.api.model.CreateTestScore
import com.summer.math_and_go_assignment.data.api.model.GetTestSeriesResponse
import com.summer.math_and_go_assignment.data.api.model.UpdateTestScore
import com.summer.math_and_go_assignment.data.repository.TestScoresRepository
import com.summer.math_and_go_assignment.utils.Util
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class AddTestViewModel @Inject constructor(private val testScoresRepository: TestScoresRepository) :
    ViewModel() {
    var testSeriesList = listOf<String>()
    var updateTestScore: Boolean = false
    lateinit var testScoreId: String

    val fillTestSeriesStatus: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    private suspend fun getTestSeries(): GetTestSeriesResponse? =
        testScoresRepository.getTestSeries()

    suspend fun updateTestScore(id: String, updateTestScore: UpdateTestScore) =
        testScoresRepository.updateTestScore(id, updateTestScore)

    suspend fun createTestScore(createTestScore: CreateTestScore) =
        testScoresRepository.createTestScore(createTestScore)

    private fun setFillTestSeriesStatus(status: Boolean) {
        viewModelScope.launch(Dispatchers.Main) {
            fillTestSeriesStatus.value = status
        }
    }


    fun setTestSeriesList(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val testSeriesResponse = getTestSeries()
                if (testSeriesResponse != null && !testSeriesResponse.error) {
                    testSeriesList = testSeriesResponse.testSeries
                    setFillTestSeriesStatus(true)
                } else {
                    setFillTestSeriesStatus(false)
                }
            } catch (e: Exception) {
                setFillTestSeriesStatus(false)
                withContext(Dispatchers.Main) {
                    Util.showShortToast(
                        context,
                        "Internet is not available"
                    )
                }
            }
        }
    }

    init {
        fillTestSeriesStatus.value = false
    }
}