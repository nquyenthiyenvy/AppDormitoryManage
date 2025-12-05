package com.example.doan;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.doan.Adapter.StudentAdapter;
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
    private static final int REQUEST_UPDATE_STUDENT = 101;
    String typeRoom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_list);
        Toolbar toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(this.getString(R.string.title_toolbar));
        toolbar.setTitleTextColor(Color.WHITE);
        Drawable overflowIcon = toolbar.getOverflowIcon();
        if (overflowIcon != null) {
            overflowIcon.setTint(Color.WHITE);
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        addControl();
        loadSV();
        addEvent();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_back,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.back) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        typeRoom = getIntent().getStringExtra("ROOM_TYPE");
    }
    private void addEvent() {
        handleStudentItemClick();
        hanldeAddStudentClick();
        handleDeleteStudent();
    }

    private void handleDeleteStudent() {
        listStudents.setOnItemLongClickListener((parent, view, position, id) -> {
            int realPosition = position - listStudents.getHeaderViewsCount();

            if (realPosition < 0 || realPosition >= students.size()) return true;

            selectedStudent =  students.get(realPosition);
            new androidx.appcompat.app.AlertDialog.Builder(StudentList.this)
                    .setTitle("Xác nhận xóa")
                    .setMessage("Bạn có chắc muốn xóa sinh viên " + selectedStudent.getName() + "?")
                    .setPositiveButton("Có", (dialog, which) -> {
                        boolean result = dbHelper.deleteStudent(selectedStudent.getId());
                        if(result){
                            Toast.makeText(StudentList.this, "Xóa thành công!", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(StudentList.this, "Xóa thất bại!", Toast.LENGTH_SHORT).show();
                        }
                        loadSV();
                    })
                    .setNegativeButton("Không", null)
                    .show();
            return true;
        });
    }

    private void hanldeAddStudentClick() {
        imgBtnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(StudentList.this, AddStudentForm.class);
            intent.putExtra("ROOM_ID", roomId);
            intent.putExtra("ROOM_TYPE",typeRoom);
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
                startActivityForResult(intent, REQUEST_UPDATE_STUDENT);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REQUEST_ADD_STUDENT|| requestCode == REQUEST_UPDATE_STUDENT) && resultCode == RESULT_OK) {
            loadSV();
        }
    }
}
