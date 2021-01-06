package com.lalee.capstoneproject.viewmodel

import android.app.Application
import android.content.ContentValues.TAG
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lalee.capstoneproject.model.CustomVisionPrediction
import com.lalee.capstoneproject.model.CustomVisionResult
import com.lalee.capstoneproject.model.JsonURL
import com.lalee.capstoneproject.repository.CustomVisionRepository
import kotlinx.coroutines.launch

class CustomVisionViewModel(application: Application) : AndroidViewModel(application) {

    private val customVisionRepository = CustomVisionRepository()

    val customVisionResult = customVisionRepository.customVisionResult
    val succesfullPrediction: MutableLiveData<Boolean> = MutableLiveData()

    fun getPredictionFromURL(imageURL: JsonURL) {
        viewModelScope.launch {
            try {
                customVisionRepository.getPredictionFromCustomVisionURL(imageURL)
            } catch (e: Throwable) {
                Log.e(TAG, e.message.toString())
            }
        }
    }

    fun checkPrediction(prediction: CustomVisionPrediction) {
        viewModelScope.launch {
            try {
                succesfullPrediction.value = prediction.probability > 0.75
            } catch (e: Throwable) {
                Log.e(TAG, e.message.toString())
            }
        }
    }
}