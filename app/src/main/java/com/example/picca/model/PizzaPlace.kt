package com.example.picca.model

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class PizzaPlace:ClusterItem{

    var addressStreet:String=""
    var addressStreetNo:String=""
    var geoLat:String=""
    var geoLng:String=""
    var openHours:String=""
    var openTo:String=""
    var phoneNo:String=""
    var name:String=""
    var id:Long=0
    var pictures1:String=""
    override fun getSnippet(): String {
        return name
    }

    override fun getTitle(): String {
        return name
    }

    override fun getPosition(): LatLng {
        return LatLng(geoLat.toDouble(),geoLng.toDouble())
    }

}