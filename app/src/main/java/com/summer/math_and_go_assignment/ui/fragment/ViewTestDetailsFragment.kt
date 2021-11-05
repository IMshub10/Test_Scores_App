package com.summer.math_and_go_assignment.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.summer.math_and_go_assignment.R
import com.summer.math_and_go_assignment.data.api.model.TestDetails
import com.summer.math_and_go_assignment.data.local.LocalUserDataStorage
import com.summer.math_and_go_assignment.databinding.MainFragmentBinding
import com.summer.math_and_go_assignment.ui.adapter.TestScoresAdapter
import com.summer.math_and_go_assignment.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.Exception

@AndroidEntryPoint
class ViewTestDetailsFragment : Fragment() {
    private val TAG = "ViewTestDetailsFragment"
    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: MainFragmentBinding
    private lateinit var adapter: TestScoresAdapter

    companion object {
        fun newInstance() = ViewTestDetailsFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        initViews()
        mainViewModel.testScoresList(LocalUserDataStorage.loadUserData(requireContext()).email!!)
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
        listeners()
    }

    private fun listeners() {
        binding.mbAddTestDetail.setOnClickListener {
            val transaction =
                requireActivity().supportFragmentManager.beginTransaction()
            transaction.addToBackStack(null)
            transaction
                .replace(R.id.container, AddTestDetailsFragment.newInstance()).commit()
        }
        //scroll listener
        binding.rvMain.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                binding.mbAddTestDetail.isVisible =
                    !(newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING)
            }
        })
        //recyclerview item click
        adapter.setOnMenuItemClickListener(object : TestScoresAdapter.SetOnMenuItemClick {
            override fun onEdit(testDetails: TestDetails) {
            }

            override fun onDelete(id: String) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        mainViewModel.deleteTestScore(id)
                        adapter.refresh()
                    } catch (e: Exception) {
                        Log.e(TAG, e.toString())
                    }
                }
            }
        })
    }

    private fun initViews() {
        adapter = TestScoresAdapter(requireContext())
        binding.apply {
            rvMain.adapter = adapter
            rvMain.layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        }
    }

    private fun initViewModel() {
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
    }

}