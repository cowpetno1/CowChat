package com.example.messenger.models

import android.os.Parcel
import android.os.Parcelable

data class User(val _id : String, val userName:String, val userEmail:String,
                val userPassword:String):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(p0: Parcel?, p1: Int) {
        p0!!.writeString(_id)
        p0!!.writeString(userName)
        p0!!.writeString(userEmail)
        p0!!.writeString(userPassword)

    }

    override fun describeContents(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}
