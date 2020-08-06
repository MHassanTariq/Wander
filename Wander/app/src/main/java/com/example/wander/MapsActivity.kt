package com.example.wander

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import java.util.*
import java.util.jar.Manifest

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private val REQUEST_LOCATION_PERMISSION = 1
    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun isLocationGranted() : Boolean{
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation(map : GoogleMap) {
        if(isLocationGranted())
            map.isMyLocationEnabled = true
        else
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(android.Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
    }

    private fun setMapLongClick(map: GoogleMap) {
        map.setOnMapLongClickListener {usersLocation ->
            val zoomLevel = 15f
            val snippet = String.format(
                Locale.getDefault(),
                "Lat: %1$.5f, Long: %2.5f",
                usersLocation.latitude, usersLocation.longitude
            )
            map.addMarker(
                MarkerOptions()
                    .position(usersLocation) // setting position of marker
                    .title("Your location") // setting title of marker
                    .snippet(snippet) // setting description of marker
                    .icon(BitmapDescriptorFactory //setting icon to marker
                        .defaultMarker(BitmapDescriptorFactory //using default marker
                            .HUE_BLUE))) //setting blue color to marker
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(usersLocation, zoomLevel))
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val myLocation = LatLng(31.520370, 74.358747)
        val zoomLevel = 20f
        map.addMarker(MarkerOptions().position(myLocation).title("My Current Location"))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, zoomLevel))
        setGroundOverlay(map, myLocation)
        setMapLongClick(map)
        setPoiClick(map)
        setMapStyle(map)
        enableMyLocation(map)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {
        if( requestCode == REQUEST_LOCATION_PERMISSION)
            if(grantResults.contains(PackageManager.PERMISSION_GRANTED))
                enableMyLocation(map)
    }

    private fun setGroundOverlay(map: GoogleMap, homeLocation : LatLng) {
        val overlaySize = 10f
        val androidOverlay = GroundOverlayOptions()
            .image(BitmapDescriptorFactory.fromResource(R.drawable.android))
            .position(homeLocation, overlaySize)
        map.addGroundOverlay(androidOverlay)
    }

    private fun setMapStyle(map : GoogleMap) {
        try {
            val success = map.setMapStyle(MapStyleOptions
                .loadRawResourceStyle(this, R.raw.map_style))
            if(!success)
                Log.d("MainActivity", "Style parsing failed.")
        }
        catch (e : Resources.NotFoundException){
            Log.d("MainActivity", "Error : ", e)
        }
    }

    private fun setPoiClick(map: GoogleMap) {
        map.setOnPoiClickListener{poi ->
            map.addMarker(MarkerOptions().title(poi.name).position(poi.latLng)).showInfoWindow()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflator = menuInflater
        inflator.inflate(R.menu.map_options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.normal_map -> {
                map.mapType = GoogleMap.MAP_TYPE_NORMAL
                return true
            }
            R.id.hybrid_map -> {
                map.mapType = GoogleMap.MAP_TYPE_HYBRID
                return true
            }
            R.id.terrain_map -> {
                map.mapType = GoogleMap.MAP_TYPE_TERRAIN
                return true
            }
            R.id.satellite_map -> {
                map.mapType = GoogleMap.MAP_TYPE_SATELLITE
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}