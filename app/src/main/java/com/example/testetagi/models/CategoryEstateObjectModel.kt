package com.example.testetagi.models

import java.util.HashMap

class CategoryEstateObjectModel(var CategoryName : String) {
    constructor(): this(CategoryName = String())

    var Items: HashMap<String, EstateObjectModel> = HashMap()
}