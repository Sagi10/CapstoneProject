package com.lalee.capstoneproject.ui

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.lalee.capstoneproject.R
import com.lalee.capstoneproject.model.JsonURL
import com.lalee.capstoneproject.viewmodel.TrashResultViewModel
import kotlinx.android.synthetic.main.fragment_camera.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


private const val REQUEST_IMAGE_CAPTURE = 1
lateinit var currentPhotoPath: String

class CameraFragment : Fragment() {

    private var mStorageRef: StorageReference? = null
    private val customVisionViewModel: TrashResultViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mStorageRef = FirebaseStorage.getInstance().reference;
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dispatchTakePictureIntent()
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun dispatchTakePictureIntent() {

        val pictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        if (pictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            var photoFile: File? = null

            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                //error
            }

            if (photoFile != null) {
                val photoURI = FileProvider.getUriForFile(
                    requireContext(),
                    "com.lalee.capstoneproject.fileprovider",
                    photoFile
                )
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(pictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }

    }

    @SuppressLint("SimpleDateFormat")
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir) // directory)

        currentPhotoPath = image.absolutePath
        return image
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == AppCompatActivity.RESULT_OK) {

            val file = File(currentPhotoPath)
            Log.i(TAG, "FILE: ${file.absolutePath}")
            val image = Uri.fromFile(file)

            imageView.setImageURI(image)

            // placement where i want to save to picture
            val imagesRef = mStorageRef!!.child("images/${image.lastPathSegment}")
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
                    if (task.isSuccessful) {
                        val downloadUri = task.result

                        Toast.makeText(activity, downloadUri!!.toString(), Toast.LENGTH_LONG).show()
                        val imageURI = JsonURL(downloadUri.toString())
                        customVisionViewModel.getPredictionFromURL(imageURI)
                        customVisionViewModel.customVisionResult.observe(viewLifecycleOwner, {
                            Log.i(TAG, it.toString())
                        })
                    } else {
                        // Handle failures
                        // ...
                        Toast.makeText(activity, "NIET GELUKT MET UPLOADEN", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}