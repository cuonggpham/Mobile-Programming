package com.example.studentmanage

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class AddStudentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_student)

        val idEditText = findViewById<EditText>(R.id.idEditText)
        val nameEditText = findViewById<EditText>(R.id.nameEditText)
        val phoneEditText = findViewById<EditText>(R.id.phoneEditText)
        val addressEditText = findViewById<EditText>(R.id.addressEditText)
        val addButton = findViewById<Button>(R.id.addButton)

        addButton.setOnClickListener {
            val id = idEditText.text.toString()
            val name = nameEditText.text.toString()
            val phone = phoneEditText.text.toString()
            val address = addressEditText.text.toString()

            if (id.isNotBlank() && name.isNotBlank() && phone.isNotBlank() && address.isNotBlank()) {
                val newStudent = Student(id, name, phone, address)
                val resultIntent = Intent().apply {
                    putExtra("newStudent", newStudent)
                }
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }
    }
}
