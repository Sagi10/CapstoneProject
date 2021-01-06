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
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.lalee.capstoneproject.R
import com.lalee.capstoneproject.model.CustomVisionPrediction
import com.lalee.capstoneproject.viewmodel.CustomVisionViewModel
import com.lalee.capstoneproject.viewmodel.MyFirebaseViewModel
import kotlinx.android.synthetic.main.fragment_camera.*
import kotlinx.android.synthetic.main.fragment_camera.pb_loading_posts
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

private const val REQUEST_IMAGE_CAPTURE = 1
lateinit var currentPhotoPath: String

class CameraFragment : Fragment() {

    private val customVisionViewModel: CustomVisionViewModel by activityViewModels()
    private val myFirebaseViewModel: MyFirebaseViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dispatchTakePictureIntent()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_camera, container, false)
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
                    getString(R.string.file_provider),
                    photoFile
                )
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(pictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        } else {
            Toast.makeText(activity, getString(R.string.no_camera_found), Toast.LENGTH_SHORT)
                .show()
        }

    }

    @SuppressLint("SimpleDateFormat")
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            "PICTURE_${timeStamp}_",
            ".jpg",
            storageDir
        ) // directory)

        currentPhotoPath = image.absolutePath
        return image
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == AppCompatActivity.RESULT_OK) {

            val file = File(currentPhotoPath)
            val imageUri = Uri.fromFile(file)

            textView.isVisible = false
            pb_loading_posts.isVisible = true

            uploadImageToFirebaseStorage(imageUri)

        } else {
            //if camera app was cancelled navigate back.
            findNavController().popBackStack()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun uploadImageToFirebaseStorage(image: Uri) {
        myFirebaseViewModel.uploadAndDownloadImageUrl(image)
        myFirebaseViewModel.imageUrl.observe(viewLifecycleOwner, { imageUrl ->

            customVisionViewModel.getPredictionFromURL(imageUrl)
            customVisionViewModel.customVisionResult.observe(
                viewLifecycleOwner,
                { customVisionResult ->

                    customVisionViewModel.checkPrediction(customVisionResult.predictions[0])
                    customVisionViewModel.succesfullPrediction.observe(
                        viewLifecycleOwner,
                        { isSuccesful ->
                            if (isSuccesful) {
                                postTrashToFirebase(
                                    customVisionResult.predictions[0].tagName,
                                    imageUrl.url
                                )
                                findNavController().navigate(R.id.action_CameraFragment_to_ResultFragment)
                            } else {
                                Toast.makeText(activity, getText(R.string.no_prediction), Toast.LENGTH_SHORT).show()
                            }
                        })
                })
        })
    }

    private fun postTrashToFirebase(tagName: String, imageURL: String) {
        myFirebaseViewModel.postTrashToFirebase(tagName, imageURL)
    }


}