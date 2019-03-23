package com.example.pockemon_newer

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.lang.Exception

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private var mMap: GoogleMap?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        checkpermission()
        loadpockemon()
    }
    var ACCESSLOCATION=123
    fun checkpermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), ACCESSLOCATION)
                return
            }
        }


        GetUserLocation()
    }

    @SuppressLint("MissingPermission")
    fun GetUserLocation()
    {
        Toast.makeText(this,"user location access on",Toast.LENGTH_LONG).show()
        //TODO: Will implement later

        var myloaction=mylocationlistner()
        var locationmanager=getSystemService(Context.LOCATION_SERVICE) as LocationManager

        //locationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3,3f,myloaction);
        locationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3,3f,myloaction)

        var mythread=mythread()
        mythread().start()

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        when(requestCode){

            ACCESSLOCATION->{

                if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    GetUserLocation()
                }
                else
                {
                    Toast.makeText(this,"We cannot access to your location",Toast.LENGTH_LONG).show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


    }

    //get user location
    var location1:Location?=null
    inner class mylocationlistner:LocationListener
    {

        constructor()
        {
            location1= Location("Start")
            location1!!.latitude=0.0
            location1!!.longitude=0.0

        }
        override fun onLocationChanged(location: Location?) {
            location1=location
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onProviderEnabled(provider: String?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onProviderDisabled(provider: String?) {

        }

    }

    var oldlocation:Location?=null
    inner class mythread:Thread
    {
        constructor():super()
        {
            oldlocation= Location("Start")
            oldlocation!!.latitude=0.0
            oldlocation!!.longitude=0.0

        }
        override  fun run()
        {
            while(true)
            {
                try {

                    if(oldlocation!!.distanceTo(location1)==0f)
                    {
                        continue
                    }

                    oldlocation=location1
                    runOnUiThread {
                        mMap!!.clear()

                        //show me
                        val sydney = LatLng(location1!!.latitude, location1!!.longitude)
                        mMap!!.addMarker(
                            MarkerOptions()
                                .position(sydney)
                                .title("Me")
                                .snippet("Here is my location")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.mario))
                        )
                        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 14f))

                        //show pockemon

                        for(i in 0..listpockemons.size-1)
                        {
                            var newpockemon=listpockemons[i]

                            if(newpockemon.iscatch==false)
                            {
                                val pockemonloc = LatLng(newpockemon.location!!.latitude, newpockemon.location!!.longitude)
                                mMap!!.addMarker(
                                    MarkerOptions()
                                        .position(pockemonloc)
                                        .title(newpockemon.name!!)
                                        .snippet(newpockemon.des!!+", power:"+newpockemon!!.power)
                                        .icon(BitmapDescriptorFactory.fromResource(newpockemon.image!!))
                                )

                                if(location1!!.distanceTo(newpockemon.location)<2)
                                {
                                    newpockemon.iscatch=true
                                    listpockemons[i]=newpockemon
                                    playerpower+=newpockemon.power!!
                                    Toast.makeText(applicationContext,"yeah!! you just caught a new pockemon and your new power is"+playerpower,Toast.LENGTH_LONG).show()
                                }


                            }
                        }
                    }

                    Thread.sleep(1000)
                }
                catch (ex:Throwable)
                {

                }
            }
        }
    }


    var playerpower=0.0
    var listpockemons=ArrayList<pockemon>()
    fun loadpockemon()
    {
        listpockemons.add(pockemon(R.drawable.charmander,
            "Charmander", "Charmander living in japan", 55.0, 37.7789994893035, -122.401846647263))
        listpockemons.add(pockemon(R.drawable.bulbasaur,
            "Bulbasaur", "Bulbasaur living in usa", 90.5, 37.7949568502667, -122.410494089127))
        listpockemons.add(pockemon(R.drawable.squirtle,
            "Squirtle", "Squirtle living in iraq", 33.5, 37.7816621152613, -122.41225361824))




    }
}

//172.16.1.2

//acmid
//augmented123