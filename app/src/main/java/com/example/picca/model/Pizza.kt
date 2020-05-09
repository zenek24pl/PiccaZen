package com.example.picca.model

import com.google.firebase.firestore.DocumentId

class Pizza {
    var descr: String = ""
    @DocumentId
    var id:String=""
    var name:String=""
    var count:Int=0
    var ingr:ArrayList<String> = arrayListOf()
    var price:Double=0.0
    var souce:String=""
    var plate:String=""
    var size:String =""
}
