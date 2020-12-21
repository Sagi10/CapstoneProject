package com.lalee.capstoneproject.repository

import android.content.ContentValues.TAG
import android.nfc.Tag
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lalee.capstoneproject.customVisionApiService.CustomVisionApi
import com.lalee.capstoneproject.customVisionApiService.CustomVisionApiSerivce
import com.lalee.capstoneproject.model.CustomVisionResult
import com.lalee.capstoneproject.model.JsonURL
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class CustomVisionRepository {

    private val customVisionRepository: CustomVisionApiSerivce = CustomVisionApi.createAPI()

    private val _customVisionResult: MutableLiveData<CustomVisionResult> = MutableLiveData()

    val customVisionResult: LiveData<CustomVisionResult> get() = _customVisionResult

    fun getPredictionFromCustomVisionURL(imageURL: JsonURL){
        try {
            val result = customVisionRepository.getPredictionFromURL(imageURL)

            result.enqueue(object: Callback<CustomVisionResult> {
                override fun onResponse(call: Call<CustomVisionResult>, response: Response<CustomVisionResult>
                ) {
                    if (response.isSuccessful){
                        _customVisionResult.value = response.body()
                        Log.d(TAG, "DIT IS DE RESULT: ${response.body()}")
                    }
                }

                override fun onFailure(call: Call<CustomVisionResult>, t: Throwable) {
                    Log.e(TAG, "ERROR MET CALL: ${t.message}")
                }

            })

        } catch (e: Throwable){
            Log.e(TAG, "ERRORRRRRRRR: ${e.message}")
        }
    }

}