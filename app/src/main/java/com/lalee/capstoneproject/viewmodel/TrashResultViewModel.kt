package com.lalee.capstoneproject.viewmodel

import android.app.Application
import android.content.ContentValues.TAG
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lalee.capstoneproject.model.TrashResult
import kotlinx.coroutines.launch

class TrashResultViewModel(application: Application) : AndroidViewModel(application) {

    private val _trashImage: MutableLiveData<Bitmap> = MutableLiveData()

    val trashImage = _trashImage

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