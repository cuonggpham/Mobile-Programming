package com.example.studentmanage

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var studentAdapter: StudentAdapter
    private val studentList = mutableListOf<Student>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val fab = findViewById<FloatingActionButton>(R.id.fab)

        studentAdapter = StudentAdapter(studentList) { student ->
            val intent = Intent(this, StudentDetailActivity::class.java)
            intent.putExtra("student", student)
            startActivity(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = studentAdapter

        fab.setOnClickListener {
            val intent = Intent(this, AddStudentActivity::class.java)
            startActivityForResult(intent, REQUEST_ADD_STUDENT)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ADD_STUDENT && resultCode == RESULT_OK) {
            val newStudent = data?.getParcelableExtra<Student>("newStudent")
            newStudent?.let {
                studentList.add(it)
                studentAdapter.notifyDataSetChanged()
            }
        }
    }

    companion object {
        const val REQUEST_ADD_STUDENT = 1
    }
}
