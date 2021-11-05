package com.summer.math_and_go_assignment.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.summer.math_and_go_assignment.R
import com.summer.math_and_go_assignment.data.api.model.DisplayItem
import com.summer.math_and_go_assignment.data.api.model.TestDetails
import com.summer.math_and_go_assignment.databinding.ItemTestScoreBinding
import com.summer.math_and_go_assignment.utils.Constants
import java.time.LocalDate

class TestScoresAdapter(private val context: Context) :
    PagingDataAdapter<TestDetails, TestScoresAdapter.TestScoreHolder>(
        DIFF_CALLBACK
    ) {
    private val TAG = "TestScoresAdapter"

    private lateinit var setOnMenuItemClick: SetOnMenuItemClick

    fun setOnMenuItemClickListener(setOnMenuItemClick: SetOnMenuItemClick) {
        this.setOnMenuItemClick = setOnMenuItemClick
    }

    inner class TestScoreHolder(private val binding: ItemTestScoreBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(testDetails: TestDetails) {
            //date conversion
            val displayItem = displayDetails(testDetails, layoutPosition)
            //bind details
            binding.apply {
                tvTestName.text = displayItem.testName
                tvDate.text = displayItem.testDate
                tvTestSeries.text = displayItem.testSeries
                tvPhysicsScore.text = displayItem.physicsScore
                tvChemistryScore.text = displayItem.chemistryScore
                tvMathScore.text = displayItem.mathScore
                tvTotalScore.text = displayItem.totalScore
            }
            binding.ivMenuUpdateDelete.setOnClickListener {
                popupMenus(it, testDetails)
            }
        }
    }

    private fun displayDetails(testDetails: TestDetails, position: Int): DisplayItem {
        val localDate =
            LocalDate.parse(testDetails.testDate, Constants.API_PROVIDING_DATE_FORMAT)
        val displayDate = Constants.DATE_DISPLAY_FORMAT.format(localDate)
        //scores text
        val physicsScoreText: String
        val chemistryScoreText: String
        val mathScoreText: String
        var totalFullScore = 0
        var totalYourScore = 0
        if (testDetails.scores.Physics != null) {
            physicsScoreText = "${testDetails.scores.Physics}/${Constants.TOTAL_SCORE}"
            totalYourScore += testDetails.scores.Physics!!
            totalFullScore += Constants.TOTAL_SCORE
        } else {
            physicsScoreText = "N.A"
        }
        if (testDetails.scores.Chemistry != null) {
            chemistryScoreText = "${testDetails.scores.Chemistry}/${Constants.TOTAL_SCORE}"
            totalYourScore += testDetails.scores.Chemistry!!
            totalFullScore += Constants.TOTAL_SCORE
        } else {
            chemistryScoreText = "N.A"
        }
        if (testDetails.scores.Mathematics != null) {
            mathScoreText = "${testDetails.scores.Mathematics}/${Constants.TOTAL_SCORE}"
            totalYourScore += testDetails.scores.Mathematics!!
            totalFullScore += Constants.TOTAL_SCORE
        } else {
            mathScoreText = "N.A"
        }

        val totalScoreText =
            "$totalYourScore/$totalFullScore"

        val testName = "${itemCount - position}. ${testDetails.testName}"
        return DisplayItem(
            testName,
            displayDate,
            testDetails.testSeries,
            physicsScoreText,
            chemistryScoreText,
            mathScoreText,
            totalScoreText
        )
    }

    companion object {
        private val DIFF_CALLBACK: DiffUtil.ItemCallback<TestDetails> =
            object : DiffUtil.ItemCallback<TestDetails>() {
                override fun areItemsTheSame(
                    oldItem: TestDetails,
                    newItem: TestDetails
                ): Boolean {
                    return oldItem._id == newItem._id
                }

                override fun areContentsTheSame(
                    oldItem: TestDetails,
                    newItem: TestDetails
                ): Boolean {
                    return oldItem.scores == newItem.scores
                            && oldItem.email == newItem.email
                            && oldItem.exam == newItem.exam
                            && oldItem.testSeries == newItem.testSeries
                            && oldItem.testName == newItem.testName
                            && oldItem.testDate == newItem.testDate
                            && oldItem.createdAt == newItem.createdAt
                            && oldItem.updatedAt == newItem.updatedAt
                }
            }
    }

    override fun onBindViewHolder(holder: TestScoreHolder, position: Int) {
        holder.bind(testDetails = getItem(position)!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestScoreHolder {
        return TestScoreHolder(
            ItemTestScoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    private fun popupMenus(view: View, testDetails: TestDetails) {
        val popupMenus = PopupMenu(context, view)
        popupMenus.inflate(R.menu.test_score_item_icons)
        popupMenus.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.edit -> {
                    Log.e(TAG, "Edit")
                    setOnMenuItemClick.onEdit(testDetails = testDetails)
                    true
                }
                R.id.delete -> {
                    Log.e(TAG, "Delete")
                    setOnMenuItemClick.onDelete(id = testDetails._id)
                    true
                }
                else -> {
                    true
                }
            }
        }
        popupMenus.show()
    }

    interface SetOnMenuItemClick {
        fun onEdit(testDetails: TestDetails)
        fun onDelete(id: String)
    }
}