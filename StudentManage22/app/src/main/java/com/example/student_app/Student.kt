package com.example.student_app

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "students")
data class Student(
    var name: String,
    @PrimaryKey
    var id: String,
    var phone: String,
    var address: String
) : Serializable
