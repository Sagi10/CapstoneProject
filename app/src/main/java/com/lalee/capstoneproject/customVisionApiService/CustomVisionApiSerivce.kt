package com.lalee.capstoneproject.customVisionApiService

import com.lalee.capstoneproject.BuildConfig
import com.lalee.capstoneproject.model.CustomVisionResult
import com.lalee.capstoneproject.model.JsonURL
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import java.io.File

interface CustomVisionApiSerivce {

    @POST(BuildConfig.CUSTOMVISION_LINK)
    fun getPredictionFromURL(@Body body: JsonURL) : Call<CustomVisionResult>

    @POST(BuildConfig.CUSTOMVISION_LINK)
    fun getPredictionFromFILE(@Body body: File) : Call<CustomVisionResult>
}
