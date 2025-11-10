package com.example.doan;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
        loadData();
    }

    private void loadData() {
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
        View header = getLayoutInflater().inflate(R.layout.item_room_header, null);
        listRooms.addHeaderView(header);
        listRooms.setAdapter(roomAdapter);
    }

    private void addControl() {
        listRooms = findViewById(R.id.listRooms);
        dbHelper = new DatabaseHelper(this);
        rooms = new ArrayList<>();
        roomAdapter = new RoomAdapter(this, rooms);
    }
}