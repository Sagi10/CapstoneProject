package com.lalee.capstoneproject.customVisionApiService

import com.lalee.capstoneproject.model.CustomVisionResult
import com.lalee.capstoneproject.model.JsonURL
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import java.io.File

interface CustomVisionApiSerivce {

    @POST("customvision/v3.0/Prediction/90e0c84c-4958-4875-9bda-20bd63aecaf1/classify/iterations/Iteration1/url")
    fun getPredictionFromURL(@Body body: JsonURL) : Call<CustomVisionResult>

    @POST("customvision/v3.0/Prediction/90e0c84c-4958-4875-9bda-20bd63aecaf1/classify/iterations/Iteration1/url")
    fun getPredictionFromFILE(@Body body: File) : Call<CustomVisionResult>
}
