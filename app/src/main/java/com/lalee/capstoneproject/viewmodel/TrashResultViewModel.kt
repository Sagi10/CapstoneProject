package com.lalee.capstoneproject.viewmodel

import android.app.Application
import android.content.ContentValues.TAG
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lalee.capstoneproject.model.JsonURL
import com.lalee.capstoneproject.model.TrashResult
import com.lalee.capstoneproject.repository.CustomVisionRepository
import kotlinx.coroutines.launch
import java.io.File

class TrashResultViewModel(application: Application) : AndroidViewModel(application) {

    private val customVisionRepository = CustomVisionRepository()

    private val _trashImage: MutableLiveData<Bitmap> = MutableLiveData()

    val customVisionResult = customVisionRepository.customVisionResult
    val trashImage = _trashImage

    fun getPredictionFromURL(imageURL: JsonURL){
        viewModelScope.launch {
            try {
                customVisionRepository.getPredictionFromCustomVisionURL(imageURL)
            } catch (e: Throwable){
                Log.e(TAG, "ERROR met ophalen van prediction: ${e.message}")

            }
        }
    }

    fun getPredictionFromFILE(file: File){
        viewModelScope.launch {
            try {
                customVisionRepository.getPredictionFromCustomVisionFILE(file)
            } catch (e: Throwable){
                Log.e(TAG, "ERROR met ophalen van prediction: ${e.message}")

            }
        }
    }

    fun sendData(image: Bitmap){
        viewModelScope.launch {
            try {
                _trashImage.postValue(image)

            } catch (e: Throwable){
                Log.e(TAG, "ERROR IMAGE: ${e.message}")
            }
        }
    }
}