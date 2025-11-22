package com.example.doan;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.doan.model.Room;
import com.example.doan.model.Student;

public class StudentDetail extends AppCompatActivity {
    ImageView imgAvatar;
    EditText edtName, edtBirthday, edtMssv, edtPhone, edtAddress;
    Spinner spGender;
    Button btnExit, btnSave;

    DatabaseHelper db;
    Student student;
    int studentId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        addControl();
        loadData();
    }
    private void loadData() {
        Cursor cursor = db.getStudentsById(studentId);
        if(cursor.moveToFirst()){
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String birthday = cursor.getString(cursor.getColumnIndexOrThrow("birthday"));
                String mssv = cursor.getString(cursor.getColumnIndexOrThrow("mssv"));
                String gender = cursor.getString(cursor.getColumnIndexOrThrow("gender"));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));
                String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
                int roomId = cursor.getInt(cursor.getColumnIndexOrThrow("roomId"));
                student = new Student(id, name, phone, gender, mssv, birthday, address, roomId);
        }
        cursor.close();
        if (student == null) {
            Toast.makeText(this, "Student not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        edtName.setText(student.getName());
        edtBirthday.setText(student.getBirthday());
        edtMssv.setText(student.getMssv());
        edtPhone.setText(student.getPhone());
        edtAddress.setText(student.getAddress());
        if (student.getGender().equalsIgnoreCase("Nam")) {
            spGender.setSelection(0);
        } else {
            spGender.setSelection(1);
        }
        imgAvatar.setImageResource(android.R.drawable.sym_def_app_icon);
    }
    private void addControl() {
        imgAvatar = findViewById(R.id.imgAvatar);
        edtName = findViewById(R.id.edtName);
        edtBirthday = findViewById(R.id.edtBirthday);
        edtMssv = findViewById(R.id.edtMssv);
        edtPhone = findViewById(R.id.edtPhone);
        edtAddress = findViewById(R.id.edtAddress);
        spGender = findViewById(R.id.spGender);
        btnExit = findViewById(R.id.btnExit);
        btnSave = findViewById(R.id.btnSave);
        db = new DatabaseHelper(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Nam", "Nữ"}
        );
        spGender.setAdapter(adapter);
        studentId = getIntent().getIntExtra("STUDENT_ID", -1);
    }
}