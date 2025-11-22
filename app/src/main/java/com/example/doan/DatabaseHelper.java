package com.example.doan;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.doan.model.Room;
import com.example.doan.model.Student;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "ktx.db";
    private static final int DB_VERSION = 10;
    private static final String TABLE_USERS = "users";

    private static final String TABLE_ROOMS = "rooms";

    private static final String TABLE_STUDENS = "students";

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

        // Bảng students
        db.execSQL("CREATE TABLE " + TABLE_STUDENS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "birthday DATE," +
                "avatar TEXT,"+
                "mssv TEXT, " +
                "gender TEXT, " +
                "phone TEXT, " +
                "address TEXT,"+
                "roomId INTEGER, " +
                "FOREIGN KEY(roomId) REFERENCES " + TABLE_ROOMS + "(id))");
        db.execSQL("INSERT INTO " + TABLE_STUDENS +
                " (name, birthday, mssv, gender, phone, address, roomId) " +
                "VALUES ('Nguyễn Văn A', '21-05-2003', 'DH52110001', 'Nam', '0123456789', 'Hà Nội', 1)");
        db.execSQL("INSERT INTO " + TABLE_STUDENS +
                " (name, birthday, mssv, gender, phone, address, roomId) " +
                "VALUES ('Nguyễn Văn C', '11-12-2003', 'DH52110003', 'Nam', '012345611', 'Đắk Lắk', 1)");
        db.execSQL("INSERT INTO " + TABLE_STUDENS +
                " (name, birthday, mssv, gender, phone, address, roomId) " +
                "VALUES ('Bùi Hữu D', '05-03-2003', 'DH52110004', 'Nam', '01234567u', 'Hải Dương', 1)");
        db.execSQL("INSERT INTO " + TABLE_STUDENS +
                " (name, birthday, mssv, gender, phone, address, roomId) " +
                "VALUES ('Nguyễn Văn A', '22-01-2003', 'DH52110005', 'Nam', '01234567uu', 'Hà Nội', 1)");
        db.execSQL("INSERT INTO " + TABLE_STUDENS +
                " (name, birthday, mssv, gender, phone, address, roomId) " +
                "VALUES ('Trần Thị B', '10-10-2004', 'DH52110002', 'Nữ', '0911222333', 'TP.HCM', 2)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROOMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENS);
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
    public boolean deleteRoom(int roomId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_STUDENS + " WHERE roomId = ?",
                new String[]{String.valueOf(roomId)}
        );
        cursor.moveToFirst();
        int studentCount = cursor.getInt(0);
        cursor.close();
        if (studentCount > 0) {
            return false;
        }
        SQLiteDatabase writableDB = this.getWritableDatabase();
        int rows = writableDB.delete(TABLE_ROOMS, "id = ?", new String[]{String.valueOf(roomId)});

        return rows > 0;
    }

    // LẤY DANH SÁCH SINH VIÊN
    public Cursor getStudentsByRoom(int roomId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_STUDENS + " WHERE roomId = ?",
                new String[]{String.valueOf(roomId)});
    }
    public Cursor getStudentsById(int studentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM " + TABLE_STUDENS + " WHERE id = ?",
                new String[]{String.valueOf(studentId)}
        );
    }
    public boolean addStudent(Student s) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", s.getName());
        values.put("birthday", s.getBirthday());
        values.put("mssv", s.getMssv());
        values.put("gender", s.getGender());
        values.put("phone", s.getPhone());
        values.put("address", s.getAddress());
        values.put("roomId", s.getRoomId());
        values.put("avatar", s.getAvatar());

        long result = db.insert(TABLE_STUDENS, null, values);

        return result != -1;
    }

}