package com.lalee.capstoneproject.ui

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.lalee.capstoneproject.R
import kotlinx.android.synthetic.main.fragment_camera.*
import kotlinx.android.synthetic.main.fragment_home.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

private const val REQUEST_IMAGE_CAPTURE = 1
lateinit var currentPhotoPath: String

class CameraFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireContext(),
                        "com.lalee.capstoneproject.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }

        // this is the old java style

//        val pictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//
//        if (pictureIntent.resolveActivity(requireActivity().packageManager) != null) {
//            var photoFile: File? = null
//
//            try {
//                photoFile = createImageFile()
//            } catch (ex: IOException) {
//                //error
//            }
//
//            if (photoFile != null) {
//                val photoURI = FileProvider.getUriForFile(
//                    requireContext(),
//                    "com.lalee.capstoneproject.fileprovider",
//                    photoFile
//                )
//                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
//                startActivityForResult(pictureIntent, REQUEST_IMAGE_CAPTURE)
//            }
//        }

    }


    @SuppressLint("SimpleDateFormat")
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }

    // this is the old java style

//        // Create an image file name
//        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
//        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//        val image = File.createTempFile(
//            "JPEG_${timeStamp}_",
//            ".jpg",
//            storageDir
//        ) // directory)
//        currentPhotoPath = image.absolutePath
//
//        return image
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == AppCompatActivity.RESULT_OK) {

            val file = File(currentPhotoPath)
            Log.i(TAG, "FILE: ${file.absolutePath}")

            imageView.setImageURI(Uri.fromFile(file))
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}