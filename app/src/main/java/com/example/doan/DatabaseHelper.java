package com.example.doan;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.doan.model.Room;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "ktx.db";
    private static final int DB_VERSION = 3;
    private static final String TABLE_USERS = "users";

    private static final String TABLE_ROOMS = "rooms";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng users
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, password TEXT)");

        // Thêm tài khoản admin mặc định
        db.execSQL("INSERT INTO " + TABLE_USERS + " (username, password) VALUES ('admin', '123')");

        // Bảng room
        db.execSQL("CREATE TABLE " + TABLE_ROOMS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nameRoom TEXT, " +
                "typeRoom TEXT)");
        db.execSQL("INSERT INTO " + TABLE_ROOMS + " (nameRoom, typeRoom) VALUES ('Phòng 101', 'Nam')");
        db.execSQL("INSERT INTO " + TABLE_ROOMS + " (nameRoom, typeRoom) VALUES ('Phòng 201', 'Nữ')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROOMS);
        onCreate(db);
    }

    // KIỂM TRA ĐĂNG NHẬP
    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_USERS + " WHERE username = ? AND password = ?",
                new String[]{username, password}
        );
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
    // LẤY DANH SÁCH PHÒNG
    public Cursor getAllRooms() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_ROOMS, null);
    }
    // Thêm phòng
    public boolean addRoom(Room room) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("nameRoom", room.getName());
        values.put("typeRoom", room.getType());

        long result = db.insert(TABLE_ROOMS, null, values);
        return result != -1;
    }

    // Cập nhật thông tin phòng
    public boolean updateRoom(Room room) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("nameRoom", room.getName());
        values.put("typeRoom", room.getType());

        // Cập nhật dựa trên id
        int rowsAffected = db.update(TABLE_ROOMS, values, "id = ?", new String[]{String.valueOf(room.getId())});

        return rowsAffected > 0;
    }

    // Xóa phòng
    public boolean deleteRoom(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_ROOMS + " WHERE id = ?",
                new Object[]{id});
        return true;
    }


}