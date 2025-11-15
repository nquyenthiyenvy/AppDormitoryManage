package com.example.doan;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.doan.Adapter.RoomAdapter;
import com.example.doan.model.Room;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listRooms;
    DatabaseHelper dbHelper;

    ArrayList <Room> rooms;
    RoomAdapter roomAdapter;
    EditText edtRoomName;
    RadioButton rbMale, rbFemale;
    Room selectedRoom = null;
    Button btnClear, btnSave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        addControl();
        addEvent();
        loadData();
    }

    private void loadData() {
        rooms.clear();
        Cursor cursor = dbHelper.getAllRooms();
        ArrayList<String> roomList = new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("nameRoom"));
                String type = cursor.getString(cursor.getColumnIndexOrThrow("typeRoom"));
                rooms.add(new Room(id,name,type));
            }while(cursor.moveToNext());
        }
        cursor.close();
        if (listRooms.getHeaderViewsCount() == 0) {
            View header = getLayoutInflater().inflate(R.layout.item_room_header, null);
            listRooms.addHeaderView(header);
        }
        listRooms.setAdapter(roomAdapter);
        roomAdapter.notifyDataSetChanged();

    }

    private void addControl() {
        listRooms = findViewById(R.id.listRooms);
        dbHelper = new DatabaseHelper(this);
        rooms = new ArrayList<>();
        roomAdapter = new RoomAdapter(this, rooms);
        edtRoomName = findViewById(R.id.edtRoomName);
        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);
        btnSave = findViewById(R.id.btnSave);
        btnClear = findViewById(R.id.btnClear);
    }
    private void addEvent() {
        // click chọn phòng
        listRooms.setOnItemClickListener((parent, view, position, id) -> {
            int realPosition = position - listRooms.getHeaderViewsCount();
            if(realPosition >= 0 && realPosition < rooms.size()) {
                selectedRoom = rooms.get(realPosition);
                // Hiển thị dữ liệu lên form
                edtRoomName.setText(selectedRoom.getName());
                if(selectedRoom.getType().equals("Nam")) {
                    rbMale.setChecked(true);
                } else {
                    rbFemale.setChecked(true);
                }
            }
        });
        // Thêm với sửa
        btnSave.setOnClickListener(v -> {
            String name = edtRoomName.getText().toString();
            String type = rbMale.isChecked() ? "Nam" : "Nữ";
            Room room = new Room(name,type);
            if(selectedRoom == null) {
                dbHelper.addRoom(room);
            } else {
                selectedRoom.setName(name);
                selectedRoom.setType(type);
                dbHelper.updateRoom(selectedRoom);
            }
            selectedRoom = null;
            edtRoomName.setText("");
            rbMale.setChecked(true);
            loadData();
        });
        btnClear.setOnClickListener(v -> {
            selectedRoom = null;
            edtRoomName.setText("");
            rbMale.setChecked(true);
        });
        listRooms.setOnItemLongClickListener((parent, view, position, id) -> {
            int realPosition = position - listRooms.getHeaderViewsCount();
            if (realPosition >= 0 && realPosition < rooms.size()) {
                Room roomToDelete = rooms.get(realPosition);
                new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this)
                        .setTitle("Xác nhận xóa")
                        .setMessage("Bạn có chắc muốn xóa phòng " + roomToDelete.getName() + "?")
                        .setPositiveButton("Có", (dialog, which) -> {
                            // Xóa khỏi database
                            boolean deleted = dbHelper.deleteRoom(roomToDelete.getId());
                            loadData();
                        })
                        .setNegativeButton("Không", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .show();
            }
            return true;
        });
    }
}