package com.example.wildtracker.model

import java.util.*

class ChatMessage(toString: String, displayName: String?) {
    var messageText: String? = null
    var messageUser: String? = null
    var messageTime: Long = 0

    fun ChatMessage(){

    }
    fun ChatMessage( messageText: String,  messageUser:String) {
        this.messageText = messageText;
        this.messageUser = messageUser;

        // Initialize to current time
        messageTime =  Date().getTime();
    }
    @JvmName("getMessageText1")
    fun getMessageText(): String? {
        return messageText
    }

    @JvmName("setMessageText1")
    fun setMessageText(messageText: String?) {
        this.messageText = messageText
    }

    @JvmName("getMessageUser1")
    fun getMessageUser(): String? {
        return messageUser
    }

    @JvmName("setMessageUser1")
    fun setMessageUser(messageUser: String?) {
        this.messageUser = messageUser
    }

    @JvmName("getMessageTime1")
    fun getMessageTime(): Long {
        return messageTime
    }

    @JvmName("setMessageTime1")
    fun setMessageTime(messageTime: Long) {
        this.messageTime = messageTime
    }
}