package com.summer.math_and_go_assignment.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.summer.math_and_go_assignment.data.api.apiservice.TestDetailsApi
import com.summer.math_and_go_assignment.data.api.model.TestScore
import com.summer.math_and_go_assignment.utils.Constants
import retrofit2.HttpException
import java.io.IOException

class ScoresPagingSource(
    private val testDetailsApi: TestDetailsApi,
    private val emailId: String
) : PagingSource<Int, TestScore>() {
    override fun getRefreshKey(state: PagingState<Int, TestScore>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TestScore> {
        val position = params.key ?: Constants.STARTING_PAGE_INDEX
        return try {
            val response = testDetailsApi.getTestScores(emailId, position, params.loadSize)
            val testScoreList = response.testScores
            LoadResult.Page(
                data = testScoreList,
                prevKey = if (position == Constants.STARTING_PAGE_INDEX) null else position - 1,
                nextKey = if (testScoreList.isEmpty()) null else position + 1
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }
}