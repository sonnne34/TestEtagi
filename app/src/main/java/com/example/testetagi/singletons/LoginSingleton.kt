package com.example.testetagi.singletons

object LoginSingleton {
    var login : String = ""

    fun addLogin(mLogin : String){
        login = mLogin

    }
}