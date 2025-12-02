package com.example.studentmanage2;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> studentList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the student list
        studentList = new ArrayList<>();
        studentList.add("MSSV: 001 - Họ tên: Nguyễn Văn A");
        studentList.add("MSSV: 002 - Họ tên: Trần Thị B");

        // Set up the ListView
        ListView listView = findViewById(R.id.student_list_view);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, studentList);
        listView.setAdapter(adapter);

        // Handle item clicks
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(MainActivity.this, StudentDetailActivity.class);
            intent.putExtra("studentInfo", studentList.get(position));
            intent.putExtra("position", position);
            startActivityForResult(intent, 2);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_student) {
            Intent intent = new Intent(this, AddStudentActivity.class);
            startActivityForResult(intent, 1);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String newStudent = data.getStringExtra("newStudent");
            studentList.add(newStudent);
            adapter.notifyDataSetChanged();
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            int position = data.getIntExtra("position", -1);
            String updatedStudent = data.getStringExtra("updatedStudent");
            if (position != -1) {
                studentList.set(position, updatedStudent);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
