package com.summer.math_and_go_assignment.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.summer.math_and_go_assignment.R
import com.summer.math_and_go_assignment.data.api.model.TestScore
import com.summer.math_and_go_assignment.data.local.LocalUserDataStorage
import com.summer.math_and_go_assignment.databinding.ViewTestFragmentBinding
import com.summer.math_and_go_assignment.ui.adapter.TestScoresAdapter
import com.summer.math_and_go_assignment.utils.Util
import com.summer.math_and_go_assignment.viewmodel.ViewTestViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.Exception

@AndroidEntryPoint
class ViewTestDetailsFragment : Fragment() {
    private val TAG = "ViewTestDetailsFragment"
    private val viewTestViewModel: ViewTestViewModel by viewModels()
    private lateinit var binding: ViewTestFragmentBinding
    private lateinit var adapter: TestScoresAdapter

    companion object {
        fun newInstance() = ViewTestDetailsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ViewTestFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        setUpDataObservers()
        listeners()
    }

    private fun setUpDataObservers() {
        viewTestViewModel.testScoresList(LocalUserDataStorage.loadUserData(requireContext()).email!!)
            .observe(viewLifecycleOwner, {
                adapter.submitData(viewLifecycleOwner.lifecycle, it)
            })
        lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { loadStates ->
                binding.pgRefreshPage.isVisible = loadStates.refresh is LoadState.Loading
                binding.tvRetryTestDetail.isVisible = loadStates.refresh is LoadState.Error
                binding.tvError.isVisible = loadStates.refresh is LoadState.Error
            }
        }
    }


    private fun listeners() {
        binding.mbAddTestDetail.setOnClickListener {
            addTestFragment(false, null, null, null, null, null, null, null)
        }
        binding.tvRetryTestDetail.setOnClickListener {
            adapter.retry()
        }
        //recyclerview item click
        adapter.setOnMenuItemClickListener(object : TestScoresAdapter.SetOnMenuItemClick {
            override fun onEdit(testScore: TestScore) {
                addTestFragment(
                    true,
                    testScore._id,
                    testScore.testSeries,
                    testScore.testName,
                    Util.getLocalDate(testScore.testDate),
                    testScore.scores.Physics,
                    testScore.scores.Chemistry,
                    testScore.scores.Mathematics
                )
            }

            override fun onDelete(id: String) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        viewTestViewModel.deleteTestScore(id)
                        adapter.refresh()
                    } catch (e: Exception) {
                        Log.e(TAG, e.toString())
                    }
                }
            }
        })

    }

    //Switch to AddTestDetailsFragment
    private fun addTestFragment(
        update: Boolean,
        id: String?,
        testSeries: String?,
        testName: String?,
        takenOn: String?,
        physicsScore: Int?,
        chemistryScore: Int?,
        mathScore: Int?
    ) {
        val transaction =
            requireActivity().supportFragmentManager.beginTransaction()
        transaction.addToBackStack(null)
        transaction
            .replace(
                R.id.container,
                AddTestDetailsFragment.newInstance(
                    update,
                    id,
                    testSeries,
                    testName,
                    takenOn,
                    physicsScore,
                    chemistryScore,
                    mathScore
                )
            ).commit()
    }

    private fun initViews() {
        adapter = TestScoresAdapter(requireContext())
        binding.apply {
            rvMain.adapter = adapter
            rvMain.layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        }
    }
}