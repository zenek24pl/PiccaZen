package com.example.picca.model

class Ingredients {
    var name:String=""
    var price:String=""
    var id:String=""
    var category:String=""
    var checked:Boolean=false

    constructor(name: String, price: String,  checked: Boolean,category: String) {
        this.name = name
        this.price = price
        this.checked = checked
        this.category=category
    }
    constructor()
}
