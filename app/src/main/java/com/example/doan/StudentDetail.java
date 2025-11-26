package com.example.doan;

import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.doan.model.Room;
import com.example.doan.model.Student;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

public class StudentDetail extends AppCompatActivity {
    ImageView imgAvatar;
    EditText edtName, edtBirthday, edtMssv, edtPhone, edtAddress;
    Button btnExit, btnSave, btnChoose;

    DatabaseHelper db;
    Student student;
    int studentId;
    Uri imageUri;
    String savedImagePath;

    ActivityResultLauncher<Intent> pickImageLauncher;
    GoogleMap googleMap;

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
        setupMap();
        loadData();
        addEvent();
    }
    private void setupMap() {
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(gMap -> {
                googleMap = gMap;
                showStudentLocationOnMap();  // gọi hiển thị vị trí sau khi map load
            });
        }
    }
    private void showStudentLocationOnMap() {

        if (googleMap == null || student == null) return;
        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        String address = student.getAddress();
        if (address == null || address.isEmpty()) return;

        try {
            Geocoder geocoder = new Geocoder(this);
            List<Address> list = geocoder.getFromLocationName(address, 1);

            if (list != null && !list.isEmpty()) {

                double lat = list.get(0).getLatitude();
                double lng = list.get(0).getLongitude();

                LatLng pos = new LatLng(lat, lng);

                googleMap.clear();
                googleMap.addMarker(new MarkerOptions().position(pos).title(student.getAddress()));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 20f));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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
            String avatar = cursor.getString(cursor.getColumnIndexOrThrow("avatar"));

            student = new Student(id, name, birthday, mssv, gender, phone, address, avatar, roomId);

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
        if (student.getAvatar() != null && !student.getAvatar().isEmpty()) {
            File imgFile = new File(student.getAvatar());
            if (imgFile.exists()) {
                imgAvatar.setImageURI(Uri.fromFile(imgFile));
            } else {
                imgAvatar.setImageResource(android.R.drawable.sym_def_app_icon);
            }
        } else {
            imgAvatar.setImageResource(android.R.drawable.sym_def_app_icon);
        }
        showStudentLocationOnMap();
    }
    private void addControl() {
        imgAvatar = findViewById(R.id.imgAvatar);
        edtName = findViewById(R.id.edtName);
        edtBirthday = findViewById(R.id.edtBirthday);
        edtMssv = findViewById(R.id.edtMssv);
        edtPhone = findViewById(R.id.edtPhone);
        edtAddress = findViewById(R.id.edtAddress);
        btnExit = findViewById(R.id.btnExit);
        btnSave = findViewById(R.id.btnSave);
        btnChoose = findViewById(R.id.btnChooseImage);
        db = new DatabaseHelper(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Nam", "Nữ"}
        );
        studentId = getIntent().getIntExtra("STUDENT_ID", -1);
        imageUri = null;
        savedImagePath = null;

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        imgAvatar.setImageURI(imageUri);
                    }
                }
        );

    }
    private void addEvent() {
        btnExit.setOnClickListener(v -> finish());
        btnChoose.setOnClickListener(v -> chooseImage());
        btnSave.setOnClickListener(v -> updateStudent());
    }
    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    private void updateStudent() {
        String name = edtName.getText().toString().trim();
        String birthday = edtBirthday.getText().toString().trim();
        String mssv = edtMssv.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();

        // Nếu chọn ảnh mới → lưu vào thư mục app
        if (imageUri != null) {
            savedImagePath = copyImageToInternalStorage(imageUri);
            student.setAvatar(savedImagePath);
        }

        // Cập nhật dữ liệu
        student.setName(name);
        student.setBirthday(birthday);
        student.setMssv(mssv);
        student.setPhone(phone);
        student.setAddress(address);

        boolean success = db.updateStudent(student);

        if (success) {
            Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
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