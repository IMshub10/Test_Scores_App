package com.summer.math_and_go_assignment.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.summer.math_and_go_assignment.R
import com.summer.math_and_go_assignment.data.api.model.CreateOrUpdateTestScoreResponse
import com.summer.math_and_go_assignment.data.api.model.CreateTestScore
import com.summer.math_and_go_assignment.data.api.model.Scores
import com.summer.math_and_go_assignment.data.api.model.UpdateTestScore
import com.summer.math_and_go_assignment.data.local.LocalUserDataStorage
import com.summer.math_and_go_assignment.databinding.AddScoresFragmentBinding
import com.summer.math_and_go_assignment.ui.dialog.DatePickerDialog
import com.summer.math_and_go_assignment.utils.Constants
import com.summer.math_and_go_assignment.utils.Util
import com.summer.math_and_go_assignment.viewmodel.AddTestViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

@AndroidEntryPoint
class AddTestDetailsFragment : Fragment() {

    private val TAG = "AddTestDetailsFragment"
    private lateinit var binding: AddScoresFragmentBinding
    private lateinit var addTestViewModel: AddTestViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AddScoresFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        initViewModel()
        ifUpdateInitViewData()
        if (addTestViewModel.updateTestScore) {
            binding.atvSelectTestSeries.isEnabled = false
            binding.pgAddScores.isVisible = false
            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
        listeners()
        observers()
    }

    private fun observers() {
        addTestViewModel.fillTestSeriesStatus.observe(viewLifecycleOwner, {
            if (it) {
                val arrayAdapter =
                    ArrayAdapter(
                        requireContext(),
                        R.layout.item_dropdown,
                        addTestViewModel.testSeriesList
                    )
                binding.atvSelectTestSeries.setAdapter(arrayAdapter)
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                binding.pgAddScores.isVisible = false
            } else {
                binding.pgAddScores.isVisible = true
            }
        })
    }


    private fun openDatePicker() {
        val listener = object : DatePickerDialog.OnDatePickerDateSelected {
            override fun onDateSelected(date: String) {
                binding.etTakenOn.setText(date)
            }
        }
        requireActivity().supportFragmentManager.let {
            DatePickerDialog.newInstance(Bundle()).apply {
                show(it, tag)
                setOnDateSelectedListener(listener)
            }
        }
    }

