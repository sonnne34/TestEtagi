package com.example.testetagi.singletons

import android.util.Log
import com.example.testetagi.listeners.EventListeners
import com.example.testetagi.models.EstateObjectCatModel

object EstateSingleton {

    var estateItem : ArrayList<EstateObjectCatModel> = ArrayList()
    var listeners: ArrayList<EventListeners> = ArrayList()

    fun addEstate(item : EstateObjectCatModel){
        var boolean = true
        for(i in estateItem){
            if(i.Items?.Adress == item.Items?.Adress){
                boolean = false
            }
        }
        if (boolean){
            estateItem.add(item)
        }
    }

    fun prise(): Double {
        var sumPrise = 0.0
        for (i in estateItem) {
            val area = i.Items?.Area!!.toDouble()
            val cost = i.Items?.Cost!!.toDouble()
            sumPrise += (cost / area)
        }
        return sumPrise
    }

    fun notifyTwo() {
        for (listener in listeners) {
            listener.updateRR()
            Log.d("Test", "notifiTwo = $listener")
        }
    }

    fun subscribe(listener: EventListeners) {
        listeners.add(listener)
        Log.d("Test", "Listener = $listener")
    }
}