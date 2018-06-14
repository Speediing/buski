package com.Team6.buski.quickstart.database

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DatabaseReference

class BuskViewModel: ViewModel() {
   lateinit var mDatabase: DatabaseReference
    lateinit var users: List<User>
    lateinit var map: GoogleMap

    fun printUsers(){
        println(users)
    }

    fun addUsers(){
        for (user in users){
            val latlng = user.location.split(":")
            val local = LatLng(latlng[0].toDouble(), latlng[1].toDouble())
            map.addMarker(MarkerOptions().position(local)
                    .title(user.username)
                    )
            map.moveCamera(CameraUpdateFactory.newLatLng(local))
        }
    }

}