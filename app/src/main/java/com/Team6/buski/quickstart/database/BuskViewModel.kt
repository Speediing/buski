package com.Team6.buski.quickstart.database

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DatabaseReference

class BuskViewModel: ViewModel() {
   lateinit var mDatabase: DatabaseReference
    lateinit var users: List<User>


    fun printUsers(){
        println(users)
    }

}