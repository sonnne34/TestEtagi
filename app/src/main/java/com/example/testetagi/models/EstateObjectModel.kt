package com.example.testetagi.models

import android.net.Uri

class EstateObjectModel(var Adress: String, var Cost: String, var Area: String, var Image: String,
                        var ImageForUri: Uri?, var Rooms: String, var Price: String, var Floor: String, var AdressId: String) {
    constructor(): this(Adress = String(), Cost = String(), Area = String(), Image = String(),
        ImageForUri = null, Rooms = String(), Price = String(), Floor = String(), AdressId = String())
}