    private fun listeners() {
        //checkbox listeners
        binding.cbPhysics.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.cbPhysics.alpha = 1.0f
                binding.etPhysicsYourScore.isEnabled = true
            } else {
                binding.cbPhysics.alpha = .33f
                binding.etPhysicsYourScore.isEnabled = false
            }
        }
        binding.cbChemistry.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.cbChemistry.alpha = 1.0f
                binding.etChemistryYourScore.isEnabled = true
            } else {
                binding.cbChemistry.alpha = .33f
                binding.etChemistryYourScore.isEnabled = false
            }
        }
        binding.cbMath.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.cbMath.alpha = 1.0f
                binding.etMathYourScore.isEnabled = true
            } else {
                binding.cbMath.alpha = .33f
                binding.etMathYourScore.isEnabled = false
            }
        }

        //button listeners
        binding.exitFrag.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }

        binding.mbSaveTestScore.setOnClickListener {
            binding.pgAddScores.isVisible = true
            CoroutineScope(Dispatchers.IO).launch {
                if (!addTestViewModel.updateTestScore) {
                    createTestScore()
                } else {
                    updateTestScore()
                }
                withContext(Dispatchers.Main) {
                    binding.pgAddScores.isVisible = false
                }
            }
        }

        //EditText listener
        binding.etTakenOn.setOnClickListener {
            openDatePicker()
        }
    }

    private suspend fun updateTestScore() {
        val atLeastOneSubjectSelected =
            binding.cbPhysics.isChecked || binding.cbChemistry.isChecked || binding.cbMath.isChecked
        if (atLeastOneSubjectSelected && validateScores()) {
            try {
                val updateResponse =
                    addTestViewModel.updateTestScore(
                        addTestViewModel.testScoreId,
                        UpdateTestScore(getTestSubjectScores())
                    )
                createOrUpdateSuccess(updateResponse)
            } catch (e: Exception) {
                Log.e(TAG + "updateError", e.toString())
            }
        } else {
            withContext(Dispatchers.Main) {
                Util.showShortToast(
                    requireContext(),
                    "Invalid Fields"
                )
            }
        }
    }

    private suspend fun createTestScore() {
        if (validateTestDetail()) {
            val createTestScoreResponse =
                addTestViewModel.createTestScore(getTestDetail())
            createOrUpdateSuccess(createTestScoreResponse)
        } else {
            withContext(Dispatchers.Main) {
                Util.showShortToast(
                    requireContext(),
                    "Invalid Fields"
                )
            }
        }
    }

    private fun createOrUpdateSuccess(createOrUpdateTestScoreResponse: CreateOrUpdateTestScoreResponse) {
        if (!createOrUpdateTestScoreResponse.error) {
            requireActivity().supportFragmentManager.popBackStack()
        } else {
            CoroutineScope(Dispatchers.Main).launch {
                Util.showShortToast(
                    requireContext(),
                    "Could not add this test score"
                )
            }
        }
    }

    private fun validateTestDetail(): Boolean {
        val testSeries = binding.atvSelectTestSeries.text.toString()
        val testName = binding.etTestName.text.toString()
        val testTakenOnDate = binding.etTakenOn.text.toString()
        val atLeastOneSubjectSelected =
            binding.cbPhysics.isChecked || binding.cbChemistry.isChecked || binding.cbMath.isChecked
        return testSeries != "Select Text Series" && testName.isNotEmpty() && testTakenOnDate.isNotEmpty() && atLeastOneSubjectSelected && validateScores()
    }

    private fun validateScores(): Boolean {
        if (binding.cbPhysics.isChecked) {
            if (binding.etPhysicsYourScore.text.toString().trim()
                    .isEmpty()
            ) {
                return false
            } else if (binding.etPhysicsYourScore.text.toString().trim().toInt() > 100) {
                return false
            }
        }
        if (binding.cbChemistry.isChecked) {
            if (binding.etChemistryYourScore.text.toString().trim()
                    .isEmpty()
            ) {
                return false
            } else if (binding.etChemistryYourScore.text.toString().trim().toInt() > 100) {
                return false
            }
        }
        if (binding.cbMath.isChecked) {
            if (binding.etMathYourScore.text.toString().trim()
                    .isEmpty()
            ) {
                return false
            } else if (binding.etMathYourScore.text.toString().trim().toInt() > 100) {
                return false
            }
        }
        return true
    }

    private fun getTestDetail(): CreateTestScore {
        val subjectScores: Scores = getTestSubjectScores()
        return CreateTestScore(
            LocalUserDataStorage.loadUserData(requireContext()).email!!,
            binding.atvSelectTestSeries.text.toString(),
            binding.etTestName.text!!.trim().toString(),
            binding.etTakenOn.text!!.trim().toString(),
            Constants.EXAM_NAME,
            subjectScores
        )
    }

    private fun getTestSubjectScores(): Scores {
        val physicsScore: Int? = if (binding.cbPhysics.isChecked) {
            binding.etPhysicsYourScore.text.toString().toInt()
        } else {
            null
        }

        val chemistryScore: Int? = if (binding.cbChemistry.isChecked) {
            binding.etChemistryYourScore.text.toString().toInt()
        } else {
            null
        }

        val mathScore: Int? = if (binding.cbMath.isChecked) {
            binding.etMathYourScore.text.toString().toInt()
        } else {
            null
        }
        return Scores(physicsScore, chemistryScore, mathScore)
    }

    private fun initViewModel() {
        addTestViewModel = ViewModelProvider(requireActivity())[AddTestViewModel::class.java]
        addTestViewModel.setTestSeriesList(requireContext())
    }

    private fun ifUpdateInitViewData() {
        arguments?.let {
            addTestViewModel.updateTestScore = it.getBoolean("update")
            if (addTestViewModel.updateTestScore) {
                addTestViewModel.testScoreId = it.getString("id")!!
                binding.atvSelectTestSeries.setText(it.getString("testSeries")!!)
                binding.etTestName.setText(it.getString("testName")!!)
                binding.etTakenOn.setText(it.getString("takenOn")!!)
                if (it.getString("physicsScore") != null) {
                    binding.cbPhysics.isChecked = true
                    binding.cbPhysics.isEnabled = false
                    binding.cbPhysics.alpha = 1.0f
                    binding.etPhysicsYourScore.setText(it.getString("physicsScore"))
                    binding.etPhysicsYourScore.isEnabled = true
                }
                if (it.getString("chemistryScore") != null) {
                    binding.cbChemistry.isChecked = true
                    binding.cbChemistry.isEnabled = false
                    binding.cbChemistry.alpha = 1.0f
                    binding.etChemistryYourScore.setText(it.getString("chemistryScore"))
                    binding.etChemistryYourScore.isEnabled = true
                }
                if (it.getString("mathScore") != null) {
                    binding.cbMath.isChecked = true
                    binding.cbMath.alpha = 1.0f
                    binding.cbMath.isEnabled = false
                    binding.etMathYourScore.setText(it.getString("mathScore"))
                    binding.etMathYourScore.isEnabled = true
                }
                binding.atvSelectTestSeries.isEnabled = false
                binding.etTestName.isEnabled = false
                binding.etTakenOn.isEnabled = false
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(
            update: Boolean,
            id: String?,
            testSeries: String?,
            testName: String?,
            takenOn: String?,
            physicsScore: Int?,
            chemistryScore: Int?,
            mathScore: Int?
        ) =
            AddTestDetailsFragment().apply {
                arguments = Bundle().apply {
                    putBoolean("update", update)
                    putString("id", id)
                    putString("testSeries", testSeries)
                    putString("testName", testName)
                    putString("takenOn", takenOn)
                    if (physicsScore != null) {
                        putString("physicsScore", physicsScore.toString())
                    } else {
                        putString("physicsScore", null)
                    }
                    if (chemistryScore != null) {
                        putString("chemistryScore", chemistryScore.toString())
                    } else {
                        putString("chemistryScore", null)
                    }
                    if (mathScore != null) {
                        putString("mathScore", mathScore.toString())
                    } else {
                        putString("mathScore", null)
                    }
                }
            }
    }
}