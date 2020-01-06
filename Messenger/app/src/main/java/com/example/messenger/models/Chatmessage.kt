package com.example.messenger.models

import org.bson.types.ObjectId
import org.litote.kmongo.id.WrappedObjectId

data class Chatmessage(val _id: String, val text: String, val fromId:String, val toId:String, val timestamp: Long)
