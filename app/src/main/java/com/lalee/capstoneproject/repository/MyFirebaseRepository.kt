package com.lalee.capstoneproject.repository

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.lalee.capstoneproject.model.JsonURL
import com.lalee.capstoneproject.model.TrashType

class MyFirebaseRepository {

    private val myFirebaseRefPosts = FirebaseDatabase.getInstance().reference.child("posts")
    private val myFirebaseRefTrash = FirebaseDatabase.getInstance().reference.child("trash")
    private val mStorageRef = FirebaseStorage.getInstance().reference;

    private val _posts: MutableLiveData<ArrayList<TrashType>> = MutableLiveData()
    private val _imageUrl: MutableLiveData<JsonURL> = MutableLiveData()

    val posts: LiveData<ArrayList<TrashType>> get() = _posts
    val imageUrl: LiveData<JsonURL> get() = _imageUrl

    fun deleteAllPosts(){
        myFirebaseRefPosts.removeValue()
    }

    fun getAllPosts() {
        myFirebaseRefPosts
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        try {
                            val posts = ArrayList<TrashType>()

                            for (item in snapshot.children) {
                                val post = TrashType(
                                    item.child("name").value.toString(),
                                    item.child("info").value.toString(),
                                    item.child("tip").value.toString(),
                                    item.child("verwerking").value.toString(),
                                    item.child("imageUrl").value.toString(),
                                    item.child("herbruikbaar").value.toString().toBoolean()
                                )
                                posts.add(post)
                            }

                            _posts.value = posts

                        } catch (e: Throwable) {
                            //error
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    //Error
                }

            })
    }

    fun uploadImage(image: Uri)  {
        // placement where i want to save to picture
        val imagesRef = mStorageRef.child("images/${image.lastPathSegment}")
        val uploadTask = imagesRef.putFile(image)

        // if uploading is succesfull download URL.
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            imagesRef.downloadUrl
        }
            // If URI creation of Firebase is succesfull send it to the prediction API.
            .addOnCompleteListener { task ->
                try {
                    if (task.isSuccessful) {

                        val downloadUri = task.result
                        val imageURI = JsonURL(downloadUri.toString())

                        // test image
                        val testImage1 =
                            JsonURL("https://firebasestorage.googleapis.com/v0/b/capstone-project-b4812.appspot.com/o/IMG_8757.jpg?alt=media&token=a96bdf63-542c-4789-96f8-61381d5f6e22")

                        val testImage2 =
                            JsonURL("https://firebasestorage.googleapis.com/v0/b/capstone-project-b4812.appspot.com/o/fles.jpg?alt=media&token=afec9430-f12f-47e1-84e0-7d2e15de0bdc")

                        val testImage3 =
                            JsonURL("https://firebasestorage.googleapis.com/v0/b/capstone-project-b4812.appspot.com/o/mondkapje.jpg?alt=media&token=dea8339e-fd58-482f-b401-0e108ba458d2")

                        _imageUrl.value = testImage2
                    }
                } catch (e: Throwable) {
                    //error
                }
            }
    }

    fun postTrashToFirebase(tagName: String, imageURL: String) {
        myFirebaseRefTrash
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        try {
                            for (item in snapshot.children) {
                                if (item.key == tagName) {
                                    val trash = TrashType(
                                        item.key.toString(),
                                        item.child("info").value.toString(),
                                        item.child("tip").value.toString(),
                                        item.child("verwerking").value.toString(),
                                        imageURL,
                                        item.child("herbruikbaar").value.toString().toBoolean()
                                    )
                                    postTrash(trash)
                                }
                            }
                        } catch (e: Throwable) {
                            //error tagname komt niet voor in database
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    //error database bestaat niet
                }
            })
    }

    fun postTrash(trash: TrashType) {
        myFirebaseRefPosts.push().setValue(trash)
    }

}