package com.example.chamberlyapp

import com.google.firebase.firestore.FieldValue

data class ChatInfo(
    val host:String = "",
    val messages: Map<String, String> = emptyMap(),
    val timestamp:Any = FieldValue.serverTimestamp(),
    val title:String = "",
    val users:UsersData = UsersData()
)
data class UsersData(
    val members:Map<String, String> = mapOf()
)