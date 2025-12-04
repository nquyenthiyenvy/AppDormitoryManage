package com.example.doan;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.doan.model.Student;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class AddStudentForm extends AppCompatActivity {
    ImageView imgAvtatar;
    Button btnExit, btnSave, btnChoose;
    EditText edtName, edtMssv, edtBirthday, edtPhone, edtAddress;


    Uri imageUri;
    DatabaseHelper db;
    String savedImagePath;
    ActivityResultLauncher<Intent> pickImageLauncher;
    int idRoom;
    String typeRoom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_student_form);
        Toolbar toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        addControl();
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
    private void addControl() {
        db = new DatabaseHelper(this);

        imgAvtatar = findViewById(R.id.imgAvatar);
        btnChoose = findViewById(R.id.btnChooseImage);
        btnSave = findViewById(R.id.btnSave);
        btnExit = findViewById(R.id.btnCancel);

        edtName = findViewById(R.id.edtName);
        edtBirthday = findViewById(R.id.edtBirthday);
        edtMssv = findViewById(R.id.edtMssv);
        edtPhone = findViewById(R.id.edtPhone);
        edtAddress = findViewById(R.id.edtAddress);
        imageUri = null;
        savedImagePath  = null;

        idRoom = getIntent().getIntExtra("ROOM_ID", -1);
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        imgAvtatar.setImageURI(imageUri);
                    }
                }
        );
        typeRoom = getIntent().getStringExtra("ROOM_TYPE");
    }
    private void addEvent() {
        btnChoose.setOnClickListener(v -> chooseImage());
        btnSave.setOnClickListener(v -> saveStudent());
    }
    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }
    private void saveStudent() {
        String name = edtName.getText().toString().trim();
        String birthday = edtBirthday.getText().toString().trim();
        String mssv = edtMssv.getText().toString().trim();
        String gender = typeRoom;
        String phone = edtPhone.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();

        if (name.isEmpty() || mssv.isEmpty() || birthday.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ!", Toast.LENGTH_SHORT).show();
            return;
        }

        // copy ảnh vào folder riêng của app
        if (imageUri != null) {
            savedImagePath = copyImageToInternalStorage(imageUri);
        }

        // Lưu DB
        Student s = new Student(0, name, birthday, mssv, gender, phone, address, savedImagePath, idRoom);
        boolean success = db.addStudent(s);
        if (success) {
            Toast.makeText(this, "Thêm sinh viên thành công!", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Trùng mã số sinh viên!", Toast.LENGTH_SHORT).show();
        }
    }
    private String copyImageToInternalStorage(Uri uri) {
        try {
            File dir = new File(getFilesDir(), "student_avatars");
            if (!dir.exists()) dir.mkdirs();

            String fileName = "avatar_" + System.currentTimeMillis() + ".jpg";
            File newFile = new File(dir, fileName);

            InputStream in = getContentResolver().openInputStream(uri);
            FileOutputStream out = new FileOutputStream(newFile);

            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }

            in.close();
            out.close();

            return newFile.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}