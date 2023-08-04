package com.example.chamberlyapp

data class ChatMessage(
    val content: String,
    val messageType: String,
    var senderName: String,
    val senderUid: String,

    )
{
    // Add a no-argument constructor
    constructor() : this("", "", "", "")
}