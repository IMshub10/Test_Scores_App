package com.summer.math_and_go_assignment.data.api

import com.summer.math_and_go_assignment.data.api.apiservice.TestDetailsApi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Inject

class RetrofitBuilder @Inject constructor() {
    private val BASE_URL =
        "https://testscoretracker.herokuapp.com/"

    private var retrofit: Retrofit? = null

    private fun getRetrofit() =
        retrofit ?: Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()


    val testDetailsApi: TestDetailsApi =
        getRetrofit().create(TestDetailsApi::class.java)
}