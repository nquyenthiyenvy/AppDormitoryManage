package com.example.doan.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.doan.R;
import com.example.doan.model.Student;

import java.util.ArrayList;

public class StudentAdapter extends ArrayAdapter<Student> {

    private Context context;
    private ArrayList<Student> students;

    public StudentAdapter(Context context, ArrayList<Student> students) {
        super(context, 0, students);
        this.context = context;
        this.students = students;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Nullable
    @Override
    public Student getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_student, parent, false);
        }
        Student s = students.get(position);
        ImageView imgAvatar = view.findViewById(R.id.imgAvatar);
        TextView tvName = view.findViewById(R.id.tvStudentName);
        TextView tvMssv = view.findViewById(R.id.tvStudentMSSV);
        TextView tvBirthday = view.findViewById(R.id.tvStudentBirthday);
        TextView tvGender = view.findViewById(R.id.tvStudentGender);
        TextView tvPhone = view.findViewById(R.id.tvStudentPhone);
        TextView tvAddress = view.findViewById(R.id.tvStudentAddress);
        tvName.setText("Name: " + s.getName());
        tvMssv.setText("MSSV: " + s.getMssv());
        tvBirthday.setText("Birthday: " + s.getBirthday());
        tvGender.setText("Gender: " + s.getGender());
        tvPhone.setText("Phone: " + s.getPhone());
        tvAddress.setText("Address: " + s.getAddress());
        String avatarPath = s.getAvatar();

        if (avatarPath != null && !avatarPath.isEmpty()) {
            Bitmap bitmap = BitmapFactory.decodeFile(avatarPath);
            if (bitmap != null) {
                imgAvatar.setImageBitmap(bitmap);
            } else {
                imgAvatar.setImageResource(android.R.drawable.sym_def_app_icon);
            }
        } else {
            imgAvatar.setImageResource(android.R.drawable.sym_def_app_icon);
        }
        return view;
    }
}
