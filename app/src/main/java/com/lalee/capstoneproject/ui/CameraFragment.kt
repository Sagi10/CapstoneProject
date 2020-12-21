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
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.lalee.capstoneproject.R
import com.lalee.capstoneproject.model.CustomVisionPrediction
import com.lalee.capstoneproject.model.CustomVisionResult
import com.lalee.capstoneproject.model.JsonURL
import com.lalee.capstoneproject.model.TrashType
import com.lalee.capstoneproject.viewmodel.CustomVisionViewModel
import com.lalee.capstoneproject.viewmodel.MyFirebaseViewModel
import kotlinx.android.synthetic.main.fragment_camera.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


private const val REQUEST_IMAGE_CAPTURE = 1
lateinit var currentPhotoPath: String

class CameraFragment : Fragment() {

    private var mStorageRef: StorageReference? = null
    private val customVisionViewModel: CustomVisionViewModel by activityViewModels()
    private val myFirebaseViewModel: MyFirebaseViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mStorageRef = FirebaseStorage.getInstance().reference;

        dispatchTakePictureIntent()
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

        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun uploadImageToFirebaseStorage(image: Uri) {
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
                    val imageURI = JsonURL(downloadUri.toString())

                    // test image
                    val testImage =
                        JsonURL("https://firebasestorage.googleapis.com/v0/b/capstone-project-b4812.appspot.com/o/IMG_8757.jpg?alt=media&token=a96bdf63-542c-4789-96f8-61381d5f6e22")

                    customVisionViewModel.getPredictionFromURL(imageURI)

                    customVisionViewModel.customVisionResult.observe(viewLifecycleOwner, {

                        /* dit is eigenlijk niet nodig.
                        val customVisionResult = CustomVisionResult(
                            it.id,
                            it.project,
                            it.iteration,
                            it.created,
                            it.predictions
                        )
                        */

                        // for now only predictions is used. But the other info is also saved.
                        it.predictions.let { predictions ->
                            val prediction = CustomVisionPrediction(
                                // only use the first given response
                                predictions[0].probability,
                                predictions[0].tagId,
                                predictions[0].tagName
                            )

                            Log.i(TAG, "PREDICTION: $prediction")

                            if (prediction.probability > 0.75) {
                                // if succesvol get the info from firebase.
                                getInfoFromFirebase(prediction.tagName, imageURI.url)
                                pb_loading_posts.isVisible = false
                                findNavController().navigate(R.id.action_CameraFragment_to_resultFragment)

                            } else {
                                Toast.makeText(
                                    activity,
                                    "NIKS IS GEVONDEN WAT ER OP LIJKT. MAAK OPNIEUW EEN FOTO",
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                            }
                        }

                    })
                } else {
                    // Handle failures
                    // ...
                    Toast.makeText(activity, "NIET GELUKT MET UPLOADEN", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    private fun getInfoFromFirebase(tagName: String, imageURL: String) {
        var trash : TrashType?

        FirebaseDatabase.getInstance().reference.child("trash")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.exists()) {
                        try {
                            for (item in snapshot.children) {
                                if (item.key == tagName) {
                                    trash = TrashType(
                                        item.key.toString(),
                                        item.child("info").value.toString(),
                                        item.child("tip").value.toString(),
                                        item.child("verwerking").value.toString(),
                                        imageURL
                                    )
                                    Toast.makeText(
                                        activity,
                                        "HET BESTAAT: ${trash!!.name}",
                                        Toast.LENGTH_LONG
                                    )
                                        .show()
                                    Log.i(TAG, "FIREBASE INFO: ${trash!!.tip}")

                                    postIeks(trash!!)
                                }
                            }


                        } catch (e: Throwable) {
                            //error
                        }

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    //error
                }

            })

    }

    private fun postIeks(trash: TrashType) {
        Toast.makeText(activity, "POST AANGEROEPEN", Toast.LENGTH_LONG).show()
        FirebaseDatabase.getInstance().reference.child("posts").push().setValue(trash)
    }


}