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
    private val _predictionName = MutableLiveData<CustomVisionPrediction>()

    val customVisionResult = customVisionRepository.customVisionResult
    val predictionName: LiveData<CustomVisionPrediction> get() = _predictionName
    val succes: MutableLiveData<Boolean> = MutableLiveData()

    fun getPredictionFromURL(imageURL: JsonURL) {
        viewModelScope.launch {
            try {
                customVisionRepository.getPredictionFromCustomVisionURL(imageURL)
            } catch (e: Throwable) {
                Log.e(TAG, "ERROR met ophalen van prediction: ${e.message}")
            }
        }
    }

    fun getNameFromPrediction(prediction: List<CustomVisionPrediction>) {
        viewModelScope.launch {
            try {
                if (prediction[0].probability > 0.75){
                    _predictionName.value!!.tagName = prediction[0].tagName
                   succes.value = true
                }

            } catch (e: Throwable) {

            }
        }
    }
}