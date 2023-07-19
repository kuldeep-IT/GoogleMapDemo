package com.rkuldeep.googlemapdemo

import android.Manifest
import android.app.PendingIntent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.rkuldeep.googlemapdemo.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapLongClickListener {
    //Jai Dada
    //Om namah Siddham
    private lateinit var mMap: GoogleMap
    lateinit var mainBinding: ActivityMainBinding

    private var locationArrayList: ArrayList<LatLng>? = null

    val sabarmati = LatLng(23.0396, 72.5660)
    val sidiSaiyad = LatLng(23.0262, 72.5802)
    val kankaria = LatLng(22.9902, 72.6095)
    val adalaj = LatLng(23.1663, 72.5698)
    val vastrapur = LatLng(23.0384,  72.5184)

    private var geofenceHelper: GeofenceHelper? = null
    private var geofencingClient: GeofencingClient? = null

    private val GEOFENCE_RADIUS = 2000f
    private val GEOFENCE_ID = "SOME_GEOFENCE_ID"

    private val FINE_LOCATION_ACCESS_REQUEST_CODE = 10001
    private val BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mMap) as SupportMapFragment
        mapFragment.getMapAsync(this)

        locationArrayList = ArrayList()
        locationArrayList!!.add(sabarmati)
        locationArrayList!!.add(sidiSaiyad)
        locationArrayList!!.add(kankaria)
        locationArrayList!!.add(adalaj)
        locationArrayList!!.add(vastrapur)

        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceHelper = GeofenceHelper(this);

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
      /*  val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.addMarker(MarkerOptions().position(LatLng(34.0, -151.0)).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))*/

        for (i in locationArrayList!!.indices){
            mMap.addMarker(MarkerOptions().position(locationArrayList!![i]).title("Marker"))
            mMap.animateCamera(CameraUpdateFactory.zoomTo(17f))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationArrayList!![i], 12f))
        }

//        getGeofencingRequest()

        enableUserLocation()
        mMap.setOnMapLongClickListener(this)

    }

    private fun getGeofencingRequest(): GeofencingRequest? {
        val geofenceList: ArrayList<Geofence> = ArrayList<Geofence>()

        // Iterate through your LatLng(s) to build the Geofence list
        for (coordinate in locationArrayList!!) {

            // Set up each Geofence with your corresponding values
            addGeofences(coordinate, GEOFENCE_RADIUS)

            geofenceList.add(
                Geofence.Builder()
                    .setRequestId("My Geofence ID") // A string to identify this geofence
                    .setCircularRegion(
                        coordinate.latitude,
                        coordinate.longitude,
                        20f
                    )
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                    .build()
            )
        }
        val builder: GeofencingRequest.Builder = GeofencingRequest.Builder()

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)

        // Add the geofences to be monitored by geofencing service using the list we created.
        builder.addGeofences(geofenceList)
        return builder.build()
    }

    fun enableUserLocation(){
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED){
            mMap.isMyLocationEnabled = true
        } else{
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
            )){
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), FINE_LOCATION_ACCESS_REQUEST_CODE)

            } else{
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), FINE_LOCATION_ACCESS_REQUEST_CODE
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if (requestCode == FINE_LOCATION_ACCESS_REQUEST_CODE){
            if (grantResults.size > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                    ){
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.

                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), FINE_LOCATION_ACCESS_REQUEST_CODE)


                    return
                }
                mMap.isMyLocationEnabled = true
            }

            super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        }

        if (requestCode == BACKGROUND_LOCATION_ACCESS_REQUEST_CODE){
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"add pin with geofence",Toast.LENGTH_SHORT).show()
            } else{
                Toast.makeText(this,"Background location is mendatory", Toast.LENGTH_SHORT).show()

                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION), FINE_LOCATION_ACCESS_REQUEST_CODE)

            }
        }

    }

    private fun addGeofences(latLng: LatLng, radius: Float) {

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), FINE_LOCATION_ACCESS_REQUEST_CODE)


            return
        }

        var geofence = geofenceHelper!!.getGeofence(
            GEOFENCE_ID,
            latLng,
            radius,
            Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_DWELL or Geofence.GEOFENCE_TRANSITION_EXIT
        )

        var geofencingRequest = geofenceHelper!!.getGeofencingRequest(geofence)

        val pendingIntent: PendingIntent = geofenceHelper?.getPIntent()!!

        // Pass the request built with the getGeofencingRequest() function to the Geofencing Client

        geofencingClient?.addGeofences(geofencingRequest, pendingIntent)
                ?.addOnSuccessListener(OnSuccessListener<Void?> {
                    Log.d(
                        "CHECK_CHECK",
                        "onSuccess: Geofence Added..."
                    )
                })
                ?.addOnFailureListener(OnFailureListener { e ->
                    val errorMessage: String = geofenceHelper!!.getErrorString(e)
                    Log.d("CHECK_CHECK", "onFailure: $errorMessage")
                })

    }

    override fun onMapLongClick(latlang: LatLng) {
        if (Build.VERSION.SDK_INT >= 29){
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED){
                handleMapLongClick(latlang)
            } else{
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )){
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf( Manifest.permission.ACCESS_BACKGROUND_LOCATION), BACKGROUND_LOCATION_ACCESS_REQUEST_CODE
                    )
                } else{
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf( Manifest.permission.ACCESS_BACKGROUND_LOCATION), BACKGROUND_LOCATION_ACCESS_REQUEST_CODE
                    )
                }
            }
        } else{
            handleMapLongClick(latlang)
        }
    }

    private fun handleMapLongClick(latlang: LatLng) {
        addMarker(latlang)
        addCircle(latlang, GEOFENCE_RADIUS)
        addGeofences(latlang,GEOFENCE_RADIUS)
    }


    private fun addMarker(latLng: LatLng) {
        val markerOptions = MarkerOptions().position(latLng)
        mMap.addMarker(markerOptions)
    }

    private fun addCircle(latLng: LatLng, radius: Float) {
        val circleOptions = CircleOptions()
        circleOptions.center(latLng)
        circleOptions.radius(radius.toDouble())
        circleOptions.strokeColor(Color.argb(255, 255, 0, 0))
        circleOptions.fillColor(Color.argb(64, 255, 0, 0))
        circleOptions.strokeWidth(4f)
        mMap.addCircle(circleOptions)
    }

}