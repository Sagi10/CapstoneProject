package com.lalee.capstoneproject

import android.app.AlertDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.google.firebase.database.FirebaseDatabase
import com.lalee.capstoneproject.viewmodel.MyFirebaseViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_result.*

class MainActivity : AppCompatActivity() {

    private var navController: Int = R.id.nav_host_fragment
    private val myFirebaseViewModel: MyFirebaseViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        fab_camera_home.setOnClickListener {
            findNavController(navController).navigate(R.id.action_HomeFragment_to_CameraFragment)
        }
        fabToggler()
    }

    private fun fabToggler() {
        findNavController(navController).addOnDestinationChangedListener { _, destination, _ ->
            if ((destination.id in arrayOf(R.id.CameraFragment) || destination.id in arrayOf(R.id.ResultFragment))) {
                fab_camera_home.hide()
            } else {
                fab_camera_home.show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        switchMenu(menu)
        return true
    }

    private fun switchMenu(menu: Menu) {
        findNavController(navController).addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.HomeFragment -> {
                    menu.findItem(R.id.action_settings_delete).isVisible = false
                    menu.findItem(R.id.action_history).isVisible = true
                }
                R.id.CameraFragment -> {
                    menu.findItem(R.id.action_settings_delete).isVisible = false
                    menu.findItem(R.id.action_history).isVisible = false
                }
                R.id.ResultFragment -> {
                    menu.findItem(R.id.action_settings_delete).isVisible = true
                    menu.findItem(R.id.action_history).isVisible = false
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_history -> {
                findNavController(navController).navigate(R.id.ResultFragment)
                true
            }
            R.id.action_settings_delete -> {
                //TODO move this in the firebase repo
                if (!myFirebaseViewModel.posts.value.isNullOrEmpty()) {

                    //TODO change this is material dialog
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage("Are you sure you want to DELETE?")
                        .setCancelable(false)
                        .setPositiveButton("YES") { _, _ ->
                            // Delete history
                            FirebaseDatabase.getInstance().reference.child("posts").removeValue()
                            myFirebaseViewModel.posts.value?.clear()
                            findNavController(navController).navigate(R.id.action_ResultFragment_to_HomeFragment)
                            Toast.makeText(
                                applicationContext,
                                "HISTORY DELETED",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .setNegativeButton("NO") { dialog, _ ->
                            // Dismiss the dialog
                            dialog.dismiss()
                        }
                    val alert = builder.create()
                    alert.show()
                } else {
                    Toast.makeText(
                        applicationContext,
                        "THERE ARE NO ITEMS TO BE DELETED",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        //only let the user use de back button when the camera fragment is open.
        findNavController(navController).addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.CameraFragment) {
                super.onBackPressed()
            }
        }
    }
}