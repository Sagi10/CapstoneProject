package com.lalee.capstoneproject.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lalee.capstoneproject.model.TrashType

class MyFirebaseViewModel(application: Application): AndroidViewModel(application) {

    private val _posts: MutableLiveData<ArrayList<TrashType>> = MutableLiveData()

    val posts: LiveData<ArrayList<TrashType>> get() = _posts

    fun getAllPosts(){
        FirebaseDatabase.getInstance().reference.child("posts")
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        try {
                            val posts = ArrayList<TrashType>()

                            for (item in snapshot.children){
                                val post = TrashType(
                                    item.child("name").value.toString(),
                                    item.child("info").value.toString(),
                                    item.child("tip").value.toString(),
                                    item.child("verwerking").value.toString(),
                                    item.child("imageUrl").value.toString()
                                )
                                posts.add(post)
                            }

                            _posts.value = posts

                        } catch (e: Throwable){
                            //error
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    //Error
                }

            })
    }
}