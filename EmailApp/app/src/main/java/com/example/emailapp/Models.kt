package com.example.emailapp

data class Email(
    val senderName: String,
    val subject: String,
    val preview: String,
    val time: String,
    val avatarColor: Int,
    val isImportant: Boolean = false,
    val isStarred: Boolean = false
)

