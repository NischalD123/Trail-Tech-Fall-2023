package edu.vt.smarttrail.surveyactivity


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.opencsv.CSVReaderBuilder
import edu.vt.smarttrail.GMapsActivity
import edu.vt.smarttrail.R
import edu.vt.smarttrail.UidSingleton
import java.io.IOException
import java.util.*

internal class FullMapActivity : AppCompatActivity(), OnMapReadyCallback {
    companion object {
        const val TAG = "FullMapActivity.kt"
    }

    private lateinit var mMap: GoogleMap

    data class Shelter(
        val name: String,
        val latitude: Double,
        val longitude: Double
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fullmapsactivity)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Get the button view
        val fullMapButton = findViewById<Button>(R.id.back_button)

        // Set a click listener for the button
        fullMapButton.setOnClickListener {
            startActivity(Intent(this, GMapsActivity::class.java))
        }


    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

// Fetch data from Firebase Realtime Database
        val database = Firebase.database
        val uid = UidSingleton.getUid()
        val surveyResponsesRef = database.reference.child("users_responses").child(uid)
        surveyResponsesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Clear existing markers and polylines on the map


                // Loop through each child node of the "users_responses" node
                var previousLocation: LatLng? = null
                for (childSnapshot in snapshot.children) {
                    // Get the latitude, longitude, and timestamp values
                    val latitude = childSnapshot.child("latitude").getValue(Double::class.java)
                    val longitude = childSnapshot.child("longitude").getValue(Double::class.java)
                    val timestamp = childSnapshot.child("timestamp").getValue(String::class.java)

                    // Create a LatLng object from the latitude and longitude values
                    val location = LatLng(latitude ?: 0.0, longitude ?: 0.0)

                    // Create a MarkerOptions object for the marker
                    val markerOptions = MarkerOptions()
                        .position(location)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                        .title("Survey response $uid")
                        .snippet(timestamp)

                    // Add the marker to the map
                    mMap.addMarker(markerOptions)

                    // If this is not the first location, add a polyline from the previous location to the current location
                    if (previousLocation != null) {
                        val polylineOptions = PolylineOptions()
                            .add(previousLocation, location)
                            .color(Color.BLUE)
                        mMap.addPolyline(polylineOptions)
                    }

                    // Set the current location as the previous location for the next iteration
                    previousLocation = location
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })

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

        //get width and height to current display screen
        val width = resources.displayMetrics.widthPixels
        val height = resources.displayMetrics.heightPixels

        // 20% padding
        val padding = (width * 0.10).toInt()

        //set latlong bounds
        mMap!!.setLatLngBoundsForCameraTarget(bounds)

        //move camera to fill the bound to screen
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding))


        // enable my location
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
        } else {
            // request permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                edu.vt.smarttrail.GMapsActivity.Companion.MY_PERMISSIONS_REQUEST_LOCATION
            )
        }

        // load shelters data from CSV file
        val shelters: List<Shelter> = try {
            val csvReader = CSVReaderBuilder(resources.openRawResource(edu.vt.smarttrail.R.raw.atshelters).bufferedReader())
                .withSkipLines(1) // Skip the header row
                .build()
            csvReader.readAll().map {
                Shelter(
                    it[3], // Shelter Name
                    it[15].toDouble(), // Latitude
                    it[16].toDouble() // Longitude
                )
            }
        } catch (e: IOException) {
            emptyList()
        }
//adds all the shelter locations
        shelters.forEach { shelter ->
            val markerOptions = MarkerOptions()
                .position(LatLng(shelter.latitude, shelter.longitude))
                .title(shelter.name)
                .icon(BitmapDescriptorFactory.fromResource(edu.vt.smarttrail.R.drawable.home))
            googleMap.addMarker(markerOptions)
        }

    }




}

