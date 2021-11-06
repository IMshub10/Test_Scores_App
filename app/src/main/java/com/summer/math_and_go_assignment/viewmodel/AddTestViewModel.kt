package com.summer.math_and_go_assignment.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.summer.math_and_go_assignment.data.api.model.CreateTestScore
import com.summer.math_and_go_assignment.data.api.model.GetTestSeriesResponse
import com.summer.math_and_go_assignment.data.api.model.Scores
import com.summer.math_and_go_assignment.data.api.model.UpdateTestScore
import com.summer.math_and_go_assignment.data.repository.TestScoresRepository
import com.summer.math_and_go_assignment.utils.Constants
import com.summer.math_and_go_assignment.utils.Util
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class AddTestViewModel @Inject constructor(
    private val testScoresRepository: TestScoresRepository
) :
    ViewModel() {
    private val TAG = "AddTestViewModel"
    private var userEmail: String = ""

    var testSeries = MutableLiveData("Select Text Series")
    var testName = MutableLiveData<String>()
    var dateTakenOn = MutableLiveData<String>()
    var yourPhysicsScore = MutableLiveData<String>()
    var yourChemistryScore = MutableLiveData<String>()
    var yourMathScore = MutableLiveData<String>()

    var pgAddScoresVisibility = MutableLiveData(false)
    var cbPhysicsIsChecked = MutableLiveData(false)
    var cbChemistryIsChecked = MutableLiveData(false)
    var cbMathIsChecked = MutableLiveData(false)
    var testSeriesList = listOf<String>()
    var updateTestScore: Boolean = false
    lateinit var testScoreId: String

    val fillTestSeriesStatus: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }
    val createOrUpdateSuccess: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val invalidToastData: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    private suspend fun getTestSeries(): GetTestSeriesResponse? =
        testScoresRepository.getTestSeries()

    private suspend fun updateTestScoreApi(id: String, updateTestScore: UpdateTestScore) =
        testScoresRepository.updateTestScore(id, updateTestScore)

    private suspend fun createTestScoreApi(createTestScore: CreateTestScore) =
        testScoresRepository.createTestScore(createTestScore)

    private fun setFillTestSeriesStatus(status: Boolean) {
        viewModelScope.launch(Dispatchers.Main) {
            fillTestSeriesStatus.value = status
        }
    }

    fun setUserEmail(userEmail: String) {
        this.userEmail = userEmail
    }

    fun onSaveClick() {
        pgAddScoresVisibility.value = true
        CoroutineScope(Dispatchers.IO).launch {
            if (!updateTestScore) {
                createTestScore()
            } else {
                updateTestScore()
            }
            withContext(Dispatchers.Main) {
                pgAddScoresVisibility.value = false
            }
        }
    }


    private fun getTestSubjectScores(): Scores {
        val physicsScore: Int? = if (cbPhysicsIsChecked.value!!) {
            yourPhysicsScore.value!!.toInt()
        } else {
            null
        }

        val chemistryScore: Int? = if (cbChemistryIsChecked.value!!) {
            yourChemistryScore.value!!.toInt()
        } else {
            null
        }

        val mathScore: Int? = if (cbMathIsChecked.value!!) {
            yourMathScore.value!!.toInt()
        } else {
            null
        }
        return Scores(physicsScore, chemistryScore, mathScore)
    }

    private suspend fun updateTestScore() {
        val atLeastOneSubjectSelected =
            cbPhysicsIsChecked.value!! || cbChemistryIsChecked.value!! || cbMathIsChecked.value!!
        if (atLeastOneSubjectSelected && validateScores()) {
            try {
                val updateResponse =
                    updateTestScoreApi(
                        testScoreId,
                        UpdateTestScore(getTestSubjectScores())
                    )
                withContext(Dispatchers.Main) {
                    createOrUpdateSuccess.value = !updateResponse.error
                }
            } catch (e: Exception) {
                Log.e(TAG + "updateError", e.toString())
            }
        } else {
            withContext(Dispatchers.Main) {
                invalidToastData.value = invalidToastData.value!! + 1
            }
        }
    }

    private suspend fun createTestScore() {
        if (validateTestDetail()) {
            try {
                Log.e(TAG, getTestDetail().toString())
                val createTestScoreResponse =
                    createTestScoreApi(getTestDetail())
                withContext(Dispatchers.Main) {
                    createOrUpdateSuccess.value = !createTestScoreResponse.error
                }
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        } else {
            withContext(Dispatchers.Main) {
                invalidToastData.value = invalidToastData.value!! + 1
            }
        }
    }

    private fun validateTestDetail(): Boolean {
        val atLeastOneSubjectSelected =
            cbPhysicsIsChecked.value!! || cbChemistryIsChecked.value!! || cbMathIsChecked.value!!
        return testSeries.value != "Select Text Series" && !testName.value.isNullOrEmpty() && !dateTakenOn.value.isNullOrEmpty() && atLeastOneSubjectSelected && validateScores()
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

    private fun validateScores(): Boolean {
        if (cbPhysicsIsChecked.value!!) {
            if (yourPhysicsScore.value != null) {
                if (yourPhysicsScore.value.toString().trim()
                        .isEmpty()
                ) {
                    return false
                } else if (yourPhysicsScore.value.toString().trim().toInt() > 100) {
                    return false
                }
            }
        }
        if (cbChemistryIsChecked.value!!) {
            if (yourChemistryScore.value != null) {
                if (yourChemistryScore.value.toString().trim()
                        .isEmpty()
                ) {
                    return false
                } else if (yourChemistryScore.value.toString().trim().toInt() > 100) {
                    return false
                }
            }
        }
        if (cbMathIsChecked.value!!) {
            if (yourMathScore.value != null) {
                if (yourMathScore.value.toString().trim()
                        .isEmpty()
                ) {
                    return false
                } else if (yourMathScore.value.toString().trim().toInt() > 100) {
                    return false
                }
            }
        }
        return true
    }

    private fun getTestDetail(): CreateTestScore {
        val subjectScores: Scores = getTestSubjectScores()
        return CreateTestScore(
            userEmail,
            testSeries.value!!.trim(),
            testName.value!!.trim(),
            dateTakenOn.value!!.trim(),
            Constants.EXAM_NAME,
            subjectScores
        )
    }

    init {
        fillTestSeriesStatus.value = false
        invalidToastData.value = -1
    }
}
