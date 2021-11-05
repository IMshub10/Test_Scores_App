package com.summer.math_and_go_assignment.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.summer.math_and_go_assignment.R
import com.summer.math_and_go_assignment.data.api.model.CreateTestDetail
import com.summer.math_and_go_assignment.data.api.model.Scores
import com.summer.math_and_go_assignment.data.local.LocalUserDataStorage
import com.summer.math_and_go_assignment.databinding.AddScoresFragmentBinding
import com.summer.math_and_go_assignment.ui.dialog.DatePickerDialog
import com.summer.math_and_go_assignment.utils.Constants
import com.summer.math_and_go_assignment.viewmodel.MainViewModel
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
    private lateinit var mainViewModel: MainViewModel
    private var testSeriesList = listOf<String>()

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
        fillTestSeries()
        listeners()
    }

    private fun fillTestSeries() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val testSeriesResponse = mainViewModel.getTestSeries()
                if (testSeriesResponse != null && !testSeriesResponse.error) {
                    testSeriesList = testSeriesResponse.testSeries
                    withContext(Dispatchers.Main) {
                        val arrayAdapter =
                            ArrayAdapter(requireContext(), R.layout.item_dropdown, testSeriesList)
                        binding.atvSelectTestSeries.setAdapter(arrayAdapter)
                        binding.pgAddScores.isVisible = false
                        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Internet is not available",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }
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
                if (validateTestDetail()) {
                    val createTestScoreResponse = mainViewModel.createTestScore(getTestDetail())
                    if (!createTestScoreResponse.error) {
                        requireActivity().supportFragmentManager.popBackStack()
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                requireContext(),
                                "Could not add this test Score",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Invalid Fields", Toast.LENGTH_SHORT)
                            .show()
                    }
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
            if (binding.etPhysicsYourScore.text.toString().trim().isEmpty()) {
                return false
            }
        }
        if (binding.cbChemistry.isChecked) {
            if (binding.etChemistryYourScore.text.toString().trim().isEmpty()) {
                return false
            }
        }
        if (binding.cbMath.isChecked) {
            if (binding.etMathYourScore.text.toString().trim().isEmpty()) {
                return false
            }
        }
        return true
    }

    private fun getTestDetail(): CreateTestDetail {
        val subjectScores: Scores = getTestSubjectScores()
        return CreateTestDetail(
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
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            AddTestDetailsFragment()
    }
}