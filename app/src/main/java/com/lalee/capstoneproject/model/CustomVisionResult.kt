package com.lalee.capstoneproject.model

import com.google.gson.annotations.SerializedName

data class CustomVisionResult(
    @SerializedName("id") var id: String,
    @SerializedName("project") var project: String,
    @SerializedName("iteration") var iteration: String,
    @SerializedName("created") var created: String,
    @SerializedName("predictions") var predictions: List<CustomVisionPrediction>
)

data class CustomVisionPrediction(
    @SerializedName("probability") var probability: Double,
    @SerializedName("tagId") var tagId: String,
    @SerializedName("tagName") var tagName: String
)

data class JsonURL(
    @SerializedName("Url") var url: String
)