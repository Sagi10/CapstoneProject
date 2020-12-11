package com.lalee.capstoneproject.model

import com.google.gson.annotations.SerializedName
import com.lalee.capstoneproject.R

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


class TrashResult
    (
    val name: String,
    val image: Int
) {
    companion object {
        val NAMES = arrayOf(
            "plastic",
            "bottle"
        )

        val IMAGE = arrayOf(
            R.drawable.plastic_bottle,
            R.drawable.plastic_bottle
        )
    }
}