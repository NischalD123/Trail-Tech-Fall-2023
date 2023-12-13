package edu.vt.smarttrail

import android.app.Activity
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast

class ScreentimePermissionActivity : AppCompatActivity() {
    private val USAGE_ACCESS_REQUEST = 123  // You can use any request code you prefer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screentime_permission)

        // Creates an action bar, where the back button is.
        val actionBar = supportActionBar

        // Creates a back button.
        actionBar!!.setDisplayHomeAsUpEnabled(true)

        val requestButton = findViewById<View>(R.id.request_screentime) as Button
        val agreeCheck = findViewById<View>(R.id.screentime_check) as CheckBox
        val readCheck = findViewById<View>(R.id.screentime_check_terms) as CheckBox
        requestButton.setOnClickListener {
            if (agreeCheck.isChecked && readCheck.isChecked) {
                // Start the usage access settings activity
                startActivityForResult(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), USAGE_ACCESS_REQUEST)
            } else {
                Toast.makeText(this@ScreentimePermissionActivity, "Please indicate you agree to the terms and policies.", Toast.LENGTH_LONG).show()
            }
        }

        val backtoLocationsPermission = findViewById<View>(R.id.back_to_request_location) as Button
        backtoLocationsPermission.setOnClickListener {
            val intent = Intent(this, edu.vt.smarttrail.LocationPermissionActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == USAGE_ACCESS_REQUEST) {
            if (isUsageAccessPermissionGranted()) {
                // User granted the usage access permission
                Toast.makeText(this, "App usage time is being enabled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isUsageAccessPermissionGranted(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), packageName)
        return mode == AppOpsManager.MODE_ALLOWED
    }

}
