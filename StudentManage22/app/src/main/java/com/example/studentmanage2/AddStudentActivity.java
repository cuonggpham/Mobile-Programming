package com.example.studentmanage2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class AddStudentActivity extends AppCompatActivity {

    private EditText editMSSV, editName, editPhone, editAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        editMSSV = findViewById(R.id.edit_mssv);
        editName = findViewById(R.id.edit_name);
        editPhone = findViewById(R.id.edit_phone);
        editAddress = findViewById(R.id.edit_address);
        Button btnAdd = findViewById(R.id.btn_add);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mssv = editMSSV.getText().toString();
                String name = editName.getText().toString();
                String phone = editPhone.getText().toString();
                String address = editAddress.getText().toString();

                if (!mssv.isEmpty() && !name.isEmpty()) {
                    String newStudent = "MSSV: " + mssv + " - Họ tên: " + name;
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("newStudent", newStudent);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
            }
        });
    }
}
