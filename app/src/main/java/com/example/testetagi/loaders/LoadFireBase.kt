package com.example.testetagi.loaders

import android.content.Context
import android.util.Log
import com.example.testetagi.adapters.EstateObjectAdapter
import com.example.testetagi.models.CategoryEstateObjectModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class LoadFireBase(context: Context) {

    fun loadApartments(model : ArrayList<CategoryEstateObjectModel>, adapter: EstateObjectAdapter) {

        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Etagi")

        myRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                    val value = dataSnapshot.getValue(CategoryEstateObjectModel::class.java)!!

                    Log.d("LOAD", "value = $value")

                    model.add(value)

                updateAdapter(model, adapter)
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
            }
        })
    }

    private fun updateAdapter(list : ArrayList<CategoryEstateObjectModel>, adapter: EstateObjectAdapter) {
        adapter.setupAdapter(list)
    }
}