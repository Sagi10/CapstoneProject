package com.lalee.capstoneproject.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.lalee.capstoneproject.model.JsonURL
import com.lalee.capstoneproject.model.TrashType
import com.lalee.capstoneproject.repository.MyFirebaseRepository
import kotlinx.coroutines.launch

class MyFirebaseViewModel(application: Application) : AndroidViewModel(application) {

    private val myFirebaseRepository = MyFirebaseRepository()

    private val _posts: LiveData<ArrayList<TrashType>> = myFirebaseRepository.posts
    private val _imageUrl: LiveData<JsonURL> = myFirebaseRepository.imageUrl

    val posts: LiveData<ArrayList<TrashType>> get() = _posts
    val imageUrl: LiveData<JsonURL> get() = _imageUrl

    fun getAllPosts() {
        viewModelScope.launch {
            try {
                myFirebaseRepository.getAllPosts()

            } catch (e: Throwable) {
                //error
            }
        }
    }

    fun uploadAndDownloadImageUrl(image: Uri) {
        viewModelScope.launch {
            try {
                myFirebaseRepository.uploadImage(image)
            } catch (e: Throwable) {
                //error
            }
        }
    }

    fun postTrashToFirebase(tagName: String, imageURL: String) {
        viewModelScope.launch {
            try {
                myFirebaseRepository.postTrashToFirebase(tagName, imageURL)
            } catch (e: Throwable) {
                //error
            }
        }
    }
}