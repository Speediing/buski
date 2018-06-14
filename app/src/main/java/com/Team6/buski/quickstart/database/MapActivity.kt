package com.Team6.buski.quickstart.database

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import com.Team6.buski.quickstart.database.models.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.HashMap
import android.location.LocationManager
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat


class MapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        // [START initialize_database_ref]
        val mDatabase = FirebaseDatabase.getInstance().reference
        // [END initialize_database_ref]
        val fab = findViewById<View>(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { view ->
        setLocation(mDatabase)
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        initSaladMenu(mDatabase)
    }

    private fun setLocation(firebaseData: DatabaseReference) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            val longitude = location.longitude
            val latitude = location.latitude
            println(latitude)
            println(longitude)
            //
        firebaseData
                .child("users")
                .child(getUid())
                .child("location")
                .setValue(latitude.toString() + ":" + longitude.toString() )
        } else {
            println("hi")
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    1)
        }
    }

    private fun initSaladMenu(firebaseData: DatabaseReference) {
        val menuListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                println(dataSnapshot)
                val user = dataSnapshot.getValue<User>(User::class.java) !!
                println(user)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }
        firebaseData.child("users").child(getUid()).addListenerForSingleValueEvent(menuListener)


        val totalUsers = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                println(dataSnapshot)
                val users = mutableListOf<User>()
                dataSnapshot.children.mapNotNullTo(users) { it.getValue<User>(User::class.java) }
                println(users)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }
        firebaseData.child("users").addListenerForSingleValueEvent(totalUsers)
//
//        firebaseData
//                .child("users")
//                .child(getUid())
//                .child("location")
//                .setValue("your moms house")
    }

    fun getUid(): String {
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
        }
    }

}

data class Salad(
        val name: String = "",
        val description: String = "",
        var uuid: String = "")

data class User(
        val email: String = "",
        val username: String = "",
        val location:String = "")
