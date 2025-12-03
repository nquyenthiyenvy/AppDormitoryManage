package com.example.doan;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;

public class     LoginActivity extends AppCompatActivity {
    private EditText edtUsername, edtPassword;
    private Button btnLogin;
    private TextView tvError, tvForgotPassword;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        addControls();  
        addEvents();
    }

    private void addEvents() {
        btnLogin.setOnClickListener(v -> xuLyDangNhap());

        tvForgotPassword.setOnClickListener(v -> {
            new AlertDialog.Builder(LoginActivity.this)
                    .setTitle("Quên mật khẩu?")
                    .setMessage("Tài khoản mặc định:\nadmin / 123\n\nLiên hệ quản trị viên để đổi mật khẩu.")
                    .setPositiveButton("OK", null)
                    .show();
        });
    }
    private void xuLyDangNhap() {
        String user = edtUsername.getText().toString().trim();
        String pass = edtPassword.getText().toString().trim();

        if (user.isEmpty() || pass.isEmpty()) {
            showError("Vui lòng nhập đầy đủ!");
            return;
        }

        if (db.checkLogin(user, pass)) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));

        } else {
            showError("Tên đăng nhập hoặc mật khẩu không đ!");
        }
    }
    private void showError(String msg) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.main), msg, Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(Color.parseColor("#E53935"));
        snackbar.setTextColor(Color.WHITE);
        snackbar.show();
    }

    private void addControls() {
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvError = findViewById(R.id.tvError);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        db = new DatabaseHelper(this);
    }
}