package edu.vt.smarttrail

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import edu.vt.smarttrail.surveyactivity.FullMapActivity

class CustomLocation {
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    constructor() // Default constructor required for Firebase

    constructor(latitude: Double, longitude: Double) {
        this.latitude = latitude
        this.longitude = longitude
    }
}

open class GMapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mapFragment: SupportMapFragment
    private lateinit var mMap: GoogleMap

    companion object {
        const val MY_PERMISSIONS_REQUEST_LOCATION = 123
        private const val LOCATION_PATH = "locations"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gmaps)

        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Get the button view
        val fullMapButton = findViewById<Button>(R.id.back_button)

        // Set a click listener for the button
        fullMapButton.setOnClickListener {
            val newIntent = Intent(this, FullMapActivity::class.java)
            startActivity(newIntent)
        }
    }

    override fun onMapReady(map: GoogleMap) {
        mMap = map

        // enable my location button
        mMap.uiSettings.isMyLocationButtonEnabled = true

        // get latlong for corners for specified place
        val one = LatLng(46.17, -85.0) //sw corner
        val two = LatLng(34.0, -68.0) //ne corner
        val builder = LatLngBounds.Builder()

        //add them to builder
        builder.include(one)
        builder.include(two)
        val bounds = builder.build()

        //get width and height to the current display screen
        val width = resources.displayMetrics.widthPixels
        val height = resources.displayMetrics.heightPixels

        // 20% padding
        val padding = (width * 0.10).toInt()

        // set latlong bounds
        mMap.setLatLngBoundsForCameraTarget(bounds)

        // move camera to fill the bound to the screen
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding))

        // enable my location
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
        } else {
            // request permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQUEST_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted, enable my location
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return
                    }
                    mMap.isMyLocationEnabled = true
                } else {
                    // permission denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}
