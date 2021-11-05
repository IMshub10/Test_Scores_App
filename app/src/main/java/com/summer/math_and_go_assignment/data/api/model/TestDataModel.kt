package com.summer.math_and_go_assignment.data.api.model

import com.squareup.moshi.Json

data class GetTestSeriesResponse(
    @Json(name = "error")
    var error: Boolean,

    @Json(name = "testSeries")
    var testSeries: List<String>
)

data class UpdateTestScore(
    @Json(name = "scores")
    var scores: Scores
)

data class Scores(
    @Json(name = "Physics")
    var Physics: Int?,

    @Json(name = "Chemistry")
    var Chemistry: Int?,

    @Json(name = "Mathematics")
    var Mathematics: Int?
)

data class GetTestScoresResponse(
    @Json(name = "error")
    var error: Boolean,

    @Json(name = "totalCount")
    var totalCount: Int,

    @Json(name = "count")
    var count: Int,

    @Json(name = "page")
    var page: Int,

    @Json(name = "limit")
    var limit: Int,

    @Json(name = "testScores")
    var testScores: List<TestScore>
)

data class TestScore(
    @Json(name = "scores")
    var scores: Scores,

    @Json(name = "_id")
    var _id: String,

    @Json(name = "email")
    var email: String,

    @Json(name = "exam")
    var exam: String,

    @Json(name = "testSeries")
    var testSeries: String,

    @Json(name = "testName")
    var testName: String,

    @Json(name = "testDate")
    var testDate: String,

    @Json(name = "createdAt")
    var createdAt: String,

    @Json(name = "updatedAt")
    var updatedAt: String,

    @Json(name = "__v")
    var __v: Int
)

data class CreateTestScore(
    @Json(name = "email")
    val email: String,

    @Json(name = "testSeries")
    val testSeries: String,

    @Json(name = "testName")
    val testName: String,

    @Json(name = "testDate")
    val testDate: String,

    @Json(name = "exam")
    val exam: String,

    @Json(name = "scores")
    val scores: Scores
)

data class CreateOrUpdateTestScoreResponse(
    @Json(name = "error")
    val error: Boolean,

    @Json(name = "testScore")
    val testScore: TestScore
)