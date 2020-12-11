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
import androidx.fragment.app.viewModels
import com.google.gson.Gson
import com.lalee.capstoneproject.R
import com.lalee.capstoneproject.model.JsonURL
import com.lalee.capstoneproject.viewmodel.TrashResultViewModel
import kotlinx.android.synthetic.main.fragment_camera.*
import kotlinx.android.synthetic.main.fragment_home.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Level.INFO

private const val REQUEST_IMAGE_CAPTURE = 1
lateinit var currentPhotoPath: String

class CameraFragment : Fragment() {

    private val customVisionViewModel: TrashResultViewModel by viewModels()

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

            val jj = JsonURL("https://firebasestorage.googleapis.com/v0/b/capstone-project-b4812.appspot.com/o/IMG_8757.jpg?alt=media&token=a96bdf63-542c-4789-96f8-61381d5f6e22")
            val jsonImage: String = Gson().toJson(jj)
            //Toast.makeText(activity, jsonImage, Toast.LENGTH_LONG).show()
            Log.i(TAG, jsonImage)

            customVisionViewModel.getPredictionFromURL(jj)
            //customVisionViewModel.getPredictionFromFILE(file.absoluteFile)
            customVisionViewModel.customVisionResult.observe(viewLifecycleOwner, {
                Toast.makeText(activity, it.id, Toast.LENGTH_SHORT).show()
            })
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}