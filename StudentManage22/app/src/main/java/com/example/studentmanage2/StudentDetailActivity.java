package com.example.studentmanage2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class StudentDetailActivity extends AppCompatActivity {

    private EditText editMSSV, editName;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_detail);

        editMSSV = findViewById(R.id.edit_mssv);
        editName = findViewById(R.id.edit_name);
        Button btnUpdate = findViewById(R.id.btn_update);
        Button btnDelete = findViewById(R.id.btn_delete);

        // Retrieve student info from intent
        Intent intent = getIntent();
        String studentInfo = intent.getStringExtra("studentInfo");
        position = intent.getIntExtra("position", -1);

        if (studentInfo != null) {
            String[] parts = studentInfo.split(" - ");
            editMSSV.setText(parts[0].replace("MSSV: ", ""));
            editName.setText(parts[1].replace("Họ tên: ", ""));
        }

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String updatedMSSV = editMSSV.getText().toString();
                String updatedName = editName.getText().toString();
                if (!updatedMSSV.isEmpty() && !updatedName.isEmpty()) {
                    String updatedStudent = "MSSV: " + updatedMSSV + " - Họ tên: " + updatedName;
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("updatedStudent", updatedStudent);
                    resultIntent.putExtra("position", position);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("position", position);
                setResult(RESULT_CANCELED, resultIntent);
                finish();
            }
        });
    }
}
