package com.lalee.capstoneproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.navigation.findNavController
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var navController: Int = R.id.nav_host_fragment

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
                Toast.makeText(applicationContext, "HISTORY DELETED", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        //removed super function to disable the back button.
    }
}