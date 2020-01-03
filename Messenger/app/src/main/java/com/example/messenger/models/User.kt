package com.example.messenger.models

class User(val _id : String, val userName:String, val profileImageUrl:String){
    constructor() :this("","","")
}
