package com.Team6.buski.quickstart.database

import android.Manifest
import androidx.lifecycle.ViewModelProvider
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.view.Menu
import android.view.MenuItem

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_busk.*
import androidx.lifecycle.*
import com.google.android.gms.maps.model.Marker


class BuskActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var buskViewModel: BuskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_busk)
        buskViewModel = ViewModelProviders.of(this).get(BuskViewModel::class.java)
        buskViewModel.mDatabase = FirebaseDatabase.getInstance().reference
        getUsers(buskViewModel.mDatabase)
        startLocation.setOnClickListener { view ->
            setLocation(buskViewModel.mDatabase)
            buskViewModel.addUsers()
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
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
        mMap.setOnMarkerClickListener(this)
        buskViewModel.map = mMap
        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        mMap.setMinZoomPreference(7f)
//        buskViewModel.scrollToCurrent()
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        if (p0?.tag == 1){
            p0.tag = 0
            startActivity(Intent(this@BuskActivity, ProfileActivity::class.java))
        }else{
            p0?.tag = 1
        }
        p0?.showInfoWindow()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val i = item.itemId
        if (i == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
            return true
        } else {
            return super.onOptionsItemSelected(item)
        }
    }

    private fun setLocation(firebaseData: DatabaseReference) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            val longitude = location.longitude
            val latitude = location.latitude
            buskViewModel?.currentUser.location = (latitude.toString() + ":" + longitude.toString() )
            firebaseData
                    .child("users")
                    .child(getUid())
                    .child("location")
                    .setValue(latitude.toString() + ":" + longitude.toString() )
            firebaseData
                    .child("users")
                    .child(getUid())
                    .child("active")
                    .setValue(buskViewModel?.currentUser.active.not() )
            buskViewModel?.currentUser.active = buskViewModel?.currentUser.active.not()
            when(buskViewModel?.currentUser.active){
                true->startLocation.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN))
                false->startLocation.setBackgroundTintList(ColorStateList.valueOf(Color.RED))
            }
            buskViewModel.scrollToCurrent()

        } else {
            println("hi")
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    1)
        }
    }

    private fun getUsers(firebaseData: DatabaseReference) {
        val menuListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue<User>(User::class.java) !!
                buskViewModel.currentUser = user
                setLocation(firebaseData)
                buskViewModel.scrollToCurrent()

            }
            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }
        firebaseData.child("users").child(getUid()).addListenerForSingleValueEvent(menuListener)


        val totalUsers = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                println(dataSnapshot)
                val users = mutableListOf<com.Team6.buski.quickstart.database.User>()
                dataSnapshot.children.mapNotNullTo(users) { it.getValue<com.Team6.buski.quickstart.database.User>(User::class.java) }
                buskViewModel.users = users
                for ( i in dataSnapshot.children){
                    print(i)
                }
                buskViewModel.addUsers()
            }
            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }
        firebaseData.child("users").addValueEventListener(totalUsers)
    }
    fun getUid(): String {
        return FirebaseAuth.getInstance().currentUser!!.uid
    }
}
