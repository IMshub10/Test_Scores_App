package com.summer.math_and_go_assignment.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.summer.math_and_go_assignment.R
import com.summer.math_and_go_assignment.data.local.LocalUserDataStorage
import com.summer.math_and_go_assignment.databinding.AddScoresFragmentBinding
import com.summer.math_and_go_assignment.ui.dialog.DatePickerDialog
import com.summer.math_and_go_assignment.utils.Util
import com.summer.math_and_go_assignment.viewmodel.AddTestViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddTestDetailsFragment : Fragment() {

    private val TAG = "AddTestDetailsFragment"
    private lateinit var binding: AddScoresFragmentBinding
    private val addTestViewModel: AddTestViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        addTestViewModel.setUserEmail(LocalUserDataStorage.loadUserData(requireContext()).email!!)
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.add_scores_fragment,
            null,
            false
        )
        binding.lifecycleOwner = this
        binding.model = addTestViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        addTestViewModel.setTestSeriesList(requireContext())
        ifUpdateInitViewData()
        if (addTestViewModel.updateTestScore) {
            binding.atvSelectTestSeries.isEnabled = false
            addTestViewModel.pgAddScoresVisibility.value = false
            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
        setUpDataObservers()
        listeners()
    }

    private fun setUpDataObservers() {
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
                addTestViewModel.pgAddScoresVisibility.value = false
            } else {
                addTestViewModel.pgAddScoresVisibility.value = true
            }
        })
        addTestViewModel.createOrUpdateSuccess.observe(viewLifecycleOwner, {
            if (it) {
                requireActivity().supportFragmentManager.popBackStack()
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            } else {
                Util.showShortToast(requireContext(), "Could not create test score.")
            }
        })
        addTestViewModel.invalidToastData.observe(viewLifecycleOwner, {
            if (it != -1) {
                Util.showShortToast(requireContext(), "Invalid fields")
            }
        })
    }

    private fun openDatePicker() {
        val listener = object : DatePickerDialog.OnDatePickerDateSelected {
            override fun onDateSelected(date: String) {
                addTestViewModel.dateTakenOn.value = date
                Log.e(TAG, date)
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
            Log.e(TAG, "Physics called $isChecked")
            addTestViewModel.cbPhysicsIsChecked.value = isChecked
            if (isChecked) {
                binding.cbPhysics.alpha = 1.0f
                binding.etPhysicsYourScore.isEnabled = true
            } else {
                binding.cbPhysics.alpha = .33f
                binding.etPhysicsYourScore.isEnabled = false
            }
        }
        binding.cbChemistry.setOnCheckedChangeListener { _, isChecked ->
            addTestViewModel.cbChemistryIsChecked.value = isChecked
            Log.e(TAG, "Chem called $isChecked")
            if (isChecked) {
                binding.cbChemistry.alpha = 1.0f
                binding.etChemistryYourScore.isEnabled = true
            } else {
                binding.cbChemistry.alpha = .33f
                binding.etChemistryYourScore.isEnabled = false
            }
        }
        binding.cbMath.setOnCheckedChangeListener { _, isChecked ->
            Log.e(TAG, "Math called $isChecked")
            addTestViewModel.cbMathIsChecked.value = isChecked
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

        //EditText listener
        binding.etTakenOn.setOnClickListener {
            openDatePicker()
        }
    }

    private fun ifUpdateInitViewData() {
        arguments?.let {
            addTestViewModel.updateTestScore = it.getBoolean("update")
            if (addTestViewModel.updateTestScore) {
                addTestViewModel.testScoreId = it.getString("id")!!
                addTestViewModel.testSeries.value = it.getString("testSeries")!!
                addTestViewModel.testName.value = it.getString("testName")!!
                addTestViewModel.dateTakenOn.value = it.getString("takenOn")!!
                if (it.getString("physicsScore") != null) {
                    Log.e(TAG, it.getString("physicsScore")!!)
                    binding.cbPhysics.isChecked = true
                    addTestViewModel.cbPhysicsIsChecked.value = true
                    binding.cbPhysics.isEnabled = false
                    binding.cbPhysics.alpha = 1.0f
                    addTestViewModel.yourPhysicsScore.value = it.getString("physicsScore")
                    binding.etPhysicsYourScore.isEnabled = true
                }
                if (it.getString("chemistryScore") != null) {
                    Log.e(TAG, it.getString("chemistryScore")!!)
                    binding.cbChemistry.isChecked = true
                    addTestViewModel.cbChemistryIsChecked.value = true
                    binding.cbChemistry.isEnabled = false
                    binding.cbChemistry.alpha = 1.0f
                    addTestViewModel.yourChemistryScore.value = it.getString("chemistryScore")
                    binding.etChemistryYourScore.isEnabled = true
                }
                if (it.getString("mathScore") != null) {
                    Log.e(TAG, it.getString("mathScore")!!)
                    binding.cbMath.isChecked = true
                    addTestViewModel.cbMathIsChecked.value = true
                    binding.cbMath.alpha = 1.0f
                    binding.cbMath.isEnabled = false
                    addTestViewModel.yourMathScore.value = it.getString("mathScore")
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