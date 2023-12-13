package edu.vt.smarttrail

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class LocationPermissionActivity : AppCompatActivity() {

    companion object {
        private const val MY_PERMISSIONS_REQUEST_LOCATION = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_permission)

        // Creates an action bar, where back button is.
        val actionBar = supportActionBar

        // Creates back button.
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        val requestButton = findViewById<View>(R.id.request_location) as Button
        val agreeCheck = findViewById<View>(R.id.location_check) as CheckBox
        val readCheck = findViewById<View>(R.id.location_check_terms) as CheckBox
        requestButton.setOnClickListener{
            if (agreeCheck.isChecked && readCheck.isChecked) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this@LocationPermissionActivity, "Location feature is already enabled.", Toast.LENGTH_LONG).show()
                }
                else {
                    // Ask for permission
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        MY_PERMISSIONS_REQUEST_LOCATION
                    )
                }
            }
            else {
                Toast.makeText(this@LocationPermissionActivity, "Please indicate you agree to the terms and policies.", Toast.LENGTH_LONG).show()
            }
        }

        val backtoScreenUsagePermission = findViewById<View>(R.id.back_to_request_screen_usage) as Button
        backtoScreenUsagePermission.setOnClickListener()
        {
            val intent = Intent(this, ScreentimePermissionActivity::class.java)
            startActivity(intent)
        }



    }
}