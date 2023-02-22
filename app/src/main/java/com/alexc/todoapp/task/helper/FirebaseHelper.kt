package com.alexc.todoapp.task.helper

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class FirebaseHelper {

    companion object{
        fun getDataBase() = FirebaseDatabase.getInstance().reference

        fun getAuth() = FirebaseAuth.getInstance()

        fun getIdUser() = getAuth().uid

        fun isAutenticated() = getAuth().currentUser != null

        fun validError(error: String): Int{
            return when {
                error.contains("")->{
                    1
                }
                else -> {
                    0
                }
            }
        }

    }
}