package com.example.doan;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.doan.Adapter.StudentAdapter;
import com.example.doan.model.Room;
import com.example.doan.model.Student;

import java.util.ArrayList;

public class StudentList extends AppCompatActivity {
    DatabaseHelper dbHelper;
    ArrayList<Student> students;
    StudentAdapter adapter;
    ListView listStudents;
    String roomName;

    Student selectedStudent = null;
    int roomId;
    ImageButton imgBtnAdd;
    private static final int REQUEST_ADD_STUDENT = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        addControl();
        loadSV();
        addEvent();
    }



    private void loadSV() {
        students.clear();
        Cursor cursor = dbHelper.getStudentsByRoom(roomId);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String birthday = cursor.getString(cursor.getColumnIndexOrThrow("birthday"));
                String mssv = cursor.getString(cursor.getColumnIndexOrThrow("mssv"));
                String gender = cursor.getString(cursor.getColumnIndexOrThrow("gender"));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));
                String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
                String avatar = cursor.getString(cursor.getColumnIndexOrThrow("avatar"));
                int roomId = cursor.getInt(cursor.getColumnIndexOrThrow("roomId"));
                students.add(new Student(id, name, birthday, mssv, gender, phone, address,avatar ,roomId));
            } while (cursor.moveToNext());
        }

        cursor.close();
        if (listStudents.getHeaderViewsCount() == 0) {
            View header = getLayoutInflater().inflate(R.layout.student_list_header, null);
            TextView tvHeader = header.findViewById(R.id.tvHeaderRoomName);
            tvHeader.setText("Danh sách sinh viên - " + roomName);
            listStudents.addHeaderView(header);
        }
        adapter = new StudentAdapter(this, students);
        listStudents.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    private void addControl() {
        roomId = getIntent().getIntExtra("ROOM_ID", -1);
        roomName = getIntent().getStringExtra("ROOM_NAME");
        listStudents = findViewById(R.id.listStudents);
        dbHelper = new DatabaseHelper(this);
        students = new ArrayList<>();
        imgBtnAdd = findViewById(R.id.btnAddStudent);
    }
    private void addEvent() {
        handleStudentItemClick();
        hanldeAddStudentClick();
    }

    private void hanldeAddStudentClick() {
        imgBtnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(StudentList.this, AddStudentForm.class);
            intent.putExtra("ROOM_ID", roomId);
            startActivityForResult(intent,REQUEST_ADD_STUDENT);
        });
    }

    private void handleStudentItemClick(){
        listStudents.setOnItemClickListener((parent, view, position, id) -> {
            int realPosition = position - listStudents.getHeaderViewsCount();
            if(realPosition >= 0 && realPosition < students.size()) {
                selectedStudent = students.get(realPosition);
                Intent intent = new Intent(StudentList.this, StudentDetail.class);
                intent.putExtra("STUDENT_ID", selectedStudent.getId());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_STUDENT && resultCode == RESULT_OK) {
            loadSV();
        }
    }
}
