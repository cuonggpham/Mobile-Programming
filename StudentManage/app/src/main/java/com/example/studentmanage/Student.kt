package com.example.studentmanage

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Student(
    val id: String,
    val name: String,
    val phone: String,
    val address: String
) : Parcelable
