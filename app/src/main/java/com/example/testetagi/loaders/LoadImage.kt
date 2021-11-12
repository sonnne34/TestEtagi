package com.example.testetagi.loaders

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.testetagi.models.EstateObjectCatModel
import com.google.firebase.storage.FirebaseStorage

class LoadImage {

    fun loadImageApartments(context: Context, estateObjectModel: EstateObjectCatModel, image: ImageView) {

        val glide = Glide.with(context)

        if (estateObjectModel.Items?.ImageForUri == null) {

            val storage = FirebaseStorage.getInstance()
            val storageRef = estateObjectModel.Items?.let { storage.getReferenceFromUrl(it.Image) }

            storageRef?.downloadUrl?.addOnSuccessListener { uri ->

                estateObjectModel.Items!!.ImageForUri = uri
                val img = glide.load(uri)
                img.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                img.centerCrop().into(image)
            }
        } else {

            val img = glide.load(estateObjectModel.Items!!.ImageForUri)
            img.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            img.centerCrop().into(image)
        }
    }
}