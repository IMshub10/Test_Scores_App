package com.summer.math_and_go_assignment.di

import com.summer.math_and_go_assignment.data.api.RetrofitBuilder
import com.summer.math_and_go_assignment.data.api.apiservice.TestDetailsApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideNewsApiService(retrofitBuilder: RetrofitBuilder): TestDetailsApi {
        return retrofitBuilder.testDetailsApi
    }
}