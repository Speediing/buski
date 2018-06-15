package com.Team6.buski.quickstart.database

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.Team6.buski.quickstart.database.models.User
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DatabaseReference

class BuskViewModel: ViewModel() {
   lateinit var mDatabase: DatabaseReference
    lateinit var users: List<com.Team6.buski.quickstart.database.User>
    lateinit var currentUser: com.Team6.buski.quickstart.database.User
    lateinit var map: GoogleMap

    fun printUsers(){
        println(users)
    }

    fun addUsers(){
        map.clear()
        for (user in users){
            val latlng = user.location?.split(":") ?: listOf("1.0", "1.0")
            if(user.active) {
                val local = LatLng(latlng[0].toDouble(), latlng[1].toDouble())
                val marker = map.addMarker(MarkerOptions().position(local)
                        .title(user.username))
                marker.tag = user.username
            }
        }
    }
    fun scrollToCurrent(){
        val latlng = currentUser.location?.split(":") ?: listOf("1.0", "1.0")
        val local = LatLng(latlng[0].toDouble(), latlng[1].toDouble())
        map.moveCamera(CameraUpdateFactory.newLatLng(local))
    }

}