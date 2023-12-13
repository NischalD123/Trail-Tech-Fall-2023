package edu.vt.smarttrail

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button


class PermissionsActivity : AppCompatActivity() {

    companion object {
        private const val MY_PERMISSIONS_REQUEST_LOCATION = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permissions)

        val actionBar = supportActionBar

        // Creates back button.
        actionBar!!.setDisplayHomeAsUpEnabled(true)



        // Button to send to Location request page
        val permissionLocationPage = findViewById<View>(R.id.check_GPS) as Button
        permissionLocationPage.setOnClickListener{
            val intent = Intent(this, edu.vt.smarttrail.LocationPermissionActivity::class.java)
            startActivity(intent)
        }

        // Button to send to Screentime request page
        val screentimePage = findViewById<View>(R.id.check_Screentime) as Button
        screentimePage.setOnClickListener{
            val intent = Intent(this, edu.vt.smarttrail.ScreentimePermissionActivity::class.java)
            startActivity(intent)
        }

        // Checkbox for Survey
//        val surveyCheck = findViewById<View>(R.id.check_GPS)
//        surveyCheck.setOnClickListener{
//
//        }

    }



}