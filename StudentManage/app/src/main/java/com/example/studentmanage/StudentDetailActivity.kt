package com.example.studentmanage

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class StudentDetailActivity : AppCompatActivity() {

    private lateinit var student: Student

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_detail)

        val idEditText = findViewById<EditText>(R.id.idEditText)
        val nameEditText = findViewById<EditText>(R.id.nameEditText)
        val phoneEditText = findViewById<EditText>(R.id.phoneEditText)
        val addressEditText = findViewById<EditText>(R.id.addressEditText)
        val updateButton = findViewById<Button>(R.id.updateButton)

        student = intent.getParcelableExtra("student")!!

        idEditText.setText(student.id)
        nameEditText.setText(student.name)
        phoneEditText.setText(student.phone)
        addressEditText.setText(student.address)

        updateButton.setOnClickListener {
            val updatedStudent = Student(
                id = idEditText.text.toString(),
                name = nameEditText.text.toString(),
                phone = phoneEditText.text.toString(),
                address = addressEditText.text.toString()
            )

            val resultIntent = Intent().apply {
                putExtra("updatedStudent", updatedStudent)
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }
}